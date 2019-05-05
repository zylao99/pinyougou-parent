app.service('findOrdersService',function($http) {
    this.findAllOrders=function () {
        return $http.get('../findOrders/findAllOrders.do');
    }
})