//定义一个服务层service
app.service("uploadService", function ($http) {
    //定义上传文件服务层方法
    this.uploadFile = function () {
        //创建表单对象FormData
        var formData = new FormData();
        var file = document.querySelector('input[type=file]').files[0];
        //向表单对象中添加表单对象
        formData.append("file", file);
        //发送请求，实现文件上传
        return $http({
            method: 'POST',
            url: "../upload/pic",
            data: formData,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        });
    }


});