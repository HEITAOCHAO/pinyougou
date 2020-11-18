app.service("brandService", function($http) {

	this.findOne = function(id) {
		return $http.get('../brand/findOne.do?id=' + id);
	}

	this.dele = function(ids) {
		return $http.get('../brand/delete.do?ids=' + ids);
	}

	this.add = function(entity) {
		return $http.post('../brand/add.do', entity);
	}

	this.update = function(entity) {
		return $http.post('../brand/update.do', entity);
	}

	this.search = function(pageNum, pageSize, searchEntity) {
		return $http.post('../brand/search.do?pageNum=' + pageNum
				+ "&pageSize=" + pageSize, searchEntity);
	}
	
	//
	this.selectBrandOptionList=function(){
		return $http.get('../brand/selectBrandOptionList.do');
	} 
})