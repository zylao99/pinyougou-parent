app.service('uploadService',function ($http) {
    //文件上传
    this.uploadFile=function () {
        //1.模拟表单
        //2.设置mult-part/form-data
        //3.发送请求


        var formData = new FormData();//模拟出一个表单  h5的js对象   input type="text" name="username"
        formData.append("file",file.files[0]); //类似于拼接  input type="text" name="username"

        //上边的file.files[0]  ===>  file 表示的是input type ="file" 中的id的值   files[0]  表示获取选择的文件中一个文件对象
        return $http({
            method:'POST',
            url:"/upload.do",
            data: formData,//表单
            headers: {'Content-Type':undefined},//设置头  如果是undefine 默认是什么类型就设置为什么类型。设置mult-part/form-data
            transformRequest: angular.identity//设置传递请求的序列化方式
        });
    }
})