//服务层
app.service('orderService', function ($http) {

    //分页
    this.findOrders=function(pageNum,pageSize){
        return $http.get('../order/findByPage/'+pageNum+'/'+pageSize);
    }
});
