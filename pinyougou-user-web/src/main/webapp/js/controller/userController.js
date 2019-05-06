 //控制层 
app.controller('userController' ,function($scope,userService,findOrdersService,loginService){

//获取用户登录信息
	$scope.showName = function () {
		loginService.loadLoginName().success(function (data) {
			$scope.loginName = data.loginName;
			$scope.loginPic = data.loginPic;
			$scope.findOne($scope.loginName);
			$scope.findAddressList();
		})
	};



	//控制层
	app.controller('findOrdersController' ,function($scope,findOrdersService) {

		$scope.findAllOrders=function(){
			findOrdersService.findAllOrders().success(
				function (result) {
					$scope.Orderlist=result;
				}
			)
		}

		$scope.topayhtml=function (orderId) {
			window.location="/pay.html#?orderId="+orderId;
		}
	})














	//写一个方法  当点击注册的时候调用 发送请求 将数据存储到数据库

	$scope.register=function () {
		//先进行校验
		if($scope.entity.password!=$scope.confirmPassword){
			alert("密码要一致");
			return;
		}
        userService.add($scope.entity,$scope.code).success(
        	function (response) {//result
				if(response.success){
					//要跳转到登录的页面
					alert("要登录")
				}else{
					alert(response.message);

				}
            }
		)
    }

    $scope.createSmsCode=function () {
		userService.createSmsCode($scope.entity.phone).success(
			function (response) {//result
				alert(response.message);
            }
		)
    }


});	
