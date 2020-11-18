 //控制层 
app.controller('userController' ,function($scope,$controller,userService){	
	
	$scope.entity={}
	$scope.reg=function(){
		if($scope.entity.password!=$scope.password){
			alert("两次密码不一致");
			$scope.password="";
			return;
		}
		
		userService.add($scope.entity,$scope.code).success(
				function(response){
					alert(response.message)
				}
		)
	}
	
	$scope.sendCode=function(){
		if($scope.entity.email==null||$scope.entity.email==""){
			alert("邮箱不能为空")
			return;
		}
		userService.sendCode($scope.entity.email).success(
				function(response){
					alert(response.message)
				}
		)
	}
    
});	
