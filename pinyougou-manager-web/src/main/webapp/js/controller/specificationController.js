 //控制层 
app.controller('specificationController' ,function($scope,$controller   ,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		specificationService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		specificationService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		specificationService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

	$scope.entity={optionList:[]};
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.specification.id!=null){//如果有ID
			serviceObject=specificationService.update( $scope.entity ); //修改  
		}else{
			serviceObject=specificationService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	//var obj = {}
	// obj.id=1
	//写一个方法 当点击新增选项的按钮的时候 向已有的数组中添加一个空对象{}
	$scope.addTableRow=function () {
        $scope.entity.optionList.push({});
    }

    //移除对象
	$scope.deleTableRow=function (index) {
        $scope.entity.optionList.splice(index,1);
    }
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		specificationService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		specificationService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//数据导出
	$scope.exportExcel=function () {
		specificationService.exportExcel($scope.selectIds).success(function (response) {
			if(response.success){
				alert(response.message);
			}else{
				alert(response.message);
			}
		})
	}
	//数据导入
	$scope.insertExcel=function () {
		specificationService.insertExcel().success(function (response) {
			if(response.success){
				alert(response.message);
				//刷新页面
				$scope.reloadList();
			}else{
				alert(response.message);
			}
		})

	}
});	
