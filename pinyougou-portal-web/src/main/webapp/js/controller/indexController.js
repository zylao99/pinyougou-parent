app.controller('indexController',function ($scope,indexServcie) {


    
    //写一个方法 当页面加载的时候就调用 查询出轮播图对应下所有的广告列表 展示到页面

    $scope.contentListx=[];

   /* $scope.contentListx[1]=[{}];//这个是不是轮播图的广告列表
    $scope.contentListx[2]=[{}];//这个是不是今日推荐的广告列表*/


    $scope.findByCategoryId=function (categoryId) {
        indexServcie.findByCategoryId(categoryId).success(
            function (response) {//List<tbcontent>
                //$scope.contentList=response;

                $scope.contentListx[categoryId]=response;

            }
        )
    }
    //跳转到搜索结果页面
    $scope.doSearch=function () {
        window.location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})