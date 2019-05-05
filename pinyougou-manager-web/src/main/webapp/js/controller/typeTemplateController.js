 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

    /**
	 *
	 * var obj = {};
	 * obj.id=1;
	 *
	 * $scope.obj.id=1;
	 *
	 *
	 * var ef = obj.id;
	 *
	 *
	 *  $get().success();
	 *
	 * function abc(id){
	 * }
	 * var abc =function(){
	 * }
	 *
	 * $scope.abc=function(){
	 * }
	 *
	 *
     * @param page
     * @param rows
     */


	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;

                $scope.entity.specIds=angular.fromJson($scope.entity.specIds);
                $scope.entity.brandIds=angular.fromJson($scope.entity.brandIds);
                $scope.entity.customAttributeItems=angular.fromJson($scope.entity.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//从数据库中获取品牌的列表
	// 组合成格式为：[{id:1,text:'bug'},{id:2,text:'duplicate'},{id:3,text:'invalid'},{id:4,text:'wontfix'}] 数据

	$scope.findBrandOptionList=function () {
        brandService.findBrandOptionList().success(
        	function (response) {//List<Map>
                $scope.config3 = {
                    data: response
                };
            }
		)
    }

    $scope.findSpecOptionList=function () {
        specificationService.findSpecOptionList().success(
        	function (response) {//List<Map>
                $scope.config4 = {
                    data: response
                };
            }
		)
    }

    $scope.entity={customAttributeItems:[]};

	//添加行
	$scope.addTableRow=function () {
        $scope.entity.customAttributeItems.push({});
    }


    /**
	 *
	 * var obj={id:1};
	 *
	 * obj.id
	 * obj['id']
	 *
	 *
     * @param jsonString  [{"id":24,"text":"李宁"},{"id":2,"text":"华为"}]
	 *
	 * [{"id":24,"abc":"李宁"},{"id":2,"text":"华为"}]
     */
    $scope.jsonToString=function (jsonString,key) {
    	var fromJson = angular.fromJson(jsonString);

    	var str = "";
    	for(var i=0;i<fromJson.length;i++){
    		var obj = fromJson[i];//{"id":24,"text":"李宁"}
            str+=obj[key]+",";
		}

		if(str.length>=1) {
            str = str.substring(0, str.length - 1);
        }
		return str;
    }

	//数据导出
	$scope.exportExcel=function () {
		typeTemplateService.exportExcel($scope.selectIds).success(function (response) {
			if(response.success){
				alert(response.message);
			}else{
				alert(response.message);
			}
		})
	}
	//数据导入
	$scope.insertExcel=function () {
		typeTemplateService.insertExcel().success(function (response) {
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
