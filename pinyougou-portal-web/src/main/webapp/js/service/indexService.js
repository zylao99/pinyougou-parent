app.service('indexServcie',function ($http) {
    this.findByCategoryId=function (categoryId) {
        return $http.get('/index/findByCategoryId.do?categoryId='+categoryId);
    }
})