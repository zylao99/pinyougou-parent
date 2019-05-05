package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
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
 * @package com.pinyougou.page.listener *
 * @since 1.0
 */
public class GenHtmlListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            ObjectMessage objectMessage = (ObjectMessage)message;
            try {
                Long[] ids = (Long[]) objectMessage.getObject();
                //IDS 就是商品SPU的id

                for (Long id : ids) {
                    itemPageService.genHtml(id);
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
