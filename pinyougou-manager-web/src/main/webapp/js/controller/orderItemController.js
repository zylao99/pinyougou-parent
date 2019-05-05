 //控制层 
app.controller('orderItemController' ,function($scope,orderItemService){


    $scope.dateFilterFind=function () {
        var date = $("#date").val();
        orderItemService.dateFilterFind(date).success(
            function(response){
                $scope.list=response;
            }
        )
	}
});	
