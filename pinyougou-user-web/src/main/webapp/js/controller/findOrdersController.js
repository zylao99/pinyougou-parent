//控制层
app.controller('findOrdersController' ,function($scope,findOrdersService) {

    $scope.findAllOrders=function(){
        findOrdersService.findAllOrders().success(
            function (result) {
                $scope.Orderlist=result;
            }
        )
    }

    $scope.topayhtml=function (orderId) {
        window.location="/pay.html#?orderId="+orderId;
    }
})