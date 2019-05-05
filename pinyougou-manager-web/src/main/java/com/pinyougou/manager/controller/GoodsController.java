package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemService itemService;



	@Reference
	private ItemPageService itemPageService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//登录的商家的ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			//校验 更新的商品 是不是当前的商家
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			Goods one = goodsService.findOne(goods.getGoods().getId());
			String sellerId1 = one.getGoods().getSellerId();
			if(!sellerId.equals(sellerId1)){
				return new Result(false, "没有权限修改");
			}

			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//删除商品

			//获取要删除的商品的SPU的ID数组
			//调用搜索服务 实现删除索引库
//			itemSearchService.deleteByIds(ids);
			jmsTemplate.send(delete_solr_index, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

		return goodsService.findPage(goods, page, rows);
	}


	@Autowired
	private JmsTemplate jmsTemplate;

	@Resource(name="update_solr_index")
	private Destination destination;

	@Resource(name="delete_solr_index")
	private Destination delete_solr_index;

	@Resource(name="gen_page_html_topic")
	private Destination gen_page_html_topic;


	@RequestMapping("/updateStatus")
	public Result updateStatus(final Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);
			//1.需要先根据ids 查询商品的数据（SKU的数据）
			final List<TbItem> tbItemListByIds = itemService.findTbItemListByIds(ids);
			//2.调用搜索服务的方法（updateIndex） 更新索引库
			//itemSearchService.updateIndex(tbItemListByIds);
			jmsTemplate.send(destination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					String s = JSON.toJSONString(tbItemListByIds);
					return session.createTextMessage(s);
				}
			});
			//3.生成商品详情的静态页面 发送消息

			jmsTemplate.send(gen_page_html_topic, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});


			return new Result(true,"操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"操作失败");
		}
	}

	@RequestMapping("/genhtmltest")
	public String genhtml(Long id){
		itemPageService.genHtml(id);
		return "success";
	}
	
}
