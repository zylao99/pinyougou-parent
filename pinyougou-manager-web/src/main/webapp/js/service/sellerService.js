//创建一给service
app.service('sellerService',function ($http) {
    //查询所有
    this.findAll =function () {
        return $http.get('../seller/findAll.do');
    };
    //分页
    this.findPage=function(page,rows){
        return $http.get('../seller/findPage.do?page='+page+'&rows='+rows);
    }
    //查询实体
    this.findOne=function (id) {
        return 	$http.get('../seller/findOne.do?id='+id);
    };
    //修改
    this.update = function (entity) {
        return $http.post('../seller/update.do',entity);
    }
    //增加
    this.add = function (entity) {
        return $http.post('../seller/add.do',entity);
    }
    //删除
    this.dele = function (ids) {
        return $http.post('../seller/delete.do',ids);
    }
    //搜索
    this.search = function (page,rows,searchEntity) {
        return $http.post('../seller/search.do?page='+page+'&rows='+rows,searchEntity);
    }

    //更改状态
    this.updateStatus=function(sellerId,status){
        return $http.get('../seller/updateStatus.do?sellerId='+sellerId+'&status='+status);
    }
    //查询销售额
    this.getSaleroom=function(sellerId){
        return $http.get('../seller/getSaleroom.do?sellerId='+sellerId);
    }
})