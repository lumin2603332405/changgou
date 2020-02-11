package com.changgou.file.controller;

import com.changgou.file.util.FastDFSClient;
import com.changgou.file.util.FastDFSFile;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.file.controller
 * @version 1.0
 * @date 2019/12/29
 */

@RestController
@CrossOrigin//支持跨域
public class UploadController {

    /**
     * 前端 发送请求 给后天
     * 后台 返回前端 上传到fastdfs的文件的路径
     */

    @PostMapping("/upload")
    public String upload(MultipartFile file){
        //ctr +alt + T + 6
        try {
            //1.判断是否为空
            if(!file.isEmpty()){
                //2.获取字节数组,获取原来的文件的名称，获取原来的文件的扩展名 存储到fastdfs上
                FastDFSFile fdfsfile = new FastDFSFile(
                        file.getOriginalFilename(),//原文件名  1234.jpg
                        file.getBytes(),//字节数组
                        StringUtils.getFilenameExtension(file.getOriginalFilename())// 得到 jpg
                );
                String[] upload = FastDFSClient.upload(fdfsfile); // [0] = group1  // [1]=M00/00/00/wKjThF4II-2AL4BeAACAThdn_1U404.jpg


                //3.返回一个上传图片的路径 拼接字符串 http://192.168.211.132:8080/group1/M00/00/00/wKjThF4IG3SAeE-pAACAThdn_1U239.jpg

                String realpath = FastDFSClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1];

                return realpath;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
