package com.pinyougou.sellergoods.service.impl;
import java.util.*;

import com.pinyougou.common.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbBrand> page=   (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand brand){
		brandMapper.updateByPrimaryKey(brand);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbBrand findOne(Long id){
		return brandMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			brandMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbBrandExample example=new TbBrandExample();
		Criteria criteria = example.createCriteria();
		
		if(brand!=null){			
			if(brand.getName()!=null && brand.getName().length()>0){
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
			if(brand.getStatus()!=null && brand.getStatus().length()>0){
				criteria.andStatusLike("%"+brand.getStatus()+"%");
			}

	
		}
		
		Page<TbBrand> page= (Page<TbBrand>)brandMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findBrandOptionList() {
		//1.查询所有的品牌列表 List<tbbrand>
		List<TbBrand> brandList = brandMapper.selectByExample(null);
		List<Map> mapList = new ArrayList<>();
		//2.组合成 List<Map>
		for (TbBrand tbBrand : brandList) {
			Map map = new HashMap();
			map.put("id",tbBrand.getId());//[{id:,text:}]
			map.put("text",tbBrand.getName());
			mapList.add(map);
		}

		return mapList;
	}

	/**
	 * 品牌导出
	 * @param ids
	 */
	@Override
	public void findBrandById(Long[] ids) {
		List<TbBrand> tbBrands =null;
		if (ids!=null&&ids.length>0) {
			TbBrandExample example=new TbBrandExample();
			Criteria criteria = example.createCriteria();
			criteria.andIdIn(Arrays.asList(ids));
			tbBrands = brandMapper.selectByExample(example);
		}
		else{
			tbBrands = brandMapper.selectByExample(null);
		}

		ExcelUtils.exportBrand(tbBrands);
	}

	/**
	 * 品牌导入
	 * @param brands
	 */
	@Override
	public void insertBrandList(List<TbBrand> brands) {
		for (TbBrand brand : brands) {
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(brand.getId());
			if (tbBrand!=null){
				brandMapper.updateByPrimaryKeySelective(brand);
			}else {
				brandMapper.insertSelective(brand);
			}
		}

	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		TbBrand record = new TbBrand();
		record.setStatus(status);
		TbBrandExample example=new TbBrandExample();
		example.createCriteria().andIdIn(Arrays.asList(ids));
		brandMapper.updateByExampleSelective(record,example);
	}
}
