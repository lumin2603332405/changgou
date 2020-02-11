package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {

    @Select(value="select tbb.* from tb_brand tbb,tb_category_brand tbc where tbb.id=tbc.brand_id and tbc.category_id=#{id}")
    public List<Brand> findByCategoryId(Integer id);
}
