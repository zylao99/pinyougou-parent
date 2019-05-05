package com.pingyougou.search;


import com.itheima.test.TbItem.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pingyougou.search *
 * @since 1.0
 */
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
@RunWith(SpringRunner.class)
public class test {

    @Autowired
    private SolrTemplate solrTemplate;

    //增
    @Test
    public void addIndex(){
      /*  TbItem tbItem = new TbItem();//new document
        tbItem.setId(100L);       //new field
        tbItem.setTitle("测试商品");//new filed

        solrTemplate.saveBean(tbItem);//bean java 对象 （）====>一个docoument
        solrTemplate.commit();//提交*/

        TbItem item =new TbItem();
        for (int i = 0; i < 50; i++) {
            item.setId(101L+i);
            item.setBrand("华为"+i);
            item.setCategory("手机"+i);
            item.setGoodsId(1L);
            item.setSeller("华为2号专卖店"+i);
            item.setTitle("华为Mate9"+i);
            item.setPrice(new BigDecimal(2000));
            solrTemplate.saveBean(item);
            solrTemplate.commit();
        }




    }

    //删
    @Test
    public void delete(){
        solrTemplate.deleteById("107");
        solrTemplate.commit();;
    }

    @Test
    public void deleteByQuery(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();;
    }


    //查

    @Test
    public void selectByID(){
        TbItem byId = solrTemplate.getById("101", TbItem.class);
        System.out.println(byId.getTitle());
    }
    //根据查询的条件 查询
    @Test
    public void selectByQuery(){

       //1.创建一个查询对象
        Query query = new SimpleQuery("*:*");


        // 2.执行查询
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);

        //3.获取里面的列表
        System.out.println("总记录数"+tbItems.getTotalElements());
        System.out.println("总页数"+tbItems.getTotalPages());
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }


    }

    //根据查询的条件 查询
    @Test
    public void selectByQueryPage(){

        //1.创建一个查询对象
        Query query = new SimpleQuery("*:*");

        //设置分页的条件
        query.setOffset(0);//page-1 * rows
        query.setRows(20);//rows

        // 2.执行查询
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);

        //3.获取里面的列表
        System.out.println("总记录数"+tbItems.getTotalElements());
        System.out.println("总页数"+tbItems.getTotalPages());
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }


    }

    //条件查询 2
    @Test
    public void selectByQueryCriteriaPage(){
        //1.创建一个查询对象
        Query query = new SimpleQuery("*:*");
        //2.创建一个查询的条件对象
        Criteria criteria = new Criteria("item_title");//item_title:90
        criteria.is("0");

        //3.添加添加到查询对象中
        query.addCriteria(criteria);
        //4.执行查询


        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);


        System.out.println("总记录数"+tbItems.getTotalElements());
        System.out.println("总页数"+tbItems.getTotalPages());
        List<TbItem> content = tbItems.getContent();

        //5.获取结果
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }


    }



    //改
}
