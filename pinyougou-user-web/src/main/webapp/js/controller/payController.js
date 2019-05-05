app.controller('payController',function ($scope,$location,payService) {
    //写一个方法 当页面加载的时候调用 发送请求 生成二维码的连接
    //通过qrious.js来生成展示

    $scope.createNative=function () {
        var orderId=$location.search()['orderId'];
        payService.createNative(orderId).success(
            function (response) {//Map
                $scope.out_trade_no=response.out_trade_no;
                $scope.total_fee=response.total_fee/100;//单位是分--->元


                //生成二维码展示
                var qrious = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                //需要调用方法 查询支付的状态
                $scope.queryStatus();
            }
        )
    }

    $scope.queryStatus=function () {
        payService.queryStatus($scope.out_trade_no).success(
            function (response) {//Result
                if(response.success){
                    //支付成功
                    location.href="paysuccess.html#?money="+$scope.total_fee;
                }else{
                    if(response.message=="支付超时"){
                        //重写生成支付二维码
                        $scope.createNative();
                    }else{
                        location.href="payfail.html";
                    }
                }

            }
        )
    }

    $scope.loadMoney=function () {
       return $location.search()['money'];
    }

})