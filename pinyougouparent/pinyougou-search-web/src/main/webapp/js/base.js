var app=angular.module("pinyougou",["infinite-scroll"]);//构建模块

// 过滤器实现信任HTML
app.filter("trustAsHtml",["$sce",function ($sce) {
    return function (data) { // data过滤前数据
        return $sce.trustAsHtml(data)
    }
}])