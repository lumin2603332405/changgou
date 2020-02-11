package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import java.util.*;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.service.impl
 * @version 1.0
 * @date 2020/1/3
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void importES() {
        //1.调用商品微服务的feign 查询到符合条件的所有的SKU的数据
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<Sku> skuList = result.getData();
        //2.调用spring data elasticsearch的API 数据存储到ES服务器中
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuList), SkuInfo.class);

        //设置动态的设置规格的数据
        for (SkuInfo skuInfo : skuInfos) {
            // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            String spec = skuInfo.getSpec();
            Map<String, Object> map = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(map);
        }

        skuEsMapper.saveAll(skuInfos);
    }

    @Autowired
    private MySearchResultMapper mySearchResultMapper;

    @Override
    public Map search(Map<String, String> searchMap) {
        //1.获取关键字
        String keywords = searchMap.get("keywords");
        if (StringUtils.isEmpty(keywords)) {
            keywords = "华为";
        }
        //2.创建一个查询对象的 构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //设置分组查询的条件  设置商品分类的分组
        // terms---> group by
        //参数1 指定设置一个别名
        //参数2 指定分组的字段
        //size() 指定分组的容量的大小。默认是10
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(500));
        //设置分组查询的条件  设置品牌的分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(5000));
        // 设置分组的条件     设置规格的分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(500000));//???? todo


        //3.设置查询的条件  匹配查询
        //nativeSearchQueryBuilder.withIndices("skuinfo").withTypes("docs"); 查询所有
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));


        //4  过滤查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //4.1 商品分类的过滤查询  MUST MUST_NOT SHOULD FILETER
        String category = searchMap.get("category");
        if (!StringUtils.isEmpty(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", category));
        }


        //4.2 品牌的过滤查询  MUST MUST_NOT SHOULD FILETER
        String brand = searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", brand));
        }

        //4.3 规格的过滤查询  MUST MUST_NOT SHOULD FILETER


        //1.循环遍历map {keywords:"","category":"手机","brand":"三星","spec_网络制式":"移动3G"}
        for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            //2.判断是规格相关的key 和value的时候获取到值拼接 执行过滤查询
            if (key.startsWith("spec_")) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value));
            }

        }
        //4.4 价格区间的的过滤查询  MUST MUST_NOT SHOULD FILETER
        String price = searchMap.get("price");//0-500   // 3000-*
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            if (split[1].equalsIgnoreCase("*")) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            } else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
            }
        }


        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

        //设置分页
        //参数1 指定的是当前的页码  0 表示第一页
        //参数2 指定的每页显示的行
        String pageNum = searchMap.get("pageNum");
        Integer page = 1;
        Integer pageSize=40;
        if (StringUtils.isEmpty(pageNum)) {
            page = 1;
        } else {
            page = Integer.parseInt(pageNum);
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        nativeSearchQueryBuilder.withPageable(pageable);


        //设置排序
        String sortField = searchMap.get("sortField");//price
        String sortRule = searchMap.get("sortRule");//DESC/ASC
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
//            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equalsIgnoreCase("ASC") ? SortOrder.ASC : SortOrder.DESC));
        }


        //设置高亮的字段 设置前缀 和后缀

        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));



        //5.构建出查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        //6.执行查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(query, SkuInfo.class,mySearchResultMapper);
        //7.获取到结果集(总页数，总记录数，当前页的集合)
        long totalElements = skuInfos.getTotalElements();//总记录数
        int totalPages = skuInfos.getTotalPages();//总页数

        List<SkuInfo> content = skuInfos.getContent();//当前页的集合


        //8.1获取分组的结果  商品分类的分组结果
        List<String> categoryList = getStringsGroupname(skuInfos, "skuCategorygroup");

        //8.2获取分组的结果  品牌的分组结果
        List<String> brandList = getStringsGroupname(skuInfos, "skuBrandgroup");


        //8.3获取分组的结果  spec的分组结果
        StringTerms specStringterms = (StringTerms) skuInfos.getAggregation("skuSpecgroup");

        Map<String, Set<String>> specMap = getStringSetMap(specStringterms);


        //8.封装结果返回map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", content);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", totalPages);
        resultMap.put("categoryList", categoryList);
        resultMap.put("brandList", brandList);
        resultMap.put("specMap", specMap);
        //设置到resultmap
        resultMap.put("pageNum",page);
        resultMap.put("pageSize",pageSize);


        return resultMap;
    }

    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {
        Map<String, Set<String>> specMap = new HashMap<>();
        if (stringTermsSpec != null) {
            Set<String> values = new HashSet<String>();
            for (StringTerms.Bucket bucket : stringTermsSpec.getBuckets()) {
                //  {"手机屏幕尺寸":"5.5寸","网络":"电信4G","颜色":"白","测试":"s11","机身内存":"128G","存储":"16G","像素":"300万像素"}
                // {"手机屏幕尺寸":"5.0寸","网络":"电信4G","颜色":"白","测试":"s11","机身内存":"128G","存储":"16G","像素":"800万像素"}
                String keyAsString = bucket.getKeyAsString();
                Map<String, String> specmap = JSON.parseObject(keyAsString, Map.class);
                for (Map.Entry<String, String> stringStringEntry : specmap.entrySet()) {
                    String key = stringStringEntry.getKey();//  手机屏幕尺寸
                    String value = stringStringEntry.getValue();//5.5寸

                    values = specMap.get(key);
                    if (values == null) {
                        values = new HashSet<String>();
                    }
                    values.add(value);
                    specMap.put(key, values);
                }
            }
        }
        return specMap;
    }

    private List<String> getStringsGroupname(AggregatedPage<SkuInfo> skuInfos, String groupname) {
        StringTerms group = (StringTerms) skuInfos.getAggregation(groupname);
        List<String> list = new ArrayList<>();
        if (group != null) {
            for (StringTerms.Bucket bucket : group.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                list.add(keyAsString);
            }
        }
        return list;
    }
}
