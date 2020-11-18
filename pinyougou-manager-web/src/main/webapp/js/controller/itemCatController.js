 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response['itemCat'];		
				$scope.entity.typeId=response['typeTemplat'];
			}
		);				
	}
	//保存 
	$scope.save=function(){				
		if($scope.grade==1){
			$scope.entity.parentId=0;
		}
		if($scope.grade==2){
			$scope.entity.parentId=$scope.entity_1.id;
		}
		if($scope.grade==3){
			$scope.entity.parentId=$scope.entity_2.id;
		}
		$scope.entity.typeId=$scope.entity.typeId.id;
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
					$scope.setGrade($scope.grade);
					if($scope.grade==1){
						$scope.findItemCatByParentId(0);
					}else if($scope.grade==2){
						$scope.selectGradeList($scope.entity_1);
					}else{
						$scope.selectGradeList($scope.entity_2);
					}
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.setGrade($scope.grade);
					if($scope.grade==1){
						$scope.findItemCatByParentId(0);
					}else if($scope.grade==2){
						$scope.selectGradeList($scope.entity_1);
					}else{
						$scope.selectGradeList($scope.entity_2);
					}
					$scope.selectIds=[];
				}else{
					alert(response.message);
				}					
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//根据几级菜单获取列表
	$scope.findItemCatByParentId=function(parentId){
		itemCatService.findItemCatByParentId(parentId).success(
				function(response){
					$scope.list=response;
				}
		);
	}
	
	//定义等级
	$scope.grade=1;
	//设置等级
	$scope.setGrade=function(value){
		$scope.grade=value;
	}
	
	$scope.selectGradeList=function(parentEntity){
		if($scope.grade==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grade==2){
			$scope.entity_1=parentEntity;
			$scope.entity_2=null;
		}
		if($scope.grade==3){
			$scope.entity_2=parentEntity;
		}
		
		$scope.findItemCatByParentId(parentEntity.id);
	}
    
	$scope.typeTempList={data:[]};
	$scope.selectTypeTemp=function(){
		typeTemplateService.selectTypeTemp().success(
				
				function(response){
					$scope.typeTempList={data:response};
				}		
		)
	}

});	
