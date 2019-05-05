package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;




    /**
     * 查询所有订单
     * @return list<TbOrder>
     */
    @RequestMapping("/findAll")
    public List<TbOrder> findAll(){

        return orderService.findAll();
    }


    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows){
        return orderService.findPage(page, rows);
    }

    /**
     * 查询+分页
     * @param order
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
        return orderService.findPage(order, page, rows);
    }


    /**
     * 根据id返回一列信息
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbOrder findOne(Long id){
        System.out.println(id);
        TbOrder tbOrder = orderService.findOne(id);
        System.out.println(tbOrder.toString());
        return tbOrder;
    }

}
