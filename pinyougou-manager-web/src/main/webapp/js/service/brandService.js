//服务层
app.service('brandService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../brand/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../brand/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../brand/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../brand/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../brand/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../brand/search.do?page='+page+"&rows="+rows, searchEntity);
	}

	this.findBrandOptionList=function () {
		return $http.get('../brand/findBrandOptionList.do');
    }

	//数据导入
	this.insertExcel=function(){
		var formData=new FormData();
		formData.append("file",file.files[0]);
		return $http({
			method:'POST',
			url:"../brand/insertExcel.do",
			data: formData,
			headers: {'Content-Type':undefined},
			transformRequest: angular.identity
		});
	}
	//数据导出
	this.exportExcel=function (ids) {
		return $http.post('../brand/exportExcel.do',ids);
	}
    this.updateStatus=function(ids,status){
        return $http.get('../brand/updateStatus.do?ids='+ids+"&status="+status);
    }

});
