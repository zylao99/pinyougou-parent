//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }
    $scope.typeTemplateListTextId=[];
    $scope.typeTemplateListConfig={data:[]};
    $scope.findAllTemplate=function () {
        typeTemplateService.findAll().success(
            function (response) {



                for(var i=0;i<response.length;i++){
                    $scope.typeTemplateListTextId.push({"id":response[i].id,"text":response[i].name});
                }

                console.log($scope.typeTemplateListTextId);
                $scope.typeTemplateListConfig={data:$scope.typeTemplateListTextId};
            }
        )
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(
            function (response) {//List
                $scope.itemCatList = response;
            }
        )
    }

    //点击的时候调用   影响变量的值 
    $scope.grade = 1;//等级

    $scope.setGrade=function (grade) {
        $scope.grade=grade;
    }

    $scope.entity_1 = null;
    $scope.entity_2 = null;

    $scope.selectList = function (p_entity) {
        // 如果等级是1
        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }


        // 如果等级是2
        if ($scope.grade == 2) {
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null
        }
        // 如果等级是3
        if ($scope.grade == 3) {
            $scope.entity_2 = p_entity;
        }

        $scope.findByParentId(p_entity.id);

    }

    //数据导出
    $scope.exportExcel=function () {
        itemCatService.exportExcel($scope.selectIds).success(function (response) {
            if(response.success){
                alert(response.message);
            }else{
                alert(response.message);
            }
        })
    }
    //数据导入
    $scope.insertExcel=function () {
        itemCatService.insertExcel().success(function (response) {
            if(response.success){
                alert(response.message);
                //刷新页面
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        })

    }
});	
