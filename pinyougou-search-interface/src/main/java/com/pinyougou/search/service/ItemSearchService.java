package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service *
 * @since 1.0
 */
public interface ItemSearchService {

    /**
     *
     * @param searchMap  表示的时候页面传递过来的搜索条件对象
     * @return  搜索的结果
     */
    public Map<String,Object> search(Map searchMap);

    void updateIndex(List<TbItem> tbItemListByIds);

    void deleteByIds(Long[] ids);

}
