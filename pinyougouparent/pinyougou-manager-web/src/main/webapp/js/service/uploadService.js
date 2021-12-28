app.service("uploadService",function ($http) {
    this.upload = function(){
        let formData = new FormData();
        formData.append("imgFile",file.files[0])

        return $http({
            url:"../upload.do",
            method:"POST",
            data:formData,
            headers : {
                "Content-Type":undefined
            },
            // 表单序列化
            transformRequest:angular.identity
        })
    }
})