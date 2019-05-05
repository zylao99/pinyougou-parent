app.controller('seckillGoodsController',function ($scope,$location,$interval,seckillGoodsService) {
    //写一个方法 当页面加载的时候调用查询数据展示

    $scope.findAll=function () {
        seckillGoodsService.findAll().success(
            function (response) {//List<>
                $scope.goodsList=response;
            }
        )
    }

    //写一个方法 当点击的时候 跳转到详情的页面

    $scope.findOne=function () {
        //从url中获取参数的值
        var id = $location.search()['id'];
        seckillGoodsService.findOne(id).success(
            function (response) {//tbseckillgoods
                $scope.entity=response;

                //1.先获取结束时间
                var enddate = new Date(response.endTime).getTime();//获取距离1970年到结束时间的毫秒数
                //2.再获取当前的时间
                var currenttime = new Date().getTime();//获取距离1970年 到当前的毫秒数
                //3.结束时间-当前的时间 得到的就是距离多久 换算成秒
               $scope.allSecond =Math.floor((enddate-currenttime)/1000);
                //4.调用$interval 倒计时

                //显示：距离 xxx天xx时xx分xx秒

                time= $interval(function(){
                    $scope.timeString=convertTimeString($scope.allSecond);
                    if($scope.allSecond>0){
                        $scope.allSecond =$scope.allSecond-1;
                    }else{
                        $interval.cancel(time);
                        alert("秒杀服务已结束");
                    }
                },1000);
            }
        )
    }


    //转换秒为   天小时分钟秒格式  XXX天 10:22:33
    convertTimeString=function(allsecond){
        var days= Math.floor( allsecond/(60*60*24));//天数
        var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小时数
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        if(days>0){
            days=days+"天 ";
        }
        if(hours<10){
            hours="0"+hours;
        }
        if(minutes<10){
            minutes="0"+minutes;
        }
        if(seconds<10){
            seconds="0"+seconds;
        }
        return days+hours+":"+minutes+":"+seconds;
    }


   /* $scope.second = 20;
    time= $interval(function(){
        console.log($scope.second);
        if($scope.second>0){
            $scope.second =$scope.second-1;
        }else{
            $interval.cancel(time);
            alert("秒杀服务已结束");
        }
    },1000);*/
   
   
   $scope.submitOrder=function (id) {
       seckillGoodsService.submitOrder(id).success(
           function (response) {//result
               if(response.success){
                   //下单成功
                  window.location.href="pay.html";
               }else{
                   if(response.message=='401'){
                       alert("请登录");

                       // http://localhost:9109/seckill-item.html#?id=5
                       var url = window.location.href;//获取当前的浏览器中的url

                       window.location.href="/page/login.do?url="+encodeURIComponent(url);

                   }else{
                       alert("下单失败");
                   }

               }
           }
       )
   }



})