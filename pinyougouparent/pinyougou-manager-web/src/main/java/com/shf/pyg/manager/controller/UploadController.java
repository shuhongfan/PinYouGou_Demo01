package com.shf.pyg.manager.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * 图片上传
 */
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/upload")
    public void upload(@RequestParam("imgFile") MultipartFile[] imgFiles){
        try {
            PrintWriter out = response.getWriter();

            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            for (MultipartFile imgFile : imgFiles) {
//                获取上传文件的扩展名
                String originalFilename = imgFile.getOriginalFilename();
                int index = originalFilename.lastIndexOf(".");
                String extName = originalFilename.substring(index + 1);
                String path = client.uploadFile(imgFile.getBytes(), extName);

                HashMap<Object, Object> map = new HashMap<>();
                map.put("error",0);
                map.put("url",file_server_url+path);

                String json = JSON.toJSONString(map);
                System.out.println(json);
                out.println(json);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
