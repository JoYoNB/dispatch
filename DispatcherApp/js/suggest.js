mui.init({
	swipeBack: true //启用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});
mui.plusReady(function() {
	console.log(new Date().getTime() + "|意见反馈:plusReady");
});
//点击提交
mui(".mui-content").on("tap",".submit-btn",function(){
	//取消input框里的焦点
	$(".input-area input[type=text]").blur();
	var iptData = {
			feedback:$("#content").val(),
			phone:$("#phone").val(),
			email:$("#email").val()
	};
	if(validate(iptData)){
		var postData = {
			url:(httpServer + '/DispatcherAppWeb/common/addFeedback.json'),
			data:iptData,
			success:function(result){
				showErrorTips("反馈成功，感谢您的支持:)");
				mui.back();
			},
			error:function(){
				showErrorTips("系统异常，请稍后再试:(");
				mui.back();
			}
		};
		utils.ajaxFn(postData);
	}else{
		console.log("no");
	}
});

// 验证用户输入
function validate(postData) {
	console.info(JSON.stringify(postData));
	var symbol = 0;
	var msg = '';
	if(postData.feedback){
		if(!validator.isLength(postData.feedback,{min:1, max: 150})){
			msg = '意见反馈最长为150字';
			symbol++;
			//显示错误提示框
			showErrorTips(msg);
			return false;
		}
	}else{
		msg = '意见反馈不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}

	if(postData.phone){
		if(! validator.isMobilePhone(postData.phone,'any')){
			msg = '请输入合法的手机号码';
			symbol++;
			showErrorTips(msg);
			return false;
		}
	}
	
	if(postData.email){
		if(! validator.isEmail(postData.email)){
			msg = '请输入合法的邮箱';
			symbol++;
			showErrorTips(msg);
			return false;
		}
	}else{
		if(!postData.phone){
			msg = '手机号和邮箱请至少填写一项';
			symbol++;
			showErrorTips(msg);
			return false;
		}
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
