package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.annotation.MySearchAnnotation;
import com.changgou.search.pojo.SkuInfo;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 自定义查询到的结果的映射
 * @author ljh
 * @packagename com.changgou.search.service.impl
 * @version 1.0
 * @date 2020/1/5
 */
@Service
public class MySearchResultMapper implements SearchResultMapper {

    //自定义 结果的映射 ： 1.结果的封装 2.获取高亮的数据
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
        //1.获取当前的页的记录

        List<T> content = new ArrayList<T>();


        //获取数据 设置值到content中
        SearchHits hits = response.getHits();

        if (hits == null || hits.getTotalHits() <= 0) {
            return new AggregatedPageImpl<T>(content);
        }

        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();//pojo对应的JSON数据（没有高亮）
            //SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
            // 转换为对象
            T t = JSON.parseObject(sourceAsString, clazz);
            //T t = JSON.parseObject(sourceAsString, clazz);

            // 判断类上是否有自定义注解
            boolean flagAnnotation = clazz.isAnnotationPresent(MySearchAnnotation.class);

            if (flagAnnotation) {
                // 有注解
                // 获取对象中被自定义注解注释的字段
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(MySearchAnnotation.class)) {
                        // 该字段被自定义注解注释
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        HighlightField highlightField = highlightFields.get(field.getName());
                        if (highlightField != null) {
                            StringBuffer sb = new StringBuffer();
                            //高亮碎片
                            for (Text text : highlightField.fragments()) {
                                sb.append(text.string());//text.string() 就是高亮的数据
                            }

                            try {
                                // 获取反射对象的所有公私有方法
                                for (Method method : clazz.getDeclaredMethods()) {
                                    // 判断
                                    if (method.getName().equalsIgnoreCase("set" + field.getName())) {
                                        // 执行set方法，将高亮的数据替换掉原来没有高亮的数据
                                        method.invoke(t,sb.toString());
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //skuInfo.setName(sb.toString());//高亮的数据替换掉原来没有高亮的数据
                        }
                    }
                }
            }
            //content.add((T) skuInfo);
            content.add(t);
        }

        //2. 获取分页的对象

        //3.获取总记录数

        //4.获取聚合的结果
        Aggregations aggregations = response.getAggregations();

        //5.获取游标的ID
        String scrollId = response.getScrollId();

        return new AggregatedPageImpl<T>(content, pageable, response.getHits().getTotalHits(), aggregations, scrollId);
    }
}
