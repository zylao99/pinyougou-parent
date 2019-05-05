 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//使用$location 来获取url中的参数



	//查询实体 
	$scope.findOne=function(){

		var id = $location.search()['id'];

		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;

				//将数据展示到富文本编辑器中
				editor.html($scope.entity.goodsDesc.introduction);

				//将商品的图片列表 转出JSON
                $scope.entity.goodsDesc.itemImages=angular.fromJson( $scope.entity.goodsDesc.itemImages);

                //扩张属性转JSON
                $scope.entity.goodsDesc.customAttributeItems=angular.fromJson( $scope.entity.goodsDesc.customAttributeItems);

                $scope.entity.goodsDesc.specificationItems=angular.fromJson( $scope.entity.goodsDesc.specificationItems);

                //需要获取到SKU的列表

                var itemList = $scope.entity.itemList;//[{spec:{\\}},{}]
                for(var i=0;i<itemList.length;i++){
                    itemList[i].spec=angular.fromJson(itemList[i].spec);
				}
			}
		);
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
            //1.先获取富文本编辑器中的内容（html）
            var introduction = editor.html();
            //2.内容赋值给entity中的属性 introduction
            $scope.entity.goodsDesc.introduction=introduction;
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
            //1.先获取富文本编辑器中的内容（html）
            var introduction = editor.html();
            //2.内容赋值给entity中的属性 introduction
            $scope.entity.goodsDesc.introduction=introduction;
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    editor.html('');
                    //跳转到商品的列表页面
					window.location.href="goods.html";
				}
			}		
		);				
	}


	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    $scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},itemList:[]};
	//添加商品  当点击保存的时候调用

	$scope.add=function () {
		//1.先获取富文本编辑器中的内容（html）
        var introduction = editor.html();
		//2.内容赋值给entity中的属性 introduction
        $scope.entity.goodsDesc.introduction=introduction;
        goodsService.add($scope.entity).success(
        	function (response) {//result
				if(response.success){
					alert("成功");
					//待定
					//清空
                    $scope.entity={goods:{},goodsDesc:{},itemList:[]};
                    editor.html('');
				}else{
                    alert(response.message);
				}
            }
		)
    }

    //写 一个方法 当点击上传按钮的时候调用  获取返回的结果   有图片的路径
	
	$scope.uploadFile=function () {
			uploadService.uploadFile().success(
				function (response) {//result
					if(response.success){
                        //$scope.imageurl= response.message;//图片的路径
						$scope.image_entity.url=response.message;
					}else{
						alert(response.message);
					}

                }
			)
		
    }

    $scope.addTableRow=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    
    //写 一个方法 当页面加载 就调用 获取到商品分类的列表  展示下拉框
	
	$scope.findItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {//List<tbitemcat>
				$scope.itemCat1List=response;
            }
		)
    }
    
    //监控 一级分类的id的变化  而 查询一级分类下的二级分类的列表 
	
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {

		//发送请求 获取一级分类下的二级分类列表
		if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List<tbitemcat>
                    $scope.itemCat2List=response;
                }
            )
		}
    });


	//监听二级分类的id变化  获取三级分类列表
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {

        //发送请求 获取一级分类下的二级分类列表
        if(newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List<tbitemcat>
                    $scope.itemCat3List=response;
                }
            )
        }
    });

    //监听三级分类的id变化  获取三级分类对应的模板id
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {

        //发送请求 获取一级分类下的二级分类列表
        if(newValue!=undefined){
           	itemCatService.findOne(newValue).success(
           		function (response) {//tbitemcat
						$scope.entity.goods.typeTemplateId=response.typeId;
                }
			)
        }
    });

    //监听模板的ID变化  获取模板对象  对象中有品牌列表
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {

        //发送请求 获取模板的对象
        if(newValue!=undefined){
            typeTemplateService.findOne(newValue).success(
                function (response) {//typeTemplate 对象
                    $scope.typeTemplate=response;
                    //转换string 为json
                    $scope.typeTemplate.brandIds=angular.fromJson( $scope.typeTemplate.brandIds);

                    //entity.goodsDesc.customAttributeItems
					//判断如果是 新增 就有这行代码
					if($scope.entity.goods.id==null || $scope.entity.goods.id==undefined){
                        $scope.entity.goodsDesc.customAttributeItems=angular.fromJson( $scope.typeTemplate.customAttributeItems);
					}

                }
            );

            //发送请求获取规格的列表  循环遍历变量展示在页面

			typeTemplateService.findSpecList(newValue).success(
				function (response) {//List<map>//[{"id":27,"text":"网络",options:[{},{}]},{"id":32,"text":"机身内存"}]
						$scope.specList=response;
                }
			)



        }
    });



    //写一个方法  点击复选框的时候调用

    /**
	 *$scope.entity.goodsDesc.specificationItems=
	 * [
		 {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
		 {"attributeValue":["16G","32G"],"attributeName":"机身内存"}
     	]
	 *
     */
	//伪代码
    $scope.updateSpecAttribute=function ($event,specName,specValue) {
    	//searchBykey 方法 用来从已有的数组中查询 对象    对象存在就返回对象  不存在返回null
		var searchObj=$scope.searchBySpecName('attributeName',specName,$scope.entity.goodsDesc.specificationItems);
		if(searchObj!=null){

			if($event.target.checked){
				//选中
                //先获取对象 对象中添加数据 -》规格选项
                //searchObj =====>{"attributeValue":["移动3G"],"attributeName":"网络"}
                searchObj.attributeValue.push(specValue);
			}else{
				//	取消勾选
                searchObj.attributeValue.splice( searchObj.attributeValue.indexOf(specValue),1);
                //判断如果length 为 0 删除对象
				if(searchObj.attributeValue.length==0)
                	$scope.entity.goodsDesc.specificationItems.splice( $scope.entity.goodsDesc.specificationItems.indexOf(searchObj),1);
			}
		}else{
			//对象不存在
			//直接添加一个对象   {"attributeValue":["移动3G"],"attributeName":"网络"}   ---->要有 规格的名称    和  规格的选项的名称
            $scope.entity.goodsDesc.specificationItems.push({"attributeValue":[specValue],"attributeName":specName});
		}
    }

    //预期的数据：
	//$scope.entity.itemList=[
   // {spec:{"网络":"移动3G","机身内存":"16G"},price:0,num:1,status:'1',isDefault:'0'},
   // {spec:{"网络":"移动4G","机身内存":"16G"},price:0,num:1,status:'1',isDefault:'0'}
//    ]



    //写一个方法  当点击复选框的时候就要重新从头生成

	$scope.createItemList=function () {
		//定义个初始化对象
        $scope.entity.itemList=[{spec:{},price:0,num:1,status:'1',isDefault:'0'}];

        // 循环遍历$scope.entity.goodsDesc.specificationItems=
		//   [{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},{"attributeValue":["16G"],"attributeName":"机身内存"}]

        var specificationItems = $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<specificationItems.length;i++){
			var obj = specificationItems[i]; //{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}

			//写一个方法  内部    拼接
            $scope.entity.itemList=addColumn($scope.entity.itemList,obj.attributeName,obj.attributeValue);
		}


    }
    //内部    做拼接  克隆
    /**
	 *
     * @param list  [{spec:{},price:0,num:1,status:'1',isDefault:'0'}];
     * @param columnName   "网络"
     * @param attributeValues  ["移动3G","移动4G"]
     * @returns {Array}
     */
    addColumn=function (list,columnName,attributeValues) {
		var newList=[];
		for (var j=0;j<list.length;j++){
			var oldRow = list[j];//{spec:{},price:0,num:1,status:'1',isDefault:'0'}
			for(var n=0;n<attributeValues.length;n++){
				//拼接成   {spec:{"网络":"移动3G","机身内存":"16G"},price:0,num:1,status:'1',isDefault:'0'}
				//克隆
				var newRow = angular.fromJson(angular.toJson(oldRow));
                newRow.spec[columnName]=attributeValues[n];
                newList.push(newRow);
			}
		}
		return newList;

    }

    $scope.checkAttributeValue = function (specName, specValue) {//网络  移动3G
        //从变量中 查询对象是否存在
        var obj = $scope.searchBySpecName("attributeName", specName, $scope.entity.goodsDesc.specificationItems);
        if (obj == null) {
            return false;
        } else {
            if (obj.attributeValue.indexOf(specValue) != -1) {
                return true;
            } else {
                return false;
            }
        }

    }


    $scope.status=['未审核','已审核','审核未通过','已关闭'];


    //页面加载 查询所有的商品的分类的列表  循环遍历  组合成  $scope.itemcatList=['',null，null,null。。。。。。...'手机']; 下标 是分类的ID  对应的下标的值 分类的名称

    $scope.itemcatList=[];
    
    $scope.findAllItemCatList=function () {
        itemCatService.findAll().success(
        	function (response) {//List<tbitemCat>
				for(var i=0;i<response.length;i++){
					var itemCatojb = response[i];//有分类的名称 和ID
                    $scope.itemcatList[itemCatojb.id]=itemCatojb.name;
				}
            }
		)
    }

	//获取销售额统计图
	$scope.getSaleroom=function(){
		var date = document.getElementById("date").value;
		goodsService.getSaleroom(date).success(
			function (response) {
				//legend数据
				$scope.legendData = response.legendData;
				//series数据
				$scope.seriesData = response.seriesData;
				$scope.lineChart();
			}
		)
	}
	//折线图
	$scope.lineChart=function () {
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
				'echarts/chart/line' // 使用柱状图就加载bar模块，饼状图加载pie模块,按需加载
			],
			function (ec) {
				// 基于准备好的dom，初始化echarts图表
				var myChart = ec.init(document.getElementById('main'));

				var option = {
					title: {
						text: '堆叠区域图'
					},
					tooltip : {
						trigger: 'axis',
						axisPointer: {
							type: 'cross',
							label: {
								backgroundColor: '#6a7985'
							}
						}
					},
					legend: {
						data:['商品销售额']
					},
					toolbox: {
						feature: {
							saveAsImage: {}
						}
					},
					grid: {
						left: '3%',
						right: '4%',
						bottom: '3%',
						containLabel: true
					},
					xAxis : [
						{
							type : 'category',
							boundaryGap : false,
							data : $scope.legendData
						}
					],
					yAxis : [
						{
							type : 'value'
						}
					],
					series : [
						{
							name:'商品销售额',
							type:'line',
							stack: '总量',
							label: {
								normal: {
									show: true,
									position: 'top'
								}
							},
							areaStyle: {normal: {}},
							data:$scope.seriesData
						}
					]
				};

				// 为echarts对象加载数据
				myChart.setOption(option);
			}
		);
	}


});	
