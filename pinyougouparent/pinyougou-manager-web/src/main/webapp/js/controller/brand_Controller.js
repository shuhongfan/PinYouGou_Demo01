// 构建模块的前端控制器
app.controller("brandController",function($scope,$controller,brandService){
    $controller("baseController",{$scope:$scope});

    $scope.findAll = function() {
        brandService.findAll().success(function(response){
            $scope.list = response
        })
    }

    // 分页
    $scope.findPage = function(page,rows) {
        brandService.findPage(page,rows).success(function(response){
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }

    // 新增品牌数据
    $scope.add = function() {
        var object = null;
        if ($scope.entity.id != null){
            object = brandService.update($scope.entity)
        } else {
            objet = brandService.add($scope.entity)
        }
        object.success(function(response){
            if (response.success){
                $scope.reloadList()
            } else {
                alert(response.message)
            }
        })
    }

    $scope.findOne = function(id) {
        brandService.findOne(id).success(function(response) {
            $scope.entity = response
        })
    }


    $scope.dele = function() {
        brandService.dele($scope.selectIds).success(function(response){
            if (response.success){
                // 刷新列表
                $scope.reloadList()
                $scope.selectIds=[]
            } else {
                alert(response.message)
            }
        })
    }

    // 条件查询+分页
    $scope.search = function(page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.list = response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    }
})