app.controller("contentController",function($scope,contentService){

    // 所有的广告列表
    $scope.contentList = []

    $scope.findByCategoryId=function(categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response
        });
    }

    $scope.search=function(){
        let keywords = $scope.keywords;
        location.href="http://localhost:9104/search.html#?keywords=" + keywords
    }
})