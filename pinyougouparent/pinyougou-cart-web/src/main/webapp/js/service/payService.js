//服务层
app.service('cartService',function($http){
    // 二维码
    this.createNative = function () {
        return $http.get('psy/createNative.do')
    }

    // 查询订单状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get('psy/queryPayStatus.do?out_trade_no='+out_trade_no)
    }

});
