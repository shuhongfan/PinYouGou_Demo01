package com.shf.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.SimpleQuery;
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

    @Override
    public Map search(Map searchMap) {
        HashMap resultMap = new HashMap();

//        1.查询列表数据
        resultMap.putAll(searchList(searchMap));

//        2.查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        return resultMap;
    }

    /**
     * 列表查询
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap){
        HashMap resultMap = new HashMap();

        SimpleQuery query = new SimpleQuery("*:*");
        String keywords = (String) searchMap.get("keywords");

        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);

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

}
