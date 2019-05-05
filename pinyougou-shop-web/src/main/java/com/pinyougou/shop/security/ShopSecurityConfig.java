package com.pinyougou.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.security *
 * @since 1.0
 */
@EnableWebSecurity//开启自动化配置
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //认证
        //auth.inMemoryAuthentication().withUser("seller").password("seller").roles("SELLER");
        //通过自定认证类的方式来实现认证
        //添加加密的方式来认证
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests().
                antMatchers("/*.html", "/css/**", "/js/**", "/img/**", "/plugins/**","/seller/add.do").permitAll()
                .anyRequest().authenticated();

        //自定义登录页面
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html", true)
                .failureUrl("/shoplogin.html?error");

        //禁用csrf
        http.csrf().disable();

        //设置同源ifrmae 可以访问
        http.headers().frameOptions().sameOrigin();
    }
}
