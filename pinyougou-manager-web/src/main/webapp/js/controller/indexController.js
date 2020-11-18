app.controller("indexController",function($scope,$controller,loginService){
	
	$scope.getLoginName=function(){
		loginService.getLoginName().success(
				function(response){
					$scope.loginName=response.loginName;
				}
		);
	}
})