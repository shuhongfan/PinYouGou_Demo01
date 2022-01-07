package com.shf.pyg.solrutil;

import com.alibaba.fastjson.JSON;
import com.shf.pyg.mapper.TbItemMapper;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 导入数据
     */
    public void importData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> items = tbItemMapper.selectByExample(example);
        for (TbItem item : items) {
            System.out.println(item.getTitle()+"   "+item.getPrice());
            Map specMap = JSON.parseObject(item.getSpec());
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importData();
    }
}
