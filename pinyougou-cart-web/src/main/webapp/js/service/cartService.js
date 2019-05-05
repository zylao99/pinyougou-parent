app.service('cartService',function ($http) {
    this.findCartList=function () {
        return $http.get('/cart/findCartList.do');
    }
    this.addGoodsToCartList=function (itemId,num) {
        return $http.get('/cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    this.findAddressList=function () {
        return $http.get('/address/findAddressList.do');
    }

    //提交订单
    this.add=function (order) {
        return $http.post('/order/add.do',order);
    }
})