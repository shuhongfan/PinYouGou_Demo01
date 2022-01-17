package com.shf.demo;

import org.csource.fastdfs.*;

public class Test {
    public static void main(String[] args) throws Exception {
//        1.加载配置文件
        ClientGlobal.init("E:\\DEMO\\PinYouGou_Demo01\\fastDFS-Demo\\src\\main\\resources\\fdfs_client.conf");
//        2.创建TrackerClient
        TrackerClient trackerClient = new TrackerClient();
//        3.得到TrackerServer(调度服务器)
        TrackerServer trackerServer = trackerClient.getConnection();
//        4.声明StrageServer
        StorageServer storageServer =null;
//        5.创建StorageClient
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);

        String[] strings = storageClient.upload_file("C:\\Users\\shuho\\Desktop\\SHF.png", "png", null);

        for (String string : strings) {
            System.out.println(string);
        }
    }
}
