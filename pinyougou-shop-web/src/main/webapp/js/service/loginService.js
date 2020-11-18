app.service("loginService",function($http){
	
	//获取名称
	this.getName=function(){
		return $http.get("../login/getName.do");
	}
})