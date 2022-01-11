package com.shf.demo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Demo {
    public static void main(String[] args) throws IOException, TemplateException {
//        1.创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
//        2.设置模板路径
        configuration.setDirectoryForTemplateLoading(new File("D:\\DEMO\\PinYouGou_Demo01\\freemarkerDemo\\src\\main\\resources"));
//        3.设置字符集
        configuration.setDefaultEncoding("utf-8");
//        4.获取模板
        Template template = configuration.getTemplate("demo.ftl");
//        5.构建数据模型
        HashMap map = new HashMap();
        map.put("name","漳卅");
        map.put("message","hello world");
        map.put("success",false);

//        商品列表
        ArrayList goodsList = new ArrayList();
        HashMap goods1 = new HashMap();
        goods1.put("name","苹果");
        goods1.put("price",2.5);
        goodsList.add(goods1);

        HashMap goods2 = new HashMap();
        goods2.put("name","香蕉");
        goods2.put("price",1.5);
        goodsList.add(goods2);

        HashMap goods3 = new HashMap();
        goods3.put("name","车厘子");
        goods3.put("price",49.96);
        goodsList.add(goods3);

        map.put("goodsList",goodsList);

        map.put("today", new Date());

        map.put("point",11123135);
        map.put("bbb",50);
//        6.创建writer对象
        FileWriter out = new FileWriter(new File("D:\\DEMO\\PinYouGou_Demo01\\freemarkerDemo\\src\\main\\resources\\demo.html"));
//        7.输出
        template.process(map,out);
//        8.关闭资源
        out.close();
    }
}
