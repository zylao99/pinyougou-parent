 //控制层 
app.controller('specificationController' ,function($scope,$controller   ,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承

    $scope.searchEntitya={status:'0'};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        specificationService.search(page,rows,$scope.searchEntitya).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    $scope.updateStatus=function (status) {
        specificationService.updateStatus($scope.selectIds,status).success(
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
