package com.shf.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map search(Map searchMap) {
        HashMap resultMap = new HashMap();

//        1.查询列表数据
        resultMap.putAll(searchList(searchMap));

//        2.查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

//        3.查询品牌和规格列表
        if (!"".equals(searchMap.get("category"))){
            resultMap.putAll(searchBrandAndSpecList((String) searchMap.get("category")));
        } else {
            if (categoryList.size()>0){
                resultMap.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }

        return resultMap;
    }

    /**
     * 列表查询
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap){
        HashMap resultMap = new HashMap();

//        0.去掉空格
        String keyword = (String) searchMap.get("keywords");
        keyword=keyword.replace(" ","");
        searchMap.put("keywords",keyword);

//        1.关键字搜索
//        SimpleQuery query = new SimpleQuery("*:*");
//        高亮查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        String keywords = (String) searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);

//        2.商品分类筛选
        if (!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
//            FilterQuery过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

//        3.品牌筛选
        if (!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

//        4.规格筛选
        if (searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");

            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

//        5.按价格筛选
        if (!"".equals(searchMap.get("price"))){
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")){ // 最低价格不等于0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){ // 如果价格有上限
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

//        6.分页查询
//        页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
//        页大小
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }

        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

//        7. 排序规则
        String sortType = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (sortType!=null && !"".equals(sortType)){
            if (sortType.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }

            if (sortType.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

//        8. 高亮
//        设置高亮列
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
//        设置高亮选项
        query.setHighlightOptions(highlightOptions);

//        根据条件进行查询
//        ScoredPage<TbItem> itemPage = solrTemplate.queryForPage(query, TbItem.class);
//        得到高亮页对象
        HighlightPage<TbItem> itemPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

//        得到高亮入口集合
        List<HighlightEntry<TbItem>> highlighted = itemPage.getHighlighted();
//        遍历高亮入口对象
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
//            得到实体类 注意:title字段没有高亮效果
            TbItem item = highlightEntry.getEntity();

            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if (highlights.size() >0 && highlights.get(0).getSnipplets().size()>0){
//                设置高亮片段
                List<String> snipplets = highlights.get(0).getSnipplets();
                item.setTitle(snipplets.get(0));
            }
        }

        resultMap.put("rows", itemPage.getContent());
//        总条数
        resultMap.put("total",itemPage.getTotalElements());
//        总页数
        resultMap.put("totalPages",itemPage.getTotalPages());

        return resultMap;
    }

    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */
    public List<String> searchCategoryList(Map searchMap){
        ArrayList<String> list = new ArrayList<>();

//        spring data solr分组查询  相当于sql中的group by
        SimpleQuery query = new SimpleQuery("*:*");
        String keywords = (String) searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);  // where item_keyword=keywords

//        设置分组选项  group by item_category
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        
//        得到分页对象
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        
//        得到分组结果对象
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        
//        得到分组入口页对象
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        
//        得到分组入口集合对象
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> groupEntry : content) {
            list.add(groupEntry.getGroupValue());
        }

        return list;
    }

    /**
     * 根据商品分类名称查询品牌和规格列表
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        HashMap map = new HashMap();

//        1.根据商品分类名称得到模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null){
//            2.查询品牌列表
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);

//            3.查询规格列表
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }

    /**
     * 导入数据
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除
     * @param goodsIds
     */
    @Override
    public void deleteByGoodsIds(Long[] goodsIds) {
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
    }
}
