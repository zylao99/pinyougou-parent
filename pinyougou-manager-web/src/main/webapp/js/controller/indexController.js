app.controller('indexController',function ($scope,loginService) {
    //页面一加载 就调用这个方法 发送请 获取到用户的信息 展示页面中

    $scope.getLoginName=function () {
        loginService.getLoginName().success(
            function (response) {//Map
                $scope.loginName=response.loginName;
            }
        )
    }
})