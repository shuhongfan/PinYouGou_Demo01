app.controller("indexController",function($scope,loginService){
    $scope.findLoginName = function() {
        loginService.loginName().success(function(response){
            $scope.loginName = response.loginName
        });
    }

    $scope.search=function () {
        // 获取关键字
        let keywords = $scope.keywords;
        location.href="http://localhost:9104/search.html#?keywords="+keywords
    }
})