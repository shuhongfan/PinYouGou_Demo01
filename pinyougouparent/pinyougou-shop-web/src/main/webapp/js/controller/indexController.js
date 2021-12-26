app.controller("indexController",function($scope,loginService){
    $scope.findLoginName = function() {
        loginService.loginName().success(function(response){
            $scope.loginName = response.loginName
        });
    }
})