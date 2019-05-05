package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.test.TbItem.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service.impl *
 * @since 1.0
 */
@Service//timeout = 100000
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();

        //高亮查询
        Map result1 = searchList(searchMap);
        map.putAll(result1);

        //分组查询


        List<String> categryList = searchCategoryList(searchMap);
        map.put("categoryList", categryList);

        //点击的某一个商品分类 所对应的    品牌和规格的列表查询  默认是查询第一个分类下的
        String category = (String) searchMap.get("category");
        if(StringUtils.isNotBlank(category)){
            Map mapbrandAndSpec = searchSpecListAndBrandList(category);
            map.putAll(mapbrandAndSpec);
        }else {
            if(categryList!=null && categryList.size()>0) {
                Map mapbrandAndSpec = searchSpecListAndBrandList(categryList.get(0));
                map.putAll(mapbrandAndSpec);
            }
        }

        return map;
    }

    @Override
    public void updateIndex(List<com.pinyougou.pojo.TbItem> itemList) {
        //先删除 在添加
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
    }

    @Override
    public void deleteByIds(Long[] ids) {
        Query query = new SimpleQuery();
        //删除item_goodsid in (1,2,3)
        Criteria criteria = new Criteria("item_goodsid");
        criteria.in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private Map searchSpecListAndBrandList(String category) {
        Map brandAndSpecMap = new HashMap();
        //1.先根据分类的名称 从redis中获取分类对应的模板的ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        //2.根据模板的ID 获取品牌的列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);

        //3.根据模板的ID 获取规格的列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

        //4.存储在map中
        brandAndSpecMap.put("brandList",brandList);
        brandAndSpecMap.put("specList",specList);
        return brandAndSpecMap;


    }

    // select category from tb_item where title like '%手机%' group by category;//手机指的是 关键字
    private List<String> searchCategoryList(Map searchMap) {

        List<String> categoryList = new ArrayList<>();
        //1.获取关键字
        String keywords = (String) searchMap.get("keywords");//手机
        keywords=keywords.replace(" ","");
        //2.创建一个查询对象
        Query query = new SimpleQuery("*:*");// select * from tb_item where title like '%手机%'

        Criteria criteria = new Criteria("item_keywords");//item_keywords : 手机
        criteria.is(keywords);
        query.addCriteria(criteria);

        //3.分组查询  添加分组条件
        GroupOptions groupOptions = new GroupOptions();

        // group by category;
        groupOptions.addGroupByField("item_category");

        query.setGroupOptions(groupOptions);

        //4.执行分组查询
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        //5.获取分组的数据
        GroupResult<TbItem> itemCategory = groupPage.getGroupResult("item_category");//指定获取哪一个分组域的值
        Page<GroupEntry<TbItem>> groupEntries = itemCategory.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();//手机   ----》10条item    笔记本 --->20条
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            String groupValue = tbItemGroupEntry.getGroupValue();//category的数据
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    private Map searchList(Map searchMap) {
        Map result = new HashMap();
        //1.先获取map中的属性值
        String keywords = (String) searchMap.get("keywords");//手机
        keywords=keywords.replace(" ","");
        //2.创建一个查询的对象
        HighlightQuery query = new SimpleHighlightQuery();
        //3 添加查询条件
        Criteria criteria = new Criteria("item_keywords");//item_keywords : 手机
        criteria.is(keywords);
        query.addCriteria(criteria);


        //高亮显示
        //设置高亮选项  （开启高亮  设置前缀 后缀 设置高亮显示的域）
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//高亮显示的域

        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);



        //过滤查询   根据商品的分类 来过滤查询  item_category:手机

        String category = (String) searchMap.get("category");
        if(StringUtils.isNotBlank(category)){
            //添加过滤查询的条件

            FilterQuery fileterquery = new SimpleFilterQuery();

            Criteria criteria1 = new Criteria("item_category");
            criteria1.is(category);

            fileterquery.addCriteria(criteria1);

            query.addFilterQuery(fileterquery);
        }

        //过滤查询   根据品牌 来过滤查询  item_brand:华为
        String brand = (String) searchMap.get("brand");
        if(StringUtils.isNotBlank(brand)){
            //添加过滤查询的条件

            FilterQuery fileterquery = new SimpleFilterQuery();

            Criteria criteria1 = new Criteria("item_brand");
            criteria1.is(brand);

            fileterquery.addCriteria(criteria1);

            query.addFilterQuery(fileterquery);
        }
        //过滤查询   根据规格来过滤查询  item_spec_网络:移动3G
        Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
        if(specMap!=null){
            for (String key : specMap.keySet()) {//key :网络   value：移动3G

                FilterQuery fileterquery = new SimpleFilterQuery();

                Criteria criteria1 = new Criteria("item_spec_"+key);
                criteria1.is(specMap.get(key));

                fileterquery.addCriteria(criteria1);

                query.addFilterQuery(fileterquery);

            }
        }

        // 价格的区间过滤查询
        String price = (String) searchMap.get("price");//0-500
        if(StringUtils.isNotBlank(price)) {
            //添加过滤查询

            FilterQuery filterquery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_price");//item_price:[0 TO 20]

            String[] split = price.split("-");

            if ("*".equals(split[1])) {//3000-*
                criteria1.greaterThanEqual(split[0]);
            } else {
                criteria1.between(split[0], split[1], true, true);
            }

            filterquery.addCriteria(criteria1);

            query.addFilterQuery(filterquery);
        }


        //4.执行查询 分页查询

        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageNo==null){
            pageNo=1;
        }
        if(pageSize==null){
            pageSize=60;
        }

        query.setOffset((pageNo-1)*pageSize);//page -1 * rows

        query.setRows(pageSize);//rows


        //5.排序 过滤

        String sortField = (String) searchMap.get("sortField");//域的名称 去掉item_
        String sortType = (String) searchMap.get("sortType");//DESC

        if(StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)){
            //排序

            //item_price asc

            if("ASC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }else{
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }

        }




        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取高亮结果
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            TbItem entity = tbItemHighlightEntry.getEntity();
            if (tbItemHighlightEntry.getHighlights() != null
                    && tbItemHighlightEntry.getHighlights().size() > 0
                    && tbItemHighlightEntry.getHighlights().get(0) != null
                    && tbItemHighlightEntry.getHighlights().get(0).getSnipplets() != null
                    && tbItemHighlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                entity.setTitle(tbItemHighlightEntry.getHighlights().get(0).getSnipplets().get(0));
            }
        }


        //5.获取结果
        List<TbItem> content = tbItems.getContent();


        result.put("rows", content);
        result.put("total", tbItems.getTotalElements());
        result.put("totalPages", tbItems.getTotalPages());

        return result;
    }
}
