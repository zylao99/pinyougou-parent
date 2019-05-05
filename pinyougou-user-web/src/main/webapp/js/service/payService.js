app.service('payService',function ($http) {
    this.createNative=function (orderId) {
        return $http.get('/pay/createNative.do?orderId='+orderId);
    }

    this.queryStatus=function (out_trade_no) {
        return $http.get('/pay/queryStatus.do?out_trade_no='+out_trade_no);
    }
})