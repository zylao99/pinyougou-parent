package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.service *
 * @since 1.0
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        System.out.println("UserDetailsServiceImpl--------");
        //从数据库查询用户名对应的密码 判断相关的业务逻辑


        //1.先通过页面传递过来的用户名查询该用户的对象信息
        TbSeller serviceOne = sellerService.findOne(username);
        //2.判断是否为空 如果为空 说明 没有这个用户 return null;
        if(serviceOne==null){
            return null;
        }


        //3.如果 不为空 说明用户是存在的。 判断 用户是否已经审核通过 如果没有审核通过  return null


        //如果 用户已经审核 才需要进行登录认证
        if("1".equals(serviceOne.getStatus())){
            //4. 验证密码



            // 验证密码是否成确 这个交给springscurity去做
            return new User(username,serviceOne.getPassword(),authorities);
        }

        //没审核
        return null;


    }
}
