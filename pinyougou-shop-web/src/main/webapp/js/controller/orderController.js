 //控制层 
app.controller('orderController' ,function($scope,$controller,$location,orderService){
	
	$controller('baseController',{$scope:$scope});//继承
	//找到该商户的所有的订单
    $scope.findSellerOrders=function(){
    	orderService.findSellerOrders().success(
    		function (response) {
				$scope.list=response;
            }
		)
	}
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


	//从地址栏得到id
	$scope.goodsId=$location.search()['id'];
	//分时段统计商品销售额
	$scope.getSalesSituation=function(startTime,endTime,goodsId){
		orderService.getSalesSituation(startTime,endTime,goodsId).success(
			function (response) {
				$scope.salesList=response;

            }
		)
	}

	//初始化的到销售情况
	$scope.getSalesSituationINIT=function(){
        orderService.getSalesSituation('2016-01-01','2019-12-12',$scope.goodsId).success(
            function (response) {
                $scope.salesList=response;

            }
        )
	}


    $scope.status=['未付款','已付款','未发货','已发货','交易成功','交易关闭','已发货'];
    $scope.type=['在线支付','货到付款']
    
});	
