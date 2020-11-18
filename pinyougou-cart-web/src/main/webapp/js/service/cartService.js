//购物车服务层
app.service("cartService",function($http){
	//购物车列表
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');	
	}
	
	//添加商品到购物车
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+"&num="+num);	
	}
	
	//求合计
	this.sum=function(cartList){
		var totalValue={totalNum:0,totalMoney:0}//合计实体
		
		for(var i=0;i<cartList.length;i++){
			var Cart=cartList[i];
			for(var j=0;j<Cart.orderItemList.length;j++){
				var orderItem=Cart.orderItemList[j];//购物车明细
				totalValue.totalNum+=orderItem.num;
				totalValue.totalMoney+= orderItem.totalFee;
			}
		}
		return totalValue;
	}
	
	//获取地址列表
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');	
	}
	
	//提交订单
	this.submit=function(order){
		return $http.post("order/add.do",order);
	}
	
})