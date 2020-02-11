package com.changgou.search.pojo;

import com.changgou.search.annotation.MySearchAnnotation;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/***
 * POJO 用于做映射 和ES dcoument 映射
 * 1.创建索引
 * 2.创建映射
 * 3.创建文档的唯一标识
 * 4.字段的映射  是否分词  是否索引 是否存储  数据类型？ 分词器是？
 *  @Document(indexName = "skuinfo",type = "docs") 标识该POJO 和ES的中文档进行映射关系
 *      indexName 指定索引名
 *      type 指定类型名
 *    @Id 指定唯一标识
 *
 * @Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
 *      field 标识和es中的字段映射
 *
 *          type  指定数据类型
 *          index 是否索引 默认是true
 *          store 是否存储 默认是false
 *          analyzer 指定分词器（默认是就是标准分词器）   指定创建文档的时候使用的分词器
 *          searchAnalyzer 指定的是搜索文档的是分词器   可以不写，表示使用同一个分词器。
 *
 *
 * @author ljh
 * @packagename com.changgou.search.pojo
 * @version 1.0
 * @date 2020/1/3
 */
@MySearchAnnotation
@Document(indexName = "skuinfo",type = "docs")
public class SkuInfo implements Serializable{
    //商品id，同时也是商品编号
    @Id
    private Long id;

    //SKU名称
    @Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
    @MySearchAnnotation
    private String name;

    //商品价格，单位为：元
    @Field(type = FieldType.Double)
    private Long price;

    //库存数量
    private Integer num;

    //商品图片
    private String image;

    //商品状态，1-正常，2-下架，3-删除
    private String status;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //是否默认
    private String isDefault;

    //SPUID
    private Long spuId;

    //类目ID
    private Long categoryId;

    //类目名称
    @Field(type = FieldType.Keyword)
    private String categoryName;

    //品牌名称
    @Field(type = FieldType.Keyword)
    private String brandName;



    //规格
    private String spec;

    //规格参数
    private Map<String,Object> specMap;//{网络知识：“”} ----》specMap.网络制式.keyword:移动4G


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Map<String, Object> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, Object> specMap) {
        this.specMap = specMap;
    }
}
