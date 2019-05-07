//服务层
app.service('addressService',function($http){
	    	
	//查询订单收货人地址列表
	this.findAddressList=function(){
		return $http.get('../address/findAddressList');
	}

    //定义根据id查询
    this.findOne = function (id) {
        return $http.get("../address/findOne/" + id);
    };

    //增加
    this.add=function(address){
        return  $http.post('../address/add',address );
    }
    //修改
    this.update=function(address){
        return  $http.post('../address/update',address );
    }

    //删除
    this.delete=function(ids){
        return  $http.get('../address/delete/'+ids );
    }
    //设置默认
    this.isDefault=function(id){
        return  $http.get('../address/isDefault/'+id );
    }
});
