 //控制层 
app.controller('userController' ,function($scope,$controller,userService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
        userService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){
        userService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
        userService.findOne(id).success(
			function(response){//tbOrder
				$scope.tbOrder= response;
			}
		);				
	}

	


    $scope.searchEntity={auditStatus:'0'};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){
        userService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    $scope.status=['正常','非正常'];



	$scope.findNumber=function () {
		userService.findNumber().success(
			function (response) {
                $scope.numMap=response;
				$scope.findNum();
            }
		)
    }

    //初始化值
    $scope.numMap={
        impNum:0,
        activeNum:0,
        noActiveNum:0
	};
    $scope.findNum=function () {
        //指定图标的配置和数据
        var option = {
            title:{
                text:'【用户活跃度】统计'
            },
            series:[{
                name:'访问量',
                type:'pie',
                radius:'70%',
                data:[
                    {value:$scope.numMap.impNum,name:'非正常数量'},
                    {value:$scope.numMap.activeNum,name:'活跃用户数量'},
                    {value:$scope.numMap.noActiveNum,name:'非活跃用户数量'},
                ]
            }]
        };
        //初始化echarts实例
        var myChart = echarts.init(document.getElementById('chartmain'));

        //使用制定的配置项和数据显示图表
        myChart.setOption(option);
    }
});	
