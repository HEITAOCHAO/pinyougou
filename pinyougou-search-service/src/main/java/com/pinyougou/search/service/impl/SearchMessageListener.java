package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Component
public class SearchMessageListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		
		try {
			TextMessage text=(TextMessage) message;
			String str=text.getText();
			List<TbItem> items=JSON.parseArray(str, TbItem.class);
			for(TbItem item:items) {
				System.out.println(item.getId()+" "+item.getTitle());
				Map map= JSON.parseObject(item.getSpec());
				item.setSpceMap(map);
			}
			itemSearchService.importList(items);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
