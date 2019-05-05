package com.pinyougou.manager.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.pinyougou.order.service.OrderItemService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrderItem;



/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

	@Reference
	private OrderItemService orderItemService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrderItem> findAll() {
		List<TbOrderItem> orderItems = orderItemService.findAll();
		return orderItems;

	}


	@RequestMapping("/dateFilterFind")
	public List<TbOrderItem> dateFilterFind(String date) {

		List<TbOrderItem> orderItems = orderItemService.dateFilterFind(date);

		return orderItems;

	}
}
