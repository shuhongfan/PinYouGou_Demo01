// 构建模块的前端控制器
// 父控制器
app.controller("baseController",function($scope){
    $scope.searchEntity={}

    // 分页配置
    $scope.paginationConf = {
        currentPage: 1, // 当前页
        totalItems: 10, // 总记录数
        itemsPerPage :10,  // 每页记录数
        perPageOptions: [10,20,30,40,50], // 页面选项
        onChange: function(){ // 当页码发生变化的时候自动触发的方法
            $scope.reloadList()
        }
    }

    // 重新加载记录
    $scope.reloadList = function() {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    }

    // 选中的ID数组
    $scope.selectIds=[]
    $scope.updateSelection = function($event,id) {
        // 判断是否选中
        if ($event.target.checked){
            $scope.selectIds.push(id);
        } else {
            var idIndex = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idIndex,1);
        }
    }

    // 提取json中的数据
    $scope.jsonToString = function(jsonString,key) {
        var json = JSON.parse(jsonString);
        var value =""
        for (let i = 0; i < json.length;i++) {
            if (i>0) {
                value += ","
            }
            value+=json[i][key]
        }
        return value;
    }

    // 在集合中查询某字段的值为某值的对象
    $scope.searchObjectByKey=function(list,key,value) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key]==value){
                return list[i]
            }
        }
        return null;
    }
})