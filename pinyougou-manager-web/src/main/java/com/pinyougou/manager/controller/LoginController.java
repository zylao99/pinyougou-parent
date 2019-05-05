package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.controller *
 * @since 1.0
 */
@RestController//@responsebody + @controller
public class LoginController {

    @RequestMapping("/loginname")
    public Map getLoginName() {
        Map<String, String> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        map.put("loginName", username);
        return map;
    }
}
