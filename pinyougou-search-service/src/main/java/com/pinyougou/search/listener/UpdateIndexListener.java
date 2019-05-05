package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.listener *
 * @since 1.0
 */
public class UpdateIndexListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        if(message instanceof TextMessage){

            try {
                //获取消息内容本身
                TextMessage textMessage =(TextMessage)message;
                //将消息内容（商品的数据）转成LIST
                String jsonstring = textMessage.getText();

                List<TbItem> tbItems = JSON.parseArray(jsonstring, TbItem.class);

                //调用API 更新到索引库中
                itemSearchService.updateIndex(tbItems);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
