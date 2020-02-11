package com.changgou.file.util;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/***
 * 工具类  用于封装 fastdfs的client的文件操作
 * @author ljh
 * @packagename com.changgou.util
 * @version 1.0
 * @date 2019/12/29
 */
public class FastDFSClient {

    static{
        try {
            //获取类路径下的配置文件
            ClassPathResource classPathResource = new ClassPathResource("fastdfs_client.conf");
            ClientGlobal.init(classPathResource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //文件上传
    public static String[] upload(FastDFSFile file) throws Exception{
        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件

        //3.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer 对象
        StorageServer storageServer = null;
        //6 创建storageClient 对象（操作图片的API CURD）  上传图片即可
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //参数1  指定文件的字节数组
        //参数2 指定 图片的扩展名  不要带点
        //参数3  指定的图片的元数据 比如：像素大小 图片的大小 拍摄日期 拍摄的作者 .........
        NameValuePair[] meta_list = new NameValuePair[]{
                new NameValuePair(file.getAuthor()),
                new NameValuePair(file.getName()) ,
                new NameValuePair(file.getSize())
        };

        String[] jpgs = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);


        return jpgs;// [0] = group1  // [1]=M00/00/00/wKjThF4II-2AL4BeAACAThdn_1U404.jpg
    }

    //下载

    public static byte[] downFile(String groupName,String remoteFileName) throws Exception{
        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件
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
        byte[] group1s = storageClient.download_file(groupName, remoteFileName);

        return group1s;
    }

    /**
     *  /**
     * delete file from storage server
     *
     * @param groupName      the group name of storage server
     * @param remoteFileName filename on storage server
     * @return 0 for success, none zero for fail (error code)
     */

    public static int deleteFile(String groupName,String remoteFileName) throws Exception{

        //1.创建一个配置文件 配置服务器的地址（ip和port）
        //2.加载配置文件
        //3.创建trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageServer 对象
        StorageServer storageServer = null;
        //6 创建storageClient 对象（操作图片的API CURD）  删除
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        int group1 = storageClient.delete_file(groupName, remoteFileName);
        if (group1 == 0) {
            System.out.println("成功");
        } else {
            System.out.println("不成功");
        }
        return group1;
    }

    /**
     * 获取文件所在的服务器的数组信息（ip,port....）
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName){
        try {
            //创建TrackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient获取TrackerServer对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取服务信息
            return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getTrackerUrl(){
        try {
            //创建TrackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient获取TrackerServer对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Tracker地址  http://192.168.211.132:8080
             return "http://"+trackerServer.getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
