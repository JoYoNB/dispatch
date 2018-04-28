var old_back = mui.back;
mui.back = function(){
	thisView.hide("slide-out-right",200);
}
var subpage_style = {
	top: immersed + 56 + 'px',
	bottom: '0'
};
	
mui.plusReady(function() {
	thisView = plus.webview.currentWebview();
	var wv_list =plus.webview.create("selectOrderList.html","selectOrderList.html",subpage_style,{type:self.type});
	thisView.append(wv_list);
});


window.addEventListener("selected",function(e){
	var obj = {};
	obj = e.detail.detailsData;

	var view = plus.webview.getWebviewById('main.html');
	 
	 mui.fire(view, 'selected', {
        orderData: obj,
    });
    plus.nativeUI.toast("订单分配成功");
    setTimeout(function(){
    	mui.back();
    },1500);
});