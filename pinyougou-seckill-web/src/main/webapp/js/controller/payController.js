app.controller('payController',function ($scope,$location,$interval,payService) {
    //写一个方法 当页面加载的时候调用 发送请求 生成二维码的连接
    //通过qrious.js来生成展示

    $scope.createNative=function () {
        payService.createNative().success(
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

                //如果超过5分钟就说明超时 3秒钟发送一次请求

                $scope.second = 100;
                time= $interval(function(){

                    if($scope.second>0){
                        $scope.second =$scope.second-1;
                        //调用查询的方法发送请求获取状态
                        payService.queryStatus($scope.out_trade_no).success(
                            function (response) {//result
                                if(response.success){
                                    location.href="paysuccess.html#?money="+$scope.total_fee;
                                    //订单支付成功：  更新订单的状态 保存订单到数据库中  清理redis中的订单
                                }else{
                                    //401  402
                                    alert("未支付");
                                }
                            }
                        )
                    }else{
                        //停止调用
                        $interval.cancel(time);
                        //跳转到支付失败页面

                        //超时 需要删除redis中的订单 恢复库存
                        alert("秒杀服务已结束");
                    }
                },3000);

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