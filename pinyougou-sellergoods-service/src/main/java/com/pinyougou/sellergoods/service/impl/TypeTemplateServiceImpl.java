package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.ExcelUtils;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbTypeTemplateExample example = new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();

		if (typeTemplate != null) {
			if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
				criteria.andNameLike("%" + typeTemplate.getName() + "%");
			}
			if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
				criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
			}
			if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
				criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
			}
			if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
				criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
			}
			if(typeTemplate.getStatus()!=null && typeTemplate.getStatus().length()>0){
				criteria.andStatusLike("%"+typeTemplate.getStatus()+"%");
			}


		}

		Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);


		//添加品牌列表和规格列表的数据缓存
		//查询所有的模板的数据 缓存到redis中
		List<TbTypeTemplate> all = findAll();
		for (TbTypeTemplate tbTypeTemplate : all) {
			//缓存品牌
			String brandIds = tbTypeTemplate.getBrandIds();//[{"id":1,"text":"联想"},{"id":3,"text":"三星"}]
			//转成JSON
			List<Map> maps = JSON.parseArray(brandIds, Map.class);
			redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),maps);

			//缓存规格的列表
			List<Map> specList = findSpecList(tbTypeTemplate.getId());
			//[{"id":27,"text":"网络",options:[{},{}]},{"id":32,"text":"机身内存"}]
			redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
		}


		return new PageResult(page.getTotal(), page.getResult());
	}


	// 把      [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
	//拼接成： [{"id":27,"text":"网络",options:[{},{}]},{"id":32,"text":"机身内存"}]
    @Override
    public List<Map> findSpecList(Long id) {

		//1.根据模板的ID 获取模板对象
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		//2.获取模板对象中的规格的列表 字符串    [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		String specIds = tbTypeTemplate.getSpecIds();
		//将字符串转换成List
		List<Map> maps = JSON.parseArray(specIds, Map.class);

		for (Map map : maps) {
			//map   ====>{"id":27,"text":"网络"}
			//3.1 循环遍历数组 获取id
			Integer specId = (Integer) map.get("id");
			//3.2 从 规格选项表中查询该规格的所有规格选项列表
			TbSpecificationOptionExample exmpale = new TbSpecificationOptionExample();
			exmpale.createCriteria().andSpecIdEqualTo(Long.valueOf(specId));
			List<TbSpecificationOption> options = optionMapper.selectByExample(exmpale);//select * from option where spec_id = 1
			//[id:1,optionName:'4G']

			//3.拼接
			map.put("options",options);
		}
        return maps;
    }

	/**
	 * 模板导出
	 * @param ids
	 */
	@Override
	public void findTypeTemplateById(Long[] ids) {
		List<TbTypeTemplate> typeTemplates = null;
		if (ids!=null&&ids.length>0) {
			TbTypeTemplateExample example = new TbTypeTemplateExample();
			Criteria criteria = example.createCriteria();
			criteria.andIdIn(Arrays.asList(ids));
			typeTemplates = typeTemplateMapper.selectByExample(example);
		}
		else{
			typeTemplates = typeTemplateMapper.selectByExample(null);
		}

		ExcelUtils.exportTypeTemplate(typeTemplates);
	}

	/**
	 * 模板导入
	 * @param tbTypeTemplates
	 */
	@Override
	public void insertTypeTemplateList(List<TbTypeTemplate> tbTypeTemplates) {
		for (TbTypeTemplate typeTemplates : tbTypeTemplates) {
			TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(typeTemplates.getId());
			if (tbTypeTemplate!=null){
				typeTemplateMapper.updateByPrimaryKeySelective(typeTemplates);
			}else {
				typeTemplateMapper.insertSelective(typeTemplates);
			}
		}
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		TbTypeTemplate record=new TbTypeTemplate();
		record.setStatus(status);
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		example.createCriteria().andIdIn(Arrays.asList(ids));
		typeTemplateMapper.updateByExampleSelective(record,example);
	}
}
