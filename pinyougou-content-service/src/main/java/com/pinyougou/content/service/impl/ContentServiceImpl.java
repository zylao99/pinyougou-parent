package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//清空缓存

		redisTemplate.boundHashOps("REDIS_CONENT_KEY").delete(content.getCategoryId());


		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		//1.先获取原来的广告的分类的ID
		Long id = content.getId();
		TbContent tbContent = contentMapper.selectByPrimaryKey(id);
		Long categoryId = tbContent.getCategoryId();


		//2.再获取现在的广告的分类的ID
		Long categoryId1 = content.getCategoryId();

		contentMapper.updateByPrimaryKey(content);


		//3.判断是否更新了分类 清空
		if(categoryId1!=categoryId.longValue()) {
			redisTemplate.boundHashOps("REDIS_CONENT_KEY").delete(categoryId1);
			redisTemplate.boundHashOps("REDIS_CONENT_KEY").delete(categoryId);
		}else{
			redisTemplate.boundHashOps("REDIS_CONENT_KEY").delete(categoryId);
		}
	}

	/*public static void main(String[] args) {
		Integer i = 10000;
		Integer y = 10000;

		System.out.println(i==y);
	}*/
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//先获取该广告的对应的分类的ID
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);

			contentMapper.deleteByPrimaryKey(id);
			// 删除redis中的数据
			redisTemplate.boundHashOps("REDIS_CONENT_KEY").delete(tbContent.getCategoryId());
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private RedisTemplate redisTemplate;
    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {



		//1.查询 缓存  如果有缓存 直接返回
		List<TbContent> contentsFromRedis= (List<TbContent>) redisTemplate.boundHashOps("REDIS_CONENT_KEY").get(categoryId);
		if(contentsFromRedis!=null && contentsFromRedis.size()>0){
			System.out.println("有缓存！");
			return contentsFromRedis;
		}

		//根据广告分类的id查询该分类下广告列表
		//select * from tb_content where category_id=1 and status=1  order by sort_order desc
		TbContentExample exmaple = new TbContentExample();
		Criteria criteria = exmaple.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);//
		criteria.andStatusEqualTo("1");

		exmaple.setOrderByClause("sort_order desc");//代替是order by   + sort_order desc
		List<TbContent> tbContents = contentMapper.selectByExample(exmaple);


		//2.添加缓存到redis中
			//1.加入依赖  2.配置文件(连接池 连接工厂 redistempalte)  3. 注入redistemplate
		//key value   用哪种数据类型 ----->hash 数据类型
		//key categoryId  value 集合

		redisTemplate.boundHashOps("REDIS_CONENT_KEY").put(categoryId,tbContents);

		System.out.println("没有缓存！");



		return tbContents;

    }

}
