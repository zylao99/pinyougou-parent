package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.solr.util *
 * @since 1.0
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    //业务  将数据导入到索引库中
    public void importDataToIndex(){
        //1.注入mapper
        //2.查询数据库中的数据
        TbItemExample exmaple = new TbItemExample();
        TbItemExample.Criteria criteria = exmaple.createCriteria();
        criteria.andStatusEqualTo("1");

        List<TbItem> tbItems = tbItemMapper.selectByExample(exmaple);

        //循环遍历  spec 属性
        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();//有值{"机身内存":"16G","网络":"联通3G"}
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }

        //3.注入solrtemplate
        //4.调用API 导入到索引库中
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }
}
