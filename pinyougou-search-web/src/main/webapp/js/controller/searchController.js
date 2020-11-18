app.controller("searchController",function($scope,searchService,$location){
	
	$scope.search=function(){
		$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo) ;
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;
					buildPageLabel();
				}
		)
	}
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sortField':'','sort':''};
	
	//添加搜索项
	$scope.addSearchItem=function(key,value){
		if(key=='category'||key=='brand'||key=='price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();
	}
	
	//移除复合搜索条件
	$scope.deleteOption=function(key){
		if(key=="category"||key=="brand"||key=='price'){
			$scope.searchMap[key]='';
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	
	var buildPageLabel=function(){
		$scope.pageLabel=[];
		var totalPage=$scope.resultMap.totalPages;  //总页数
		var total=$scope.resultMap.total;   //总条数
		
		$scope.firstDot=true;  //前面省略号
		$scope.lastDot=true;	//后面省略号
		
		var begin=1;          //开始页
		var end=totalPage;    //结束页
		
		var pageNo=$scope.searchMap.pageNo;   //当前页
		
		if(end>5){
			if(pageNo<=3){
				end=5
				$scope.firstDot=false;
			}else if(pageNo>=end-2){
				begin=end-4;
				$scope.lastDot=false;
			}else{
				begin=pageNo-2;
				end=pageNo+2;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}

		for(var i=begin;i<=end;i++){
			$scope.pageLabel.push(i);
		}
	}
	
	//分页查询
	$scope.queryByPage=function(pageNo){
		if(pageNo<1||pageNo>$scope.resultMap.totalPages){
			return ;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	//判断是否第一页
	function isBegin(){
		if($scope.searchMap.pageNo==1){
			return true;
		}	
		return false;
	}
	
	//判断是否最后一页
	function isEnd(){
		if($scope.search.pageNo==$scope.resultMap.totalPages){
			return true;
		}
		return false;
	}
	
	//设置排序规则
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;	
		$scope.searchMap.sort=sort;	
		$scope.search();
	}
	
	//如果关键字和包含品牌，那么品牌就隐藏
	$scope.keywordsIsBrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
				return false;
			}			
		}		
		return true;
	}
	
	//接收主页跳转的信息
	$scope.locationJump=function(){
		var key=$location.search()["keywords"];
		if(key==""&&key==null){
			return;
		}
		
		$scope.searchMap.keywords= key;
		$scope.search();
	}
	
	
})