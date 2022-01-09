app.controller("searchController",function($scope,searchService){
    // 搜索
    $scope.search=function() {
        searchService.search($scope.searchMap).success(
            function(response) {
                $scope.resultMap = response
            }
        )
    }

    // 搜索对象
    $scope.searchMap={
        keywords:"",
        brand: "",
        category: "",
        spec:{},
        price:""
    }

    // 添加搜索项
    $scope.addSearchItem = function(key,value) {
        if (key=='brand' || key=='category' || key=='price'){ // 如果点击的是品牌或分类
            $scope.searchMap[key] = value;
        } else {// 如果用户点击的是规格
            $scope.searchMap.spec[key]=value;
        }
        // 重新查询
        $scope.search()
    }

    // 移除搜索项
    $scope.removeSearchItem = function (key) {
        if (key=='brand' || key=='category' || key=='price'){// 如果点击的是品牌或分类
            $scope.searchMap[key]=""
        } else { // 如果用户点击的是规格
            delete $scope.searchMap.spec[key]
        }
        // 重新查询
        $scope.search()
    }
})