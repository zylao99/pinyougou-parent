package com.pinyougou.search.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.listener *
 * @since 1.0
 */
public class DeleteIndexListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        if(message instanceof ObjectMessage){
            ObjectMessage objectMessage =(ObjectMessage) message;
            try {
                Long[] ids = (Long[]) objectMessage.getObject();

                //调用APi删除索引库
                itemSearchService.deleteByIds(ids);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
