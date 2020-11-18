app.controller("contentController",function($scope,contentService){
	
	//广告集合	
	$scope.contentList=[]
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
				function(response){
					$scope.contentList[categoryId]=response;
				}
		)
	}
	
	//跳转搜素页面
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
	
})