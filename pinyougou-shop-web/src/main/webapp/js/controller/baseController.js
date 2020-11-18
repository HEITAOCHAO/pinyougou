app.controller('baseController', function($scope) {
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [ 10, 20, 30, 40, 50 ],
		onChange : function() {
			$scope.reloadList();
		}
	};

	$scope.reloadList = function() {
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}

	$scope.selectIds = [];
	$scope.updataSelet = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id)
		} else {
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index, 1);
		}
	}
	
	$scope.jsonToString=function(json,key){
		var jsonObject=JSON.parse(json);
		var str="";
		for(var i=0;i<jsonObject.length;i++){
			
			if(i>0){
				str+=",";
			}
			str+=jsonObject[i][key];
		}
		return  str;
	}
	
	//从集合中按照key查询对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}

})





