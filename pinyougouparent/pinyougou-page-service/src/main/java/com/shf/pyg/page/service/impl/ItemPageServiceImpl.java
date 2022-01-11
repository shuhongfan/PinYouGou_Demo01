package com.shf.pyg.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shf.pyg.mapper.TbGoodsDescMapper;
import com.shf.pyg.mapper.TbGoodsMapper;
import com.shf.pyg.mapper.TbItemCatMapper;
import com.shf.pyg.mapper.TbItemMapper;
import com.shf.pyg.page.service.ItemPageService;
import com.shf.pyg.pojo.TbGoods;
import com.shf.pyg.pojo.TbGoodsDesc;
import com.shf.pyg.pojo.TbItem;
import com.shf.pyg.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer FreeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

//    获取生成路径
    @Value("${pagedir}")
    private String pageDir;

    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            Configuration configuration = FreeMarkerConfigurer.getConfiguration();
//        获取模板
            Template template = configuration.getTemplate("item.ftl");
//        构建数据模型
            HashMap dataModel = new HashMap();

//        查找商品对象
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);

//        查询商品扩展对象
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);

//            一级分类名称
            String itemcat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemcat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemcat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("itemcat1",itemcat1);
            dataModel.put("itemcat2",itemcat2);
            dataModel.put("itemcat3",itemcat3);

//            SKU列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
//            按照是否默认倒序排序  为了让默认SKU排在第一位
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);

//            FileWriter out = new FileWriter(new File(pageDir + goodsId + ".html"));
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pageDir + goodsId + ".html"), "UTF-8");
            template.process(dataModel,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
