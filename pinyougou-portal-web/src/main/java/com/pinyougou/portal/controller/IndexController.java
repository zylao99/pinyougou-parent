package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.portal.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
        //var jobj = {key:value}
        //jobj.key =
        List<TbContent> contentListByCategoryId = contentService.findContentListByCategoryId(categoryId);
        return  contentListByCategoryId;
    }
}
