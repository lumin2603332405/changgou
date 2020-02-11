package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.dao
 * @version 1.0
 * @date 2020/1/3
 */
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
