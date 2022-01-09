package com.shf.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.ScoredPage;

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

//        1.关键字搜索
        SimpleQuery query = new SimpleQuery("*:*");
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

//        根据条件进行查询
        ScoredPage<TbItem> itemPage = solrTemplate.queryForPage(query, TbItem.class);
        resultMap.put("rows", itemPage.getContent());

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
}
