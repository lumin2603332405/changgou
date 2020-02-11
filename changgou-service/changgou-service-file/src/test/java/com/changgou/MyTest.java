package com.changgou;

import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2019/12/29
 */
public class MyTest {


    //上传图片
    @Test
    public void upload() throws Exception {
        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fastdfs_client.conf");
        //3.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer 对象
        StorageServer storageServer = null;
        //6 创建storageClient 对象（操作图片的API CURD）  上传图片即可
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //参数1  指定图片的本地路径
        //参数2 指定 图片的扩展名  不要带点
        //参数3  指定的图片的元数据 比如：像素大小 图片的大小 拍摄日期 拍摄的作者 .........
        String[] jpgs = storageClient.upload_file("C:\\Users\\admin\\Pictures\\Saved Pictures\\42932268_1492004444336.jpg", "jpg", null);

        //file_id
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    //下载图片
    @Test
    public void download() throws Exception {
        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fastdfs_client.conf");
        //3.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer 对象
        StorageServer storageServer = null;
        //6 创建storageClient 对象（操作图片的API CURD）  下载
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //指定组名
        //指定文件名
        byte[] group1s = storageClient.download_file("group1", "M00/00/00/wKjThF4IG3SAeE-pAACAThdn_1U239.jpg");
        //写入磁盘
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\1234.jpg"));
        fileOutputStream.write(group1s);
        fileOutputStream.close();
    }

    //删除图片
    @Test
    public void delete() throws Exception {
        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fastdfs_client.conf");
        //3.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer 对象
        StorageServer storageServer = null;
        //6 创建storageClient 对象（操作图片的API CURD）  删除
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        int group1 = storageClient.delete_file("group1", "M00/00/00/wKjThF4IG3SAeE-pAACAThdn_1U239.jpg");
        if (group1 == 0) {
            System.out.println("成功");
        } else {
            System.out.println("不成功");
        }

    }


    @Test
    public void getinfo() throws  Exception{
        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fastdfs_client.conf");
        //3.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer 对象
        StorageServer storageServer = null;
        //6 创建storageClient 对象（操作图片的API CURD）  删除
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        FileInfo group1 = storageClient.get_file_info("group1", "M00/00/00/wKjThF4IIF2AGzPmAACAThdn_1U488.jpg");
        System.out.println(group1.getCreateTimestamp()+":"+group1.getFileSize()+":"+group1.getSourceIpAddr());
    }

    //获取组相关的信息
    @Test
    public void getGroupInfo() throws Exception {
        //加载全局的配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fastdfs_client.conf");

        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();

        //组对应的服务器的地址  因为有可能有多个服务器.
        ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, "group1", "M00/00/00/wKjThF4IIF2AGzPmAACAThdn_1U488.jpg");
        for (ServerInfo serverInfo : group1s) {
            System.out.println(serverInfo.getIpAddr());
            System.out.println(serverInfo.getPort());
        }
    }



}
