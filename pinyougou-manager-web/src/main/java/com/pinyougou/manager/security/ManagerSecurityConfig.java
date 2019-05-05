package com.pinyougou.manager.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.security *
 * @since 1.0
 */
@EnableWebSecurity
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //ROLE_ 省略
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");



    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //不拦截所匹配的url
        http.authorizeRequests().antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html").permitAll()
                .anyRequest().authenticated();

        //配置自定义的登录页面

        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login.html?error");

        //禁用csrf
        http.csrf().disable();

        //同源iframe 可以访问
        http.headers().frameOptions().sameOrigin();

        //退出登录
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login.html").invalidateHttpSession(true);




    }
}
