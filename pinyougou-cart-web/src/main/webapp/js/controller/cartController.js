app.controller('cartController',function ($scope,cartService) {




    //写一个方法 当页面加载了之后就调用 查询当前用户的购物车的列表
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {//List<cart>
                $scope.cartList=response;

                $scope.totalNum=0;
                $scope.totalMoney=0;
                for(var i=0;i<$scope.cartList.length;i++){
                    var cart = $scope.cartList[i];

                    for(var j=0;j<cart.orderItemList.length;j++){
                        $scope.totalNum+=cart.orderItemList[j].num;
                        $scope.totalMoney+=cart.orderItemList[j].totalFee;
                    }

                }
            }
        )
    }

    //写一个方法  当点击+  或者-  就相当于向已有购物车添加商品

    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {//result
                if(response.success){
                    //刷新页面 重新查询购物车的列表
                    $scope.findCartList();
                }else{
                    alert(response.message);
                }

            }
        )
    }
    
    //获取地址列表的方法 在页面初始化的时候调用
    $scope.findAddressList=function () {
        cartService.findAddressList().success(
            function (response) {//List<tbaddress>
                  $scope.addressList=response;

                  for(var i=0;i< $scope.addressList.length;i++){
                      var obj = $scope.addressList[i];//
                      if(obj.isDefault=='1'){
                          $scope.address=obj;
                      }
                  }

            }
        )
    }

    // 先定义一个变量 用于存储当前选中的地址对象
    $scope.address={};
    //写一个方法 当点击的时候调用 去影响变量的值 将被点击的地址对象赋值给变量 address
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }

    //判断当前被点击的地址对象 是否 匹配上当前地址对象变量  如果匹配 true

    $scope.isSelected=function (address) {
        if($scope.address==address){
            return true;
        }
        return false;
    }

    $scope.order={paymentType:'1'};//订单的变量对象

    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }

    $scope.submitOrder=function () {
        //设置选中的地址到order中
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;

        cartService.add( $scope.order).success(
            function (response) {//
                if(response.success){
                   window.location.href="pay.html";
                }else{
                    alert(response.message);
                }

            }
        )
    }



})