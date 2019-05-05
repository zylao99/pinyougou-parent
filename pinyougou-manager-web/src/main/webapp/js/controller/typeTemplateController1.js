 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承

    $scope.searchEntitya={status:'0'};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        typeTemplateService.search(page,rows,$scope.searchEntitya).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    $scope.updateStatus=function (status) {
        typeTemplateService.updateStatus($scope.selectIds,status).success(
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
