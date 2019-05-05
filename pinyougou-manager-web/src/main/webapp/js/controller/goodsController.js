 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,itemCatService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    $scope.selectIds=[];
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={auditStatus:'0'};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


    $scope.status=['未审核','已审核','审核未通过','已关闭'];


    //页面加载 查询所有的商品的分类的列表  循环遍历  组合成  $scope.itemcatList=['',null，null,null。。。。。。...'手机']; 下标 是分类的ID  对应的下标的值 分类的名称

    $scope.itemcatList=[];

    $scope.findAllItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {//List<tbitemCat>
                for(var i=0;i<response.length;i++){
                    var itemCatojb = response[i];//有分类的名称 和ID
                    $scope.itemcatList[itemCatojb.id]=itemCatojb.name;
                }
            }
        )
    }
    
    $scope.updateStatus=function (status) {
		goodsService.updateStatus($scope.selectIds,status).success(
			function (response) {//result
				if(response.success){
                    $scope.selectIds=[];
					$scope.reloadList();
				}else{
					alert(response.message);
				}

            }
		)
    }
    
});	
