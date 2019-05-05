//服务层
app.service('orderService',function($http){

	//找到所有的该商户的订单
	this.findSellerOrders=function () {
		return $http.get('../sellerOrder/findSellerOrders.do')
    }

	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../order/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../order/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../order/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../order/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../order/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../order/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../order/search.do?page='+page+"&rows="+rows, searchEntity);
	}

	//分时间段统计
	this.getSalesSituation=function (startTime, endTime, goodsId) {
		return $http.post('../sellerOrder/salesSituation.do?startTime='+startTime+'&endTime='+endTime+'&goodsId='+goodsId)
    }
});
