//首页控制器
app.controller('payController',function($scope,  payService){
    // 生产二维码
    $scope.createNative=function () {
        payService.createNative().success(function(response) {
            $scope.out_trade_no = response.out_trade_no
            $scope.total_fee  = (response.total_fee/100).toFixed(2)

            var qr = new QRious({
                element: document.getElementById("qrious"),
                size:250,
                value: response.code_url,
                level: "H"
            })
            // 查询订单状态
            queryPayStatus(response.out_trade_no)
        })
    }

    // 查询订单状态
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function(response) {
            if (response.success){
                location.href = "paysuccess.html"
            } else {
                location.href = "payfail.html"
            }
        })
    }
});