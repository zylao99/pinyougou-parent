package com.pinyougou.order.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbOrderItem;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderItemServiceImpl implements OrderItemService{

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrderItem> findAll() {
		List<TbOrderItem> orderItems = orderItemMapper.selectByGroup();
		return orderItems;
	}

	@Override
	public List<TbOrderItem> dateFilterFind(String date) {
		return orderItemMapper.dateFilterFind(date);
	}


}
