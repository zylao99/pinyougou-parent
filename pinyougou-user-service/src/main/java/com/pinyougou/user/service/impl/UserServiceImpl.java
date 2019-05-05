package com.pinyougou.user.service.impl;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.pinyougou.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.pojo.TbUserExample.Criteria;


import entity.PageResult;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.jms.Destination;


/**
 * 服务实现层
 * @author Administrator
 *
 */

@Service

public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		updateStatus();
		List<TbUser> userList1 = userMapper.selectByExample(null);
		return userList1;
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		//updateStatus();
		PageHelper.startPage(pageNum, pageSize);
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		//加密存储
		String md5passowrd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5passowrd);
		userMapper.insert(user);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}
	}


	@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {

		updateStatus();


		PageHelper.startPage(pageNum, pageSize);
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();

		if(user!=null){
			if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}

		}

		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name="pinyougou_sms")
	private Destination destination;

	@Override
	public boolean createSmsCode(String phone) {
		//1.生成短信验证码  6位数字
		double random = Math.random()*1000000;
		String code = (long)random+"";

		//2.保存验证码 到redis中  key
		redisTemplate.boundValueOps("USER_REGISTER_"+phone).set(code,24,TimeUnit.HOURS);

		//3.发送消息 将数据发送到短信平台
		Map<String,String> map = new HashMap<>();
		map.put("mobile",phone);
		map.put("template_code","SMS_126865257");
		map.put("sign_name","黑马三国的包子");
		map.put("param","{\"code\":\""+code+"\"}");
		jmsTemplate.convertAndSend(destination,map);
		return true;
	}

	@Override
	public boolean isChecked(String phone, String code) {

		//1.先根据用户的手机号获取redis中的验证码
		String coderedis= (String) redisTemplate.boundValueOps("USER_REGISTER_" + phone).get();
		if(coderedis==null ){
			return false;
		}
		//2.匹配页面传递过来是否一致，如果一致就是正确

		if(coderedis.equals(code)){
			return true;
		}

		return false;
	}

	@Override
	public Map<String,Integer> findNumber() {
		Map<String,Integer> numMap = new HashMap();
		Integer count = userMapper.findCount();//总数

		Integer impNum = userMapper.findImpNum();//查询非正常的数量

		//查询活跃用户
		List<TbUser> userList = userMapper.selectByExample(null);
		for (TbUser tbUser : userList) {
			//获取用户的最后登录时间
			Date lastLoginTime = tbUser.getLastLoginTime();

			//获取当前时间，将这个时间减去三个月
			Date date = new Date();
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			rightNow.add(Calendar.DAY_OF_YEAR, -1);  //超过一天未登录，就不算活跃用户
			Date time = rightNow.getTime();

			long ms = 86400000;
			int activeNum = 0;
			if (time.getTime() - lastLoginTime.getTime() >= ms){
				activeNum +=activeNum;
			}
			System.out.println(activeNum);
			int noActiveNum = count-impNum-activeNum;

			numMap.put("count",count);
			numMap.put("impNum",impNum);
			numMap.put("activeNum",activeNum);
			numMap.put("noActiveNum",noActiveNum);

		}

		return numMap;
	}

	@Override
	public List<TbUser> query(String username) {

		TbUserExample example = new TbUserExample();
		TbUserExample.Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		return  userMapper.selectByExample(example);

	}


	public static void main(String[] args) {
		double random = Math.random()*1000000;
		System.out.println((long)random);
	}


	public void updateStatus(){
		List<TbUser> userList = userMapper.selectByExample(null);
		for (TbUser tbUser : userList) {
			//获取用户的最后登录时间
			Date lastLoginTime = tbUser.getLastLoginTime();

			//获取当前时间，将这个时间减去三个月
			Date date = new Date();
			Calendar rightNow  = Calendar.getInstance();
			rightNow.setTime(date);
			rightNow .add(Calendar.MONTH,-3);
			Date time = rightNow.getTime();


			//判断，如果time > lastLoginTime    表示已经超过三个月未登录   将使用状态设为非正常
			Long id = tbUser.getId();
			if (time.getTime() > lastLoginTime.getTime()){  //三个月
				//更新数据库，将使用状态设为非正常
				tbUser.setStatus("1");
			}else {
				tbUser.setStatus("0");
			}
			TbUserExample example = new TbUserExample();
			Criteria criteria = example.createCriteria();
			criteria.andIdEqualTo(id);
			userMapper.updateByExample(tbUser,example);
		}
	}
}
