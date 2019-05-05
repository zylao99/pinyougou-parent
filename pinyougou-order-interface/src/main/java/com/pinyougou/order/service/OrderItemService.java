package com.pinyougou.order.service;
import java.util.Date;
import java.util.List;
import com.pinyougou.pojo.TbOrderItem;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderItemService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrderItem> findAll();


    List<TbOrderItem> dateFilterFind(String date);
}
