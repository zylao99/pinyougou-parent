 //控制层 
app.controller('contentController' ,function($scope,contentService){
	

	//定义数组，存储页面广告数据
	$scope.catList = [];
    //根据分类id查询广告内容信息
	$scope.findContentListByCategoryId=function(categoryId){
		contentService.findContentListByCategoryId(categoryId).success(
			function(response){
				//把分类id作为数组角标，在角标所对应位置存储广告集合
				$scope.catList[categoryId] = response;
			}			
		);
	};


	//angularJS参数路由：在静态页面传递参数必须加上#
	//语法：#?key=value
	
	//搜索跳转方法
	$scope.solrSearch = function () {
		//转发请求，把搜索请求转发搜索系统，且传递参数
		window.location.href="http://localhost:8083/search.html#?keywords="+$scope.keywords;
    }

    
});	
