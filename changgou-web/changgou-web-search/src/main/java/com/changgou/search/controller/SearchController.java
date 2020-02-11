package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.controller
 * @version 1.0
 * @date 2020/1/6
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SkuFeign skuFeign;

    /**
     * for  test
     *
     * @return
     */
   /* @RequestMapping("/showSearchPage")
    public String showPage(){
        return "search";
    }*/
    @GetMapping(value = "/list")
    public String search(Model model, @RequestParam(required = false) Map<String, String> searchMap) {
        //1.接收页面传递的参数
        //2.调用feign 获取到ES的结果集
        Map resultmap = skuFeign.search(searchMap);
        //3.将结果集封装到model中
        model.addAttribute("result", resultmap);


        //设置数据回显
        model.addAttribute("searchMap", searchMap);



        //记录当前请求的url 路径 存储到model中

        String url = url(searchMap);

        model.addAttribute("url", url);


        //设置分页的记录  设置到model中比如：   pageinfo{ mypages:[1-5]}

        Page<SkuInfo> page = new Page<SkuInfo>(
                Long.valueOf(resultmap.get("total").toString()),
                Integer.valueOf(resultmap.get("pageNum").toString()),
                Integer.valueOf(resultmap.get("pageSize").toString())
        );
        model.addAttribute("page",page);

        //4.返回视图 （视图中获取model中的key的值渲染）

        return "search";
    }

    //  {keywords:"手机","category":"语言文字","brand":"华为"}
    private String url(Map<String, String> searchMap) {
        String url = "/search/list";
        if(searchMap!=null && searchMap.size()!=0){
            url+="?";
            for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                if(key.equalsIgnoreCase("pageNum")){
                    continue;
                }
                url+=key+"="+value+"&";
            }
            url=url.substring(0,url.length()-1);
        }
        // /search/list?keywords=手机&category=语言文字&
        return url;
    }


}
