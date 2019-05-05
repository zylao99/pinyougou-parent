//服务层
app.service('specificationService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../specification/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../specification/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../specification/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../specification/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../specification/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../specification/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../specification/search.do?page='+page+"&rows="+rows, searchEntity);
	}
	this.findSpecOptionList=function () {
		return $http.get('../specification/findSpecOptionList.do')
    }

	//数据导入
	this.insertExcel=function(){
		var formData=new FormData();
		formData.append("file",file.files[0]);
		return $http({
			method:'POST',
			url:"../specification/insertExcel.do",
			data: formData,
			headers: {'Content-Type':undefined},
			transformRequest: angular.identity
		});
	}
	//数据导出
	this.exportExcel=function (ids) {
		return $http.post('../specification/exportExcel.do',ids);
	}
    this.updateStatus=function(ids,status){
        return $http.get('../specification/updateStatus.do?ids='+ids+"&status="+status);
    }

});
