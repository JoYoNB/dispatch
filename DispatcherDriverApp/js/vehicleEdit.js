mui.init({
	swipeBack: true //启用右滑关闭功能
});

var vehicleId = null;
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	console.log(new Date().getTime() + "|车辆信息:plusReady");
	
	// 加载车辆信息
	var vehicleData = {
		url:httpServer + '/DriverAppWeb/information/getVehicleInfo.json',
		data:{},
		dataType:"json",
		type:"post",
		success:renderVehicleInfo,
		error:function(error){console.log(error)}
	};
	utils.loadData(vehicleData);
	
	//加载车辆类型
	var vehicleTypeData={
		url:httpServer + '/DriverAppWeb/common/getVehicleTypeList.json',
		data:{},
		dataType:"json",
		type:"get",
		success:renderVehicleTypeInfo,
		error:function(error){console.log(error)}
	};
	utils.loadData(vehicleTypeData);
	
	// initPageLanguage();	
});

//初始化车辆信息
function renderVehicleInfo(result){
	var data = result.data;
	if(data.imageUrl){
		var _img = '<img style="width:100%;height:100%;position:absolute;top:0;left:0;" src="'+data.imageUrl+'" alt="" />';
		$(".add-icon-box").hide();
		$(".add-pic").append(_img);
		$(".delete-img").show();
	}
	vehicleId=data.vehicleId;
	$(".plateNo").val(data.plateNo ? data.plateNo : '');
	$(".equipmentPn").val(data.equipmentPn ? data.equipmentPn : '');
	$(".vehicleWeightMax").val(data.vehicleWeightMax ? data.vehicleWeightMax : '');
	$(".vehicleLength").val(data.vehicleLength ? data.vehicleLength : '');
	$(".vehicleInsideLength").val(data.vehicleInsideLength ? data.vehicleInsideLength : '');
	$(".vehicleInsideWidth").val(data.vehicleInsideWidth ? data.vehicleInsideWidth : '');
	$(".vehicleInsideHeight").val(data.vehicleInsideHeight ? data.vehicleInsideHeight : '');
	$(".carryWeigthMax").val(data.carryWeigthMax ? data.carryWeigthMax : '');
	$(".weigthUseFactor").val(data.weigthUseFactor ? data.weigthUseFactor : '');
	$(".swerveRadiusMin").val(data.swerveRadiusMin ? data.swerveRadiusMin : '');
	$(".powerRate").val(data.powerRate ? data.powerRate : '');
	$("#VehicleTypeText").val(data.vehicleTypeName);
	$("#VehicleTypeId").val(data.vehicleTypeId);
	//遍历组装载货类型
	var carryTypeName="";
	var carryTypeId="";
	data.carryTypes.forEach(function(obj){
		carryTypeName=carryTypeName+obj.carryTypeName+"/";
		carryTypeId=carryTypeId+obj.carryTypeId+",";
	})
	if(carryTypeName){
		carryTypeName=carryTypeName.substring(0,carryTypeName.length-1);
	}
	if(carryTypeId){
		carryTypeId=carryTypeId.substring(0,carryTypeId.length-1);
	}
	$(".carryTypeName").val(carryTypeName);
	$(".carryTypeId").val(carryTypeId);
}
//车辆类型控件
var selectVehiclePicker = new mui.PopPicker(); 
function renderVehicleTypeInfo(result){
	var data = result.data.list;
	var vehicleTypeData= new Array();
	for(var i = 0;i < data.length; i++) {
		vehicleTypeData.push({"value":data[i].id,"text":data[i].name});
	}
	selectVehiclePicker.setData(vehicleTypeData);	
}

//选中车辆类型
$(".mui-content").on("tap",".selectVehicleType",function(){
	selectVehiclePicker.show(function(items) {
		$("#VehicleTypeText").val(items[0].text);
		$("#VehicleTypeId").val(items[0].value);
		//返回 false 可以阻止选择框的关闭
		//return false;
	});
});
	
	
//选择货载属性
mui(".mui-content").on("tap",".selectCargoAttr",function(){
	utils.openNewWindow({
		url:"selectCarryType.html",
		id:"selectCarryType.html"
	});
});
window.addEventListener('doit', function(e){
	$(".carryTypeName").val(e.detail.inputText);
	$(".carryTypeId").val(e.detail.inputId);
});
	
//点击提交
mui(".mui-content").on("tap",".submit-btn",function(){
	var btnArray=['取消','确认'];
	var title='';
	mui.confirm("保存修改？",title,btnArray,function(e){
		if(e.index==1){
			//取消input框里的焦点
			$(".input-area input[type=text]").blur();
			var iptData = {
					plateNo:$(".plateNo").val(),
					equipmentPn:$(".equipmentPn").val(),
					vehicleTypeId:$("#VehicleTypeId").val(),
					carryTypeIds:$(".carryTypeId").val(),
					vehicleWeightMax:$(".vehicleWeightMax").val(),
					vehicleLength:$(".vehicleLength").val(),
					vehicleInsideLength:$(".vehicleInsideLength").val(),
					vehicleInsideWidth:$(".vehicleInsideWidth").val(),
					vehicleInsideHeight:$(".vehicleInsideHeight").val(),
					carryWeigthMax:$(".carryWeigthMax").val(),
					weigthUseFactor:$(".weigthUseFactor").val(),
					swerveRadiusMin:$(".swerveRadiusMin").val(),
					powerRate:$(".powerRate").val(),
					vehicleImage:imgData
			};
			if(vehicleId){
				iptData.vehicleId=vehicleId;
			}
			console.log(JSON.stringify(iptData));
			if(validate(iptData)){
				var postData = {
					url:httpServer + '/DriverAppWeb/information/updateVehicleInfo.json',
					data:iptData,
					type:'post',
					success:function(result){
						console.log(JSON.stringify(result));
						if(result.code==0){
							showErrorTips("成功");
							location.href="vehicleEdit.html";
						}else if(result.code==130001){
							showErrorTips("车牌号已存在");
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
				console.log("校验不通过");
			}
		}else{
			//点击取消
			return ;
		}
	});
	
});

// 验证用户输入
function validate(postData) {
	var symbol = 0;
	var msg = '';
	if(!postData.plateNo){
		msg = '车牌号不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.equipmentPn ){
		msg = 'PN号不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleTypeId ){
		msg = '车辆类型不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.carryTypeIds ){
		msg = '载货类型不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleWeightMax ){
		msg = '最大允许总质量不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleLength ){
		msg = '整车总长不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleWeightMax ){
		msg = '最大允许总质量不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleInsideLength ){
		msg = '货箱内部尺寸(长度)不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleInsideWidth ){
		msg = '货箱内部尺寸(宽度)不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.vehicleInsideHeight ){
		msg = '货箱内部尺寸(高度)不能为空';
		symbol++;
		showErrorTips(msg);
		return false;
	}
	if(!postData.carryWeigthMax ){
		msg = '载质量不能为空';
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
	var imgData='';
	//上传头像图片 
    function uploadHead(imgPath) { 
        console.log("imgPath = " + imgPath); 
        var image = new Image(); 
        image.src = imgPath; 
        image.onload = function() { 
                imgData = getBase64Image(image); 
                console.log("imgData===>"+imgData);
                /*在这里调用上传接口*/ 
//              mui.ajax("图片上传接口", { 
//                  data: { 
//                       
//                  }, 
//                  dataType: 'json', 
//                  type: 'post', 
//                  timeout: 10000, 
//                  success: function(data) { 
//                      console.log('上传成功'); 
//                  }, 
//                  error: function(xhr, type, errorThrown) { 
//                      mui.toast('网络异常，请稍后再试！'); 
//                  } 
//              }); 
        } 
    } 
    //将图片压缩转成base64 
    function getBase64Image(img) { 
        var canvas = document.createElement("canvas"); 
        var width = img.width; 
        var height = img.height; 
        // calculate the width and height, constraining the proportions 
        if (width > height) { 
            if (width > 100) { 
                height = Math.round(height *= 100 / width); 
                width = 100; 
            } 
        } else { 
            if (height > 100) { 
                width = Math.round(width *= 100 / height); 
                height = 100; 
            } 
        } 
        canvas.width = width;   /*设置新的图片的宽度*/ 
        canvas.height = height; /*设置新的图片的长度*/ 
        var ctx = canvas.getContext("2d"); 
        ctx.drawImage(img, 0, 0, width, height); /*绘图*/ 
        var dataURL = canvas.toDataURL("image/png", 1); 
        return dataURL.replace("data:image/png;base64,", ""); 
    }    



var files = null;
var server = '上传图片的接口地址';
//选择图片
mui(".mui-content").on("tap",".add-pic",function(){
	console.log("选择相册照片");
	getPicture();
});
//删除图片
mui(".mui-content").on("tap",".delete-img",function(){
	console.log("删除图片");
	files = null;
	$(".delete-img").hide();
	$(".add-pic").find("img").remove();
	$(".add-icon-box").show();
	imgData='';
	return false;
});

//从相册中选照片
function getPicture(){
	console.log('从相册中选择图片：');
    plus.gallery.pick(function(path){
    	  console.log(path);
          showImg( path );
          files = {name:"上传图片的name",path:path} ;
          $(".delete-img").show();
           uploadHead(path);
    }, function(e){
    	console.log('取消选择图片');
    }, {filter:'image'});
};



//显示选择的图片
function showImg(url){
	// 兼容以"file:"开头的情况
	if(0!=url.indexOf('file://')){
		url='file://'+url;
	}
	if($(".add-pic").find("img").length == 0){
		var _img = '<img style="width:100%;height:100%;position:absolute;top:0;left:0;" src="'+url+'" alt="" />';
		$(".add-pic").append(_img);
	}else{
		$(".add-pic").find("img").attr("src",url);
	}
}

/*// 上传文件
function upload(){
	if(files.length<=0){
		plus.nativeUI.alert("没有添加上传文件！");
		return;
	}
	console.log("开始上传：");
	var wt=plus.nativeUI.showWaiting();
	var task=plus.uploader.createUpload(server,
		{method:"POST"},
		function(t,status){ //上传完成
			if(status==200){
				console.log("上传成功："+t.responseText);
				
			}else{
				console.log("上传失败："+status);
				wt.close();
			}
		}
	);
	task.addData("client","HelloH5+");
	task.addData("uid",getUid());
	for(var i=0;i<files.length;i++){
		var f=files[i];
		task.addFile(f.path,{key:f.name});
	}
	task.start();
}*/


 // 显示错误提示框
 function showErrorTips(msg){
 	mui.toast(msg) 
 }

var originalHeight=document.documentElement.clientHeight || document.body.clientHeight;
window.onresize=function(){
    //软键盘弹起与隐藏  都会引起窗口的高度发生变化
//  var  resizeHeight=document.documentElement.clientHeight || document.body.clientHeight;
//  if(resizeHeight*1<originalHeight*1){ //resizeHeight<originalHeight证明窗口被挤压了
//          plus.webview.currentWebview().setStyle({
//              height:originalHeight
//          });
//    }
//  document.activeElement.scrollIntoView(true);
}
window.onresize=function(){
	document.activeElement.scrollIntoView(true);	
}
