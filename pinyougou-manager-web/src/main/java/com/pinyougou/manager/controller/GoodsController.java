package com.pinyougou.manager.controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.Goods;
import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	
	@Autowired
	private Destination queueSearchDeleteDestination;   //删除搜索信息
	@Autowired
	private Destination topicDeletePageDestination;  //删除详细页面
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
			goodsService.delete(ids);
			List<Long> list=new ArrayList<>();
			for(Long id:ids) {
				list.add(id);
			}
			final List<Long> jsm=list;
			//删除sorl的数据
			//itemSearchService.deleteByGoodsIds(list);
			jsmTemplate.send(queueSearchDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					String jsonString = JSON.toJSONString(jsm);
					return session.createTextMessage(jsonString);
				}
			});
			
			//删除详细页面
			jsmTemplate.send(topicDeletePageDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			
		
		return new Result(true, "删除成功"); 
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
	
	//@Reference(timeout=40000)
	//private ItemPageService itemPageService;
	//@Reference
	//private ItemSearchService itemSearchService;
	
	
	@Autowired
	private Destination topicCreatePageDestination;
	
	@Autowired
	private JmsTemplate jsmTemplate;
	
	@Autowired
	private Destination queueSearchDestination;   //用于发送solr导入的消息
	
	/**
	 * 修改状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status) {
		try {
			goodsService.updateStatus(ids, status);
			
			//把数据存入solr
			if("1".equals(status)) {
				List<TbItem> items = goodsService.findItemListByGoodsIdandStatus(ids, status);
				if(items.size()>0) {
					//itemSearchService.importList(items);
					final String str = JSON.toJSONString(items);
					jsmTemplate.send(queueSearchDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(str);
						}
					});
					
				}
				//静态页生成
				for(final Long goodsId:ids){
					//itemPageService.getItemHtml(goodsId);
					jsmTemplate.send(topicCreatePageDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							return session.createTextMessage(goodsId+"");
						}
					});
				}
			}
			return new Result(true,"成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false,"失败");
		}
	}
	
}
