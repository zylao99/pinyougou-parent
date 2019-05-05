 //控制层 
app.controller('orderItemController' ,function($scope,orderItemService){

	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
        orderItemService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};



    $scope.dateFilterFind=function () {
        var date = $("#date").val();
        orderItemService.dateFilterFind(date).success(
            function(response){
                $scope.itemList=response;
            }
        )
	}
});	
