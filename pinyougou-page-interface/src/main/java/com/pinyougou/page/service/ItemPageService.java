package com.pinyougou.page.service;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.page.service *
 * @since 1.0
 */
public interface ItemPageService {
    /**
     * 根据商品的信息 生成静态页面  核心：数据集+ 模板=html
     * @param id
     */
    public void genHtml(Long id);
}
