package com.pinyougou.solrutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	private  void importItemData() {
		TbItemExample  itemExampel=new TbItemExample();
		Criteria criteria = itemExampel.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> items=itemMapper.selectByExample(itemExampel);
		
		for(TbItem item:items) {
			Map map= JSON.parseObject(item.getSpec());
			item.setSpceMap(map);
		}
		
		solrTemplate.saveBeans(items);
		solrTemplate.commit();
	}
	
	
	public static void main(String[] args) {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solr=(SolrUtil) context.getBean("solrUtil");
		solr.importItemData();
	}
}
