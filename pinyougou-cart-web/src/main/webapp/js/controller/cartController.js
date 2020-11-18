app.controller("cartController",function($scope,cartService){

	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
				function(response){
					$scope.cartList=response;
					console.log(response)
					$scope.totalValue=cartService.sum($scope.cartList);//求合计数
				}
		);
	}
	
	//添加商品到购物车
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
				function(response){
					if(response.success){
						$scope.findCartList();
					}else{
						alert(response.message)
					}
					
				}
		)
	}
	
	
	//获取地址列表
	$scope.findAddressList=function(){
		
		cartService.findAddressList().success(
				function(response){
					$scope.addressList=response;
					for(var i=0;i<response.length;i++){
						if(response[i].isDefault==1){
							$scope.address=response[i];
							break;
						}
					}
					
						
				}
		)
	}
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;
	}
	
	//判断是否是当前选中的地址
	$scope.isSelectAddress=function(address){
		if($scope.address==address){
			return true;
		}
		return false;
	}
	
	$scope.order={"paymentType":'1'}
	
	//选择支付方式
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
	
	//提交订单
	$scope.submit=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submit($scope.order).success(
				function(response){
					if(response.success){
						//页面跳转
						if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
							location.href="pay.html";
						}else{//如果货到付款，跳转到提示页面
							location.href="paysuccess.html";
						}					
					}else{
						alert(response.message);	//也可以跳转到提示页面				
					}		
				}
		)
		
	}
})
