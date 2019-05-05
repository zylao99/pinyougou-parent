package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.common.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
			if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
			if(itemCat.getStatus()!=null && itemCat.getStatus().length()>0){
				criteria.andStatusLike("%"+itemCat.getStatus()+"%");
			}

	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> findByParentId(Long parentId) {
		//select * from tb_itemcat where parent_id = 1

		TbItemCatExample exmaple= new TbItemCatExample();
		exmaple.createCriteria().andParentIdEqualTo(parentId);

		//将分类的【所有的】数据缓存起来
		List<TbItemCat> all = findAll();
		for (TbItemCat tbItemCat : all) {
			redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(),tbItemCat.getTypeId());
		}


		return itemCatMapper.selectByExample(exmaple);
	}


	/**
	 * 商品分类导出
	 * @param ids
	 */
	@Override
	public void findItemCatById(Long[] ids) {
		List<TbItemCat> itemCats = null;
		if (ids!=null&&ids.length>0) {
			TbItemCatExample example = new TbItemCatExample();
			Criteria criteria = example.createCriteria();
			criteria.andIdIn(Arrays.asList(ids));
			itemCats = itemCatMapper.selectByExample(example);
		}
		else{
			itemCats = itemCatMapper.selectByExample(null);
		}
		ExcelUtils.exportItemCat (itemCats);
	}
	/**
	 * 商品分类导入
	 * @param itemCats
	 */
	@Override
	public void insertItemCatsList(List<TbItemCat> itemCats) {
		for (TbItemCat itemCat : itemCats) {
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(itemCat.getId());
			if (tbItemCat!=null){
				itemCatMapper.updateByPrimaryKeySelective(itemCat);
			}else {
				itemCatMapper.insertSelective(itemCat);
			}
		}

	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		TbItemCat record = new TbItemCat();
		record.setStatus(status);
		TbItemCatExample example=new TbItemCatExample();
		example.createCriteria().andIdIn(Arrays.asList(ids));
		itemCatMapper.updateByExampleSelective(record,example);
	}


}
