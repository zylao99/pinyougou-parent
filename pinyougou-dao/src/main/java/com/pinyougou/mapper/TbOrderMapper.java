package com.pinyougou.mapper;

import com.pinyougou.pojo.Sale;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;

import java.util.HashMap;

import java.util.Date;
import java.util.List;

import com.pinyougou.pojogroup.SalesSituationVO;
import com.pinyougou.pojogroup.SalesSituationVOSQL;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TbOrderMapper {
    int countByExample(TbOrderExample example);

    int deleteByExample(TbOrderExample example);

    int deleteByPrimaryKey(Long orderId);

    int insert(TbOrder record);

    int insertSelective(TbOrder record);

    List<TbOrder> selectByExample(TbOrderExample example);

    TbOrder selectByPrimaryKey(Long orderId);

    int updateByExampleSelective(@Param("record") TbOrder record, @Param("example") TbOrderExample example);

    int updateByExample(@Param("record") TbOrder record, @Param("example") TbOrderExample example);

    int updateByPrimaryKeySelective(TbOrder record);

    int updateByPrimaryKey(TbOrder record);



    @Select("SELECT DATE_FORMAT(a.create_time, '%Y-%m-%d') AS saleTime,SUM(a.payment) AS saleMoney FROM tb_order AS a, tb_order_item AS b WHERE a.order_id =b.order_id AND a.create_time >= #{startTime} AND a.create_time < #{endTime} AND b.goods_id= #{goodsId} GROUP BY saleTime")
    List<Sale> getSalesSituation(SalesSituationVOSQL salesSituationVOSQL);

    Double getSaleroom1(HashMap<String, Object> map);

    Double getSaleroom(HashMap<String, Object> map);
}