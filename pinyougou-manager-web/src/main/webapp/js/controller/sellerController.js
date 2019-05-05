//2.创建一个控制器
app.controller('sellerController',function ($scope,$controller,sellerService) {
    $controller('baseController',{$scope:$scope});
    //查询所有不分页
    $scope.findAll = function(){
        sellerService.findAll().success(
            function (response) {//List<tbbrand>
                $scope.list=response;
            }
        )

    }

    //分页
    $scope.findPage=function(page,rows){
        sellerService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //写一个方法 用于保存品牌   当点击按钮的时候调用
    $scope.entity={};
    //写一个方法 用于 点击修改的时候 根据品牌的ID 发送请求 获取数据库的数据 展示
    $scope.findOne =function(id){
        sellerService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    }
    $scope.save=function () {
        var serviceObject;//服务层对象
        if($scope.entity.specification.id!=null){//如果有ID
            serviceObject=sellerService.update( $scope.entity ); //修改
        }else{
            serviceObject=sellerService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    alert(response.message);
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //写一个方法 当点击删除按钮的时候 调用 发送请求 将数组数据传递给后台 后台删除数据
    $scope.dele=function () {
        sellerService.dele($scope.selectIds).success(
            function (response) {
                if(response.success){
                    $scope.selectIds=[];
                    //刷新页面
                    $scope.reloadList();
                    alert(response.message);
                }else{
                    alert(response.message);
                }
            }
        )
    }
    $scope.searchEntity={};
    //定义一个方法 当 搜索的时候进行调用
    $scope.search = function (page,rows) {
        sellerService.search(page,rows,$scope.searchEntity).success(
            function (response) {//pageResult
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )
    }
    $scope.entity={specification:{},specificationOptionList:[]};//组合对象
    //新增选项行
    $scope.addTableRow=function(){
        $scope.entity.specificationOptionList.push({});
    }


    //获取销售额统计图
    $scope.getSaleroom=function(id,nickName){
        sellerService.getSaleroom(id).success(
            function (response) {
                $scope.legendData = response.legendData;
                $scope.seriesData = response.seriesData;
                $scope.nickName = nickName;
                $scope.pieChart();
            }
        )
    }
    //饼状图
    $scope.pieChart=function () {
        // 路径配置
        require.config({
            paths: {
                echarts: 'http://echarts.baidu.com/build/dist'
            }
        });

        // 使用
        require(
            [
                'echarts',
                'echarts/chart/pie' // 使用柱状图就加载bar模块，饼状图加载pie模块,按需加载
            ],
            function (ec) {
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(document.getElementById('main'));

                var option = {
                    title : {
                        text:$scope.nickName,
                        subtext: '每月销售额',
                        x:'center'
                    },
                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient : 'vertical',
                        x : 'left',
                        data:$scope.legendData
                    },
                    toolbox: {
                        show : true,
                        feature : {
                            mark : {show: false},
                            dataView : {show: false, readOnly: false},
                            magicType : {
                                show: false,
                                type: ['pie', 'funnel'],
                                option: {
                                    funnel: {
                                        x: '25%',
                                        width: '50%',
                                        funnelAlign: 'left',
                                        max: 1700
                                    }
                                }
                            },
                            restore : {show: true},
                            saveAsImage : {show: true}
                        }
                    },
                    calculable : true,
                    series : [
                        {
                            name:'销售比例',
                            type:'pie',
                            center: ['50%', '45%'],
                            radius: '50%',
                            data:$scope.seriesData
                        }
                    ]
                };

                // 为echarts对象加载数据
                myChart.setOption(option);
            }
        );
    }

})