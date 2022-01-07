package com.shf.pyg.demo;

import com.shf.pyg.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-solr.xml" })
public class TestSolr {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 添加一条数据
     */
    @Test
    public void addSolr(){
        TbItem item = new TbItem();
        item.setId(1L);
        item.setTitle("华为手机");
        item.setPrice(new BigDecimal(4000));
        item.setBrand("华为");
        item.setSeller("华为旗舰店武汉分店");
        item.setCategory("手机");

        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    /**
     * 查找一条数据
     */
    @Test
    public void testFindOne(){
        TbItem item = solrTemplate.getById(1L, TbItem.class);
        System.out.println(item);
        System.out.println(item.getTitle());
        System.out.println(item.getPrice());
    }

    /**
     * 删除一条
     */
    @Test
    public void testDelete(){
        solrTemplate.deleteById(new String("1"));
        solrTemplate.commit();
    }

    /**
     * 批量添加
     */
    @Test
    public void addList(){
        ArrayList<TbItem> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(i+1L);
            item.setTitle("华为手机Meta"+i);
            item.setPrice(new BigDecimal(4000+i*10));
            item.setBrand("华为");
            item.setSeller("华为旗舰店武汉分店");
            item.setCategory("手机");
            items.add(item);
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    /**
     * 分页条件查询
     */
    @Test
    public void queryTemplate(){
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria=criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        query.setOffset(0);
        query.setRows(20);

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = page.getContent();
        for (TbItem item : content) {
            System.out.println(item.getTitle()+"   "+item.getPrice());
        }
        System.out.println("总记录数："+page.getTotalElements());
    }


    /**
     * 删除全部数据
     */
    @Test
    public void testDeleteAll(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
