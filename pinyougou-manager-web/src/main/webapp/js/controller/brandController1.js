 //控制层 
app.controller('brandController' ,function($scope,$controller,brandService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.searchEntity={status:'0'};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		brandService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.updateStatus=function (status) {
        brandService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if(response.success){
                    $scope.selectIds=[];
                    $scope.reloadList();
                    //window.location.href="goods.html";
                    alert(response.message);
                }else {
                    alert(response.message);
                }
            }
        )
    }


    
});	
