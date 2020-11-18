app.controller('itemController', function($scope) {

	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	} 
	
	//记录用户选择的规格
	$scope.specificationItems={};
	
	//用户选择规格
	$scope.selectSpec=function(name,value){
		$scope.specificationItems[name]=value;
		searchSku();
	}
	
	//判断某规格选项是否被用户选中
	$scope.isSelect=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}
		return false;
	}

	//加载默认SKU
	$scope.loadSKU=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//匹配两个对象	
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(JSON.stringify(skuList[i].spec)==JSON.stringify($scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的	
	}
	
	//添加购物车
	$scope.addToCart=function(){
		alert("加入购物车"+$scope.sku.id)
	}
	
	
	
	
})