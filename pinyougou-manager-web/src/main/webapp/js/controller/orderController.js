 //控制层 
app.controller('orderController' ,function($scope,$controller,orderService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
        orderService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){
        orderService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
        orderService.findOne(id).success(
			function(response){//tbOrder
				$scope.tbOrder= response;
			}
		);				
	}

	


    $scope.searchEntity={auditStatus:'0'};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){
        orderService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    $scope.status=['未付款','已付款','未发货','已发货','交易成功','交易关闭','已发货'];
    $scope.type=['在线支付','货到付款']
    
});	
