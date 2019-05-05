app.controller('searchController',function ($scope,$location,searchService) {

    //先定义一个变量  用于绑定查询到的条件
    $scope.searchMap={'category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':10,'sortField':'','sortType':'' };

    //写一个方法  当点击搜索的按钮时候调用 发送请求 获取结果
    $scope.search=function () {
        searchService.search($scope.searchMap).success(
            function (response) {//Map
                $scope.resultMap = response;
                //构建分页的标签
                buildPageLabel();
            }
        )
    }

    buildPageLabel = function () {
        $scope.pageLabel = [];
        var firstPage = 1;//开始页码
        var lastPage = $scope.resultMap.totalPages;//结束页码

        $scope.isPre=false;
        $scope.isNext=false;


        /*+ 如果总页数<=5页     显示全部。

         + 如果总页数>5
         + 如果当前页为 <3 (显示前5页)
         + 如果当前页为 >总页数-2 (显示后5页)		   总共  100页  当前页是99     96 97 98 99 100
         + 否则	 显示中间的页码  （显示以当前页为中心的 5页）*/


        if ($scope.resultMap.totalPages > 5) {
            if($scope.searchMap.pageNo<3){
                //显示前5页
                firstPage=1;
                lastPage=5;
                $scope.isPre=false;
                $scope.isNext=true;

            }else if($scope.searchMap.pageNo> $scope.resultMap.totalPages-2){//    96 97 98 99 100
                //展示后5页
                firstPage= $scope.resultMap.totalPages-4;
                lastPage= $scope.resultMap.totalPages;

                $scope.isPre=true;
                $scope.isNext=false;
            }else{
                //    4 5 6 7 8
                firstPage= $scope.searchMap.pageNo-2;

                lastPage=$scope.searchMap.pageNo+2;
                $scope.isPre=true;
                $scope.isNext=true;
            }

        } else {
            //总页数<=5页   显示前5页
            $scope.isPre=false;
            $scope.isNext=false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //点击品牌  或者 商品分类的时候调用  影响变量searchMap里的值
    $scope.addSearchItem=function (key,value) {
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]=value;

        }else{
            $scope.searchMap.spec[key]=value;
        }

        $scope.search();

    }
    
    $scope.removeSearchItem=function (key) {
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    
    //写一个方法 点击页码的时候调用 改变pageNo的值 发送请求 查询 
    
    $scope.searchByPage=function (page) {
        //判断数据是否为数字
        if(isNaN(page)==false){
            if(page<1){
                $scope.searchMap.pageNo=1;
            }
            if(page>$scope.resultMap.totalPages){
                page=$scope.resultMap.totalPages;
            }
            $scope.searchMap.pageNo=parseInt(page);//改变值
            $scope.search();//发送请求 查询数据
        }else{
            alert("请输入数字");
        }


    }

    $scope.clear=function () {
        $scope.searchMap={'keywords':$scope.searchMap.keywords,'category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':10,'sortField':'','sortType':'' };
    }

    $scope.doSort=function (sortField,sortType) {
        $scope.searchMap.sortType=sortType;
        $scope.searchMap.sortField=sortField;
        //发送请求 进行查询
        $scope.search();
    }

    $scope.isKeywordsIsBrand=function(){
        //从品牌列表中 循环遍历 和关键字对比匹配 如果匹配成功 返回true
        var brandList = $scope.resultMap.brandList;//[{id:1,'text':联想}]
        for(var i=0;i<brandList.length;i++){
            var obj = brandList[i];//{id:1,'text':联想}
            if($scope.searchMap.keywords.indexOf(obj.text)!=-1){
                //有
                $scope.searchMap.brand=obj.text;
                return true;
            }
        }

        return false;
    }

    //页面加载的时候就要调用
    $scope.searchByKeywords=function () {
        var keywords = $location.search()['keywords'];
        //立即发送请求查询数据
        $scope.searchMap.keywords=keywords;
        $scope.search();
    }


    
   


})