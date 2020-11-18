package com.pinyougou.search.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.search.service.ItemSearchService;

@Component
public class DeleteSearchMessageListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		
		try {
			TextMessage text=(TextMessage) message;
			String str = text.getText();
			List<Long> list=JSON.parseArray(str, Long.class);
			
			itemSearchService.deleteByGoodsIds(list);
			System.out.println("成功");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
