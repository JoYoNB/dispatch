mui.init({
	swipeBack: true //启用右滑关闭功能
});

var driverId = null;
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();

mui.plusReady(function() {
	console.log(new Date().getTime() + "|司机个人资料:plusReady");
	// 加载用户信息
	var userData = {
		url:httpServer + '/DriverAppWeb/information/getDriverInfo.json',
		data:{},
		dataType:"json",
		type:"get",
		success:renderUserInfo,
		error:function(error){console.log(error)}
	};
	utils.loadData(userData);
	
});

//渲染用户信息
function renderUserInfo(result){
	if(! result){
		return;
	}
	var data = result.data;
	driverId=data.driverId;
	$(".driverName").val(data.driverName ? data.driverName : "");
	$(".deptName").val(data.deptName ? data.deptName : "");
	$(".deptId").val(data.deptId ? data.deptId : "");
	$(".vehicleText").val(data.vehicleText ? data.vehicleText : "");
	$(".vehicleId").val(data.vehicleId ? data.vehicleId : "");
	$(".phone").val(data.phoneNo ? data.phoneNo : "");
	$(".time").val(data.entryTime?data.entryTime.substring(0,10):"");
	$(".vehicleText").val(data.plateNo?data.plateNo:"");
	$(".vehicleId").val(data.vehicleId);
}

//选择关联车辆
mui(".mui-content").on("tap",".select-vehicle",function(){
	utils.openNewWindow({
		url:"selectVehicle.html",
		id:"selectVehicle.html"
	});
});
//加载选中车辆
window.addEventListener('doit', function(e){
	$(".vehicleText").val(e.detail.name);
	$(".vehicleId").val(e.detail.id);
});

//日期
var dtpicker = new mui.DtPicker({
    type: "date",//设置日历初始视图模式 
});
//加载选中日期
$(".time").click(function(){
	var self = this;
	dtpicker.show(function(items){
		console.log(items);
		$(self).val(items);
	});
});
	

// 验证用户输入
function validate(postData) {
	var symbol = 0;
	var msg = '';
	if(postData.driverName){
		if(!validator.isLength(postData.driverName,{min:1, max: 20})){
			msg = '司机姓名长度在1到20之间';
			symbol++;
			//显示错误提示框
			showErrorTips(msg);
			return false;
		}
	}else{
		msg = '司机姓名不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}

	if(! postData.vehicleId){
		msg = '关联车辆不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}

	if(postData.phoneNo){
		if(! validator.isMobilePhone(postData.phoneNo,'zh-CN')){
			msg = '请输入合法的手机号码';
			symbol++;
			showErrorTips(msg);
			return false;
		}
	}else{
		msg = '手机号码不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	
	if(! postData.entryTime){
		msg = '日期不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	
	
	if(symbol == 0){
		return true;
	}else{
		return false;
	}
}


//点击提交
mui(".mui-content").on("tap",".submit-btn",function(){
	var btnArray=['取消','确认'];
	var title='';
	mui.confirm("保存修改？",title,btnArray,function(e){
		if(e.index==1){
			//取消input框里的焦点
			$(".input-area input[type=text]").blur();
			var iptData = {
					driverName:$(".driverName").val(),
					vehicleId:$(".vehicleId").val(),
					phoneNo:$(".phone").val(),
					entryTime:$(".time").val()+" 00:00:00"
			};
			if(!driverId){
				return ;	
			}
			iptData.driverId = driverId;
			if(validate(iptData)){
				console.log("发送给后台的数据:"+iptData);
				var postData = {
					url:httpServer + '/DriverAppWeb/information/updateDriverInfo.json',
					data:iptData,
					success:function(result){
						console.log(JSON.stringify(result));
						if(result.code==0){
							showErrorTips("成功");
							location.href="driverEdit.html";
						}else if(result.code==130006){
							showErrorTips("车辆已被其他司机关联,请重新选择关联车辆");
						}else{
							showErrorTips("失败");
						}
					},
					error:function(){
						showErrorTips("失败");
					}
				};
				utils.loadData(postData);
			}else{
				console.log("no");
			}
		}else{
			//取消
			return ;
		}
	});
});


 // 显示错误提示框
 function showErrorTips(msg){
 	mui.toast(msg) 
 }

var originalHeight=document.documentElement.clientHeight || document.body.clientHeight;
window.onresize=function(){
    //软键盘弹起与隐藏  都会引起窗口的高度发生变化
    var  resizeHeight=document.documentElement.clientHeight || document.body.clientHeight;
    if(resizeHeight*1<originalHeight*1){ //resizeHeight<originalHeight证明窗口被挤压了
            plus.webview.currentWebview().setStyle({
                height:originalHeight
            });
      }
}