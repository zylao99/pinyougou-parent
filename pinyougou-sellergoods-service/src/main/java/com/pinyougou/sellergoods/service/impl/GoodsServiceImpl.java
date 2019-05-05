package com.pinyougou.sellergoods.service.impl;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private  TbOrderMapper orderMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//1.添加商品（SPU）的信息
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");//都要是0 未审核
		tbGoods.setIsDelete(false);//不逻辑删除
		goodsMapper.insert(tbGoods);
		//2.添加商品的描述信息(goodsdesc)

		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		tbGoodsDescMapper.insert(goodsDesc);
		//3.添加商品的SKU列表信息
		//TODO
		List<TbItem> itemList = goods.getItemList();
		saveItems(tbGoods, goodsDesc, itemList);


	}

	private void saveItems(TbGoods tbGoods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
		//判断是否 启用规格
		if("1".equals(tbGoods.getIsEnableSpec())){

			for (TbItem tbItem : itemList) {

				//补全属性
				//设置商品的标题   SPU + 规格选项名
				String goodsName = tbGoods.getGoodsName();
				String spec = tbItem.getSpec();//{"网络":"移动3G","机身内存":"16G"}
				Map<String,String> parse = (Map) JSON.parseObject(spec);
				String title = goodsName;
				for (String key : parse.keySet()) {
					title+=" "+parse.get(key);
				}
				tbItem.setTitle(title);

				//设置图片路径

				String itemImages = goodsDesc.getItemImages();//[{color:"",url:""},{}]

				List<Map> maps = JSON.parseArray(itemImages, Map.class);
				tbItem.setImage(maps.get(0).get("url").toString());

				//设置三级分类的ID  先根据分类的ID 获取分类的对象
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
				tbItem.setCategoryid(tbItemCat.getId());
				tbItem.setCategory(tbItemCat.getName());

				//设置时间
				tbItem.setCreateTime(new Date());
				tbItem.setUpdateTime(tbItem.getCreateTime());

				//设置SPU的id
				tbItem.setGoodsId(tbGoods.getId());

				//设置商家的iD
				TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
				tbItem.setSellerId(tbSeller.getSellerId());
				tbItem.setSeller(tbSeller.getNickName());//店铺名
				//设置品牌名称
				TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
				tbItem.setBrand(tbBrand.getName());




				itemMapper.insert(tbItem);
			}

		}else{
			TbItem tbItem = new TbItem();
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem.setPrice(tbGoods.getPrice());
			tbItem.setNum(999);

			tbItem.setStatus("1");
			tbItem.setSpec("{}");

			//设置图片路径

			String itemImages = goodsDesc.getItemImages();//[{color:"",url:""},{}]

			List<Map> maps = JSON.parseArray(itemImages, Map.class);
			tbItem.setImage(maps.get(0).get("url").toString());



			//设置三级分类的ID  先根据分类的ID 获取分类的对象
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
			tbItem.setCategoryid(tbItemCat.getId());
			tbItem.setCategory(tbItemCat.getName());

			//设置时间
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(tbItem.getCreateTime());

			//设置SPU的id
			tbItem.setGoodsId(tbGoods.getId());

			//设置商家的iD
			TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
			tbItem.setSellerId(tbSeller.getSellerId());
			tbItem.setSeller(tbSeller.getNickName());//店铺名
			//设置品牌名称
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
			tbItem.setBrand(tbBrand.getName());


			itemMapper.insert(tbItem);
		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新SPU
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//更新描述
		tbGoodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//更新SKU
		List<TbItem> itemList = goods.getItemList();

		//先删除原来的SKU列表 再添加
		//delete from tb_item where goods_id =1
		TbItemExample exmaple = new TbItemExample();
		exmaple.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(exmaple);

		saveItems(goods.getGoods(), goods.getGoodsDesc(), itemList);



	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//创建goods对象
		Goods goods =new Goods();

		//设置商品的SPU的信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//设置商品的描述信息
		TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);
		//设置商品的SKU信息
		//select * from tb_item where goods_id=id
		TbItemExample exmaple = new TbItemExample();
		exmaple.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(exmaple);
		goods.setItemList(tbItems);

		//返回goods
		return goods;
	}

	@Transactional()
	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//逻辑删除 更新
			//先获取商品的数据
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete(true);//要删除
			//更新状态 更新到数据库中
			goodsMapper.updateByPrimaryKey(tbGoods);

		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteEqualTo(false);
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());//qiandu  qiandu1  qiandu2
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		//循环遍历
		for (Long id : ids) {
			//获取到id对应的商品数据
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			//更新状态
			tbGoods.setAuditStatus(status);

			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 根据时间查询销售额
	 * @param sellerId
	 * @param day
	 * @return
	 */
	@Override
	public Map getSaleroom(String sellerId, Integer day) {
		HashMap<String, Object> data = new HashMap<>();
		//创建legend容器
		ArrayList<Object> legendData = new ArrayList<>();
		//创建series容器
		ArrayList<Object> seriesData = new ArrayList<>();
		//new一个日期格式类
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//循环天数 获取对应的数据
		for (int i = day; i > 0; i--) {
			//获取对应的天数日期
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, -(i));
			Date date = c.getTime();
			//将天数存入legend容器
			legendData.add(format.format(date));
			//查询数据
			HashMap<String , Object> map = new HashMap<>();
			map.put("day",i);
			map.put("sellerId",sellerId);
			Double total =orderMapper.getSaleroom(map);
			//判断数据是否为空
			if(total!=null){
				seriesData.add(total);
			}else{
				//查不到数据设置默认值0;
				seriesData.add(0);
			}

		}
		data.put("legendData", legendData);
		data.put("seriesData", seriesData);
		return data;
	}
}
