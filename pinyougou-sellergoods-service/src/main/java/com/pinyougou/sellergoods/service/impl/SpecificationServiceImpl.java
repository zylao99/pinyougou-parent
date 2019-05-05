package com.pinyougou.sellergoods.service.impl;

import java.util.*;

import com.pinyougou.common.util.ExcelUtils;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //获取页面传递过来的规格的数据
        TbSpecification specification1 = specification.getSpecification();
        //1.添加规格的数据  主键返回 配置dao的xml配置文件  自动会将创建的主键的id 设置pojo中的id属性中
        specificationMapper.insert(specification1);
        //2.添加规格的选项的数据
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption option : optionList) {
            option.setSpecId(specification1.getId());
            optionMapper.insert(option);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {

        //1.更新规格的表的数据
        specificationMapper.updateByPrimaryKey(specification.getSpecification());
        //2.更新规格的选项的表的数据（有可能是删除） 先删除 再添加

        //delete from option where spec_id=1
        TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = exmaple.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpecification().getId());
        optionMapper.deleteByExample(exmaple);

        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption option : optionList) {
            //设置spec的id
            option.setSpecId(specification.getSpecification().getId());
            optionMapper.insert(option);
        }


    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //1.创建 一个组合对象
        Specification specification = new Specification();

        //2.查询规格的数据
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

        //3.查询规格的选项的数据
        //select * from tb_option where spec_id =1
        TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = exmaple.createCriteria();
        //select * from option
        criteria.andSpecIdEqualTo(tbSpecification.getId());//select * from option where spec_id =1
        List<TbSpecificationOption> options = optionMapper.selectByExample(exmaple);

        //4.设置组合对象的属性值 返回
        specification.setSpecification(tbSpecification);
        specification.setOptionList(options);
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格的数据
            specificationMapper.deleteByPrimaryKey(id);
            //删除规格下的所有的选项的数据
            TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
            exmaple.createCriteria().andSpecIdEqualTo(id);
            optionMapper.deleteByExample(exmaple);//更加条件删除 delete form option where spec_id = 1
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
            if(specification.getStatus()!=null && specification.getStatus().length()>0){
                criteria.andStatusLike("%"+specification.getStatus()+"%");
            }


        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> findSpecOptionList() {
        //1.查询所有的品牌列表 List<tbbrand>
        List<TbSpecification> specificationsList = specificationMapper.selectByExample(null);
        List<Map> mapList = new ArrayList<>();
        //2.组合成 List<Map>
        for (TbSpecification specification : specificationsList) {
            Map map = new HashMap();
            map.put("id",specification.getId());//[{id:,text:}]
            map.put("text",specification.getSpecName());
            mapList.add(map);
        }
        return mapList;
    }


    /**
     *规格导出
     * @param ids
     */
    @Override
    public void findSpecificationById(Long[] ids) {
        List<TbSpecification> specifications = null;
        if (ids!=null&&ids.length>0) {
            TbSpecificationExample example = new TbSpecificationExample();
            Criteria criteria = example.createCriteria();
            criteria.andIdIn(Arrays.asList(ids));
            specifications	= specificationMapper.selectByExample(example);
        }
        else{
            specifications = specificationMapper.selectByExample(null);
        }
        ExcelUtils.exportSpecification(specifications);
    }

    /**
     * 规格导入
     * @param tbSpecifications
     */
    @Override
    public void insertSpecificationList(List<TbSpecification> tbSpecifications) {
        for (TbSpecification specification : tbSpecifications) {
            TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(specification.getId());
            if (tbSpecification!=null){
                specificationMapper.updateByPrimaryKeySelective(specification);
            }else {
                specificationMapper.insertSelective(specification);
            }
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        TbSpecification record = new TbSpecification();
        record.setStatus(status);
        TbSpecificationExample example=new TbSpecificationExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        specificationMapper.updateByExampleSelective(record,example);
    }
}
