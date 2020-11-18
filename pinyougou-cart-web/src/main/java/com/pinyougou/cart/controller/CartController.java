package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;

import entity.Cart;
import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout=6000)
	public CartService cartService;
	
	@Autowired
	public HttpServletRequest request;
	
	@Autowired
	public HttpServletResponse response;
	/**
	 * 购物车列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		//得到登陆人账号,判断当前是否有人登陆
		String username = SecurityContextHolder.getContext().getAuthentication().getName(); 
		
		String cookieValue = util.CookieUtil.getCookieValue(request, "cartList", "utf-8");
		if(cookieValue==""||cookieValue==null) {
			cookieValue="[]";
		}
		List<Cart> cookieCart = JSON.parseArray(cookieValue, Cart.class);
		
		if(username.equals("anonymousUser")) {
			return cookieCart;
		}else {
			List<Cart> redisCart = cartService.findCartListFromRedis(username);
			if(cookieCart.size()>0){//如果本地存在购物车
				//合并购物车
				redisCart=cartService.mergeCartList(redisCart, cookieCart);	
				//清除本地cookie的数据
				util.CookieUtil.deleteCookie(request, response, "cartList");
				//将合并后的数据存入redis 
				cartService.saveCartListToRedis(username, redisCart); 
			}	
			
			return redisCart;
		}
		
		
	}
	/**
	 * 添加商品到购物车
	 * @param request
	 * @param response
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
	public Result addGoodsToCartList(Long itemId,Integer num) {
		/*response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
		response.setHeader("Access-Control-Allow-Credentials", "true");*/   //如果要操作cookie就需要加上这一句
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登录用户："+username);
		try {
			List<Cart> findCartList = findCartList();//获取购物车列表
			List<Cart> addGoodsToCartList = cartService.addGoodsToCartList(findCartList, itemId, num);
			
			if(username.equals("anonymousUser")) {
				util.CookieUtil.setCookie(request, 
						response, 
						"cartList",
						JSON.toJSONString(addGoodsToCartList)
						, 60*60*24, 
						"UTF-8");
			}else {
				cartService.saveCartListToRedis(username, addGoodsToCartList);
			}
			
			
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.getStackTrace();
			return new Result(false,"添加失败");
		}
	}
}
