app.controller('brandController', function($scope,$controller,brandService) {

	$controller('baseController',{$scope:$scope});

	$scope.save = function() {
		var object = null;
		if ($scope.entity.id != null) {
			object = brandService.update($scope.entity);
		} else {
			object = brandService.add($scope.entity);
		}
		object.success(function(response) {
			if (response.success) {
				alert(response.message)
				$scope.reloadList();
			} else {
				alert(response.message)
			}
		})
	};

	$scope.findOne = function(id) {
		brandService.findOne(id).success(function(response) {
			console.log(response)
			$scope.entity = response;
		})
	}

	$scope.dele = function() {
		if ($scope.selectIds.length == 0) {
			alert("没有选择删除选项")
			return;
		}
		brandService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				alert(response.message)
				$scope.reloadList();
			} else {
				alert(response.message)
			}
		})
	}

	$scope.searchEntity = {};
	$scope.search = function(pageNum, pageSize) {
		brandService.search(pageNum, pageSize, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;
				})
	}
	
	
})