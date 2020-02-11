package com.changgou;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 描述
 *
 * @author ljh
 * @version 1.0
 * @package com.changgou *
 * @Date 2019-12-27
 * @since 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsApplicationTest {

    @Autowired(required = false)
    private BrandMapper brandMapper;


    @Test
    public void findAll(){
        List<Brand> brands = brandMapper.selectAll();
        for (Brand brand : brands) {
            System.out.println(brand.getName());
        }
    }



}
