//控制层 
app.controller('goodsController', function($scope, $controller,$location, goodsService,
		uploadService,itemCatService,typeTemplateService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		goodsService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, rows) {
		goodsService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 查询实体
	$scope.findOne = function() {
		var id=$location.search()['id'];
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(function(response) {
			$scope.entity = response;
			//富文本框
			editor.html($scope.entity.goodsDesc.introduction);
			//图片
			$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
			//扩展属性
			$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems)
			//规格
			$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);	
			
			
			//SKU列表规格列转换				
			for( var i=0;i<$scope.entity.itemList.length;i++ ){
				$scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec);		
			}
		});
	}

	// 保存
	$scope.save = function() {
		$scope.entity.goodsDesc.introduction = editor.html();
		var serviceObject;// 服务层对象
		if ($scope.entity.goods.id!= null) {// 如果有ID
			serviceObject = goodsService.update($scope.entity); // 修改
		} else {
			serviceObject = goodsService.add($scope.entity);// 增加
		}
		serviceObject.success(function(response) {
			if (response.success) {
				alert('添加成功！');
				// 重新查询
				$scope.reloadList();// 重新加载
				location.href="goods.html";
			} else {
				alert(response.message);
			}
		});
	}

	// 保存
	$scope.add = function() {
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(function(response) {
			if (response.success) {
				// 重新查询
				alert('保存成功！');
				
				location.href="goods.html";
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除
	$scope.dele = function() {
		// 获取选中的复选框
		goodsService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				$scope.reloadList();// 刷新列表
				$scope.selectIds = [];
			}
		});
	}

	$scope.searchEntity = {};// 定义搜索对象

	// 搜索
	$scope.search = function(page, rows) {
		goodsService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}

	
	$scope.img_entity={};
	// 上传文件
	$scope.uploadFile = function() {
		uploadService.uploadFile().success(function(response) {
			console.log(response)
			if (response.success) {
				$scope.img_entity.url = response.message;
			} else {
				alert(response.message)
			}

		}).error(function(){
			alert("上传发生错误")
		})
	}
	$scope.test=function(){
		var file = document.getElementById("file");
		// for IE, Opera, Safari, Chrome
        if (file.outerHTML) {
            file.outerHTML = file.outerHTML;
        } else { // FF(包括3.5)
            file.value = "";
        }
	}
	
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}}
	$scope.add_Img_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.img_entity);
	}
	
	$scope.dele_imgList=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1)
	}

	//一级列表
	$scope.selectItemCat1List=function(){
		itemCatService.findItemCatByParentId(0).success(
				function(response){
					$scope.itemCat1List=response;
				}
		)
	}

	
	//读取二级分类
	$scope.$watch('entity.goods.category1Id', function(newValue, oldValue) {   
		if(newValue==null){
			return;
		}
	    	//根据选择的值，查询二级分类
	    	itemCatService.findItemCatByParentId(newValue).success(
	    		function(response){
	    			$scope.itemCat2List=response; 	    			
	    		}
	    	);    	
	});  
	
	//读取三级分类
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		if(newValue==null){
			return;
		}
		itemCatService.findItemCatByParentId(newValue).success(
	    		function(response){
	    			$scope.itemCat3List=response; 	    			
	    		}
	    	); 
	})
	//模板ID
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		if(newValue==null){
			return;
		}
		itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.goods.typeTemplateId=response.itemCat.typeId;
				}
		)
	})
	
	//品牌列表,规格列表
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
		if(newValue==null){
			return;
		}
		//品牌列表
		typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.typeTemplate=response;
					$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
					if($location.search()['id']==null){
						//扩展属性
						$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
					}
				}
		)
		//规格列表
		typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.spceList=response;
				}
		)
	})
	
	//选择规格
	$scope.updateSpecAttribute=function($event,name,value){
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value)
			}else{
				object.attributeValue.splice(object.attributeName.indexOf(value),1);
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	
	//创建SKU列表
	$scope.createItemList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}]
		var item=$scope.entity.goodsDesc.specificationItems;
		
		for(var i=0;i<item.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,item[i].attributeName,item[i].attributeValue);
		}
	}
	
	addColumn=function(list,columnName,columnValue){
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<columnValue.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName]=columnValue[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	//状态
	$scope.status=["未审核",'已审核','审核未通过',"关闭"];
	
	
	$scope.itemCatList=[];
	//全部分类
	$scope.findItemCat=function(){
		itemCatService.findAll().success(
				function(response){
					for(var i=0;response.length;i++){
						if(response[i]==null){
							break;
						}
						$scope.itemCatList[response[i].id]=response[i].name;
					}
				}
		);
	}
	
	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items=$scope.entity.goodsDesc.specificationItems;
		var object=$scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return false;
		}
		if(object.attributeValue.indexOf(optionName)>=0){
			return true
		}
		return false;
	}

});






