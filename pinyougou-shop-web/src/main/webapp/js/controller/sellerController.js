app.controller('sellerController',function ($scope,sellerService) {
    //写一个方法 当点击入驻的时候调用  将数据存储到数据库中
    
    $scope.register=function () {
        sellerService.add($scope.entity).success(
            function (response) {//result
                if(response.success){
                    //跳转到登录页面
                    alert(response.message);
                    window.location.href="shoplogin.html";
                }else{
                    alert(response.message);
                }
            }
        )
    }
})