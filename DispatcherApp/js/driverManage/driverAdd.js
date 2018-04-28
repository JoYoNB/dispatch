var driverId = null;

mui('.mui-scroll-wrapper').scroll();
mui.plusReady(function() {
	var self = plus.webview.currentWebview();
    driverId = self.driverId;
    if(driverId){ 
   		getDriverInfo(driverId);
    }
    //选择关联车辆
	$(".mui-content").on("tap",".select-vehicle",function(){
		utils.openView({
			url:"selectVehicle.html",
			id:"selectVehicle.html"
		});
	});
	window.addEventListener('doit', function(e){
		$(".vehicleText").val(e.detail.name);
		$(".vehicleId").val(e.detail.id);
	});

	//选择部门
	$(".mui-content").on("tap",".select-dept",function(){
		utils.openView({
			url:"selectDept.html",
			id:"selectDept.html"
		});
	});

	//日期
	var dtpicker = new mui.DtPicker({
		    type: "date",//设置日历初始视图模式 
		});
		$(".time").click(function(){
			var self = this;
			dtpicker.show(function(items){
				$(self).val(items);
			});
		});
		
	//点击提交
	mui(".mui-content").on("tap",".submit-btn",function(){
		$(".input-area input[type=text]").blur();
		var iptData = {
				name:$(".driverName").val(),
				deptId:$(".deptId").val(),
				vehicleId:$(".vehicleId").val(),
				phone:$(".phone").val(),
				time:$(".time").val()
		};
		if(driverId){
			iptData.driverId = driverId;
		}
		if(validate(iptData)){
			console.log("发送给后台的数据:"+iptData);
			var postData = {
				url:'',
				data:iptData,
				success:function(){
					
				},
				error:function(){
					
				}
			};
			utils.ajaxFn(postData);
		}else{
			console.log("no");
		}
	});
});

//根据id获取司机信息
function getDriverInfo(driverId){
	utils.ajaxFn({
		url:'./js/data/driverData.json',
		type:'get',
		data:{driverId:driverId},
		success:function(result){
			if(result.code == 0){
				//获取数据成功，渲染数据
				renderDriverInfo(result);
			}	
		},
		error:function(error){
			console.log(error);
		}
	});
}

function renderDriverInfo(result){
	var data = result.data;
	$(".driverName").val(data.driverName ? data.driverName : "");
	$(".deptName").val(data.deptName ? data.deptName : "");
	$(".deptId").val(data.deptId ? data.deptId : "");
	$(".vehicleText").val(data.vehicleText ? data.vehicleText : "");
	$(".vehicleId").val(data.vehicleId ? data.vehicleId : "");
	$(".phone").val(data.phone ? data.phone : "");
	$(".time").val(data.time ? data.time : "");
}
// 验证用户输入
function validate(postData) {
	var symbol = 0;
	var msg = '';
	if(postData.name){
		if(!validator.isLength(postData.name,{min:1, max: 20})){
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

	if(! postData.dept){
		msg = '部门不能为空';
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

	if(postData.phone){
		if(! validator.isMobilePhone(postData.phone,'zh-CN')){
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
	if(symbol == 0){
		return true;
	}else{
		return false;
	}
}

 // 显示错误提示框
 function showErrorTips(msg){
 	mui.toast(msg) 
 }


//处理弹出遮盖输入框软键盘
var _focusElem = null; 
document.body.addEventListener("focus", function(e) {
    _focusElem = e.target || e.srcElement;
}, true);

var originalHeight=document.documentElement.clientHeight || document.body.clientHeight;
window.onresize=function(){
	 var  resizeHeight=document.documentElement.clientHeight || document.body.clientHeight;
	 if(resizeHeight*1<originalHeight*1){ 
	         _focusElem.scrollIntoView(false);
	        document.getElementById("subimt-box").style.position = "static";
	  }else{
	        document.getElementById("subimt-box").style.position = "absolute";
	  }
}
