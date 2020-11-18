package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;

import entity.Cart;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 添加商品到购物车
	 * @param cartList
	 * @param itemId
	 * @param num
	 * @return
	 */
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		//1.根据商品SKU ID查询SKU商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if(item==null){
			throw new RuntimeException("商品不存在");
		}
		if(!item.getStatus().equals("1")){
			throw new RuntimeException("商品状态无效");
		}
		//2.获取商家ID		
		String sellerId = item.getSellerId();
		//3.根据商家ID判断购物车列表中是否存在该商家的购物车		
		Cart cart = isExist(cartList,sellerId);
		
		if(cart==null) {//4.如果购物车列表中不存在该商家的购物车
			//4.1 新建购物车对象
			cart=new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			TbOrderItem orderItem = createOrderItem(item,num);
			List<TbOrderItem> list=new ArrayList<>();
			list.add(orderItem);
			cart.setOrderItemList(list);
			//4.2 将新建的购物车对象添加到购物车列表		
			
			cartList.add(cart);
			
		}else {//5.如果购物车列表中存在该商家的购物车		
			// 查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			
			if(orderItem==null) {//5.1. 如果没有，新增购物车明细	
				orderItem = createOrderItem(item,num);
				cart.getOrderItemList().add(orderItem);
			}else {//5.2. 如果有，在原购物车明细上添加数量，更改金额		
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
				//如果数量操作后小于等于0，则移除
				if(orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				//如果移除后cart的明细数量为0，则将cart移除
				if(cart.getOrderItemList().size()==0) {
					cartList.remove(cart);
				}
			}
			
		}
		return cartList;
	}

	/**
	 * 根据商家ID判断购物车列表中是否存在该商家的购物车
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart isExist(List<Cart> cartList,String sellerId) {
		for(Cart cart:cartList) {
			if(sellerId.equals(cart.getSellerId())) {
				return cart;
			}
		}
		return null; 
	}
	
	/**
	 * 创建订单
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		if(num<=0){
			throw new RuntimeException("数量非法");
		}
		TbOrderItem orderItem=new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}
	
	/**
	 * 查询购物车明细列表中是否存在该商品
	 * @param cartList
	 * @param itemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> OrderItemList,Long itemId) {
		for(TbOrderItem orderItem:OrderItemList) {
			if(orderItem.getItemId().longValue()==itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 从redis中查询购物车
	 * @param username
	 * @return
	 */
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车数据....."+username);
		List<Cart> cartList=(List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if(cartList==null) {
			cartList=new ArrayList<>();
		}
		return cartList;
	}

	
	/**
	 * 将购物车保存到redis
	 * @param username
	 * @param cartList
	 */
	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
		
	}

	/**
	 * 合并购物车
	 * @param cartList1
	 * @param cartList2
	 * @return
	 */
	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		
		for(Cart cart:cartList2) {
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for(TbOrderItem orderItem:orderItemList) {
				cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
			}
		}
		return cartList1;
	}
}
