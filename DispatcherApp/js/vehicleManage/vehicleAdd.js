mui('.mui-scroll-wrapper').scroll();

mui.plusReady(function() {
	var self = plus.webview.currentWebview();
    var vId = self.vehicleId;
    var name = self.name;

	if(vId){ 
		getVehicleInfo(vId);
	}

	//加载车辆类型
	utils.ajaxFn({
		url:"",
		data:{},
		success:function(result){
			var selectVehicleList = [];
			if (result.code == 0) {
				var selectVehiclePicker = new mui.PopPicker(); 
				var data = result.data;
				selectVehicleList.push({vehicleId:data,vehicleId,vehicleType:vehicleType});
				selectVehiclePicker.setData(selectVehicleList);
			}
		}
	});

	$(".mui-content").on("tap",".selectVehicleType",function(){
		selectVehiclePicker.show(function(items) {
			$("#VehicleTypeText").val(items[0].text);
			$("#VehicleTypeId").val(items[0].value);
		});
	});
	//删除图片
	$(".mui-content").on("tap",".delete-img",function(){
		files = null;
		$(".delete-img").hide();
		$(".add-pic").find("img").remove();
		return false;
	});

	$(".mui-content").on("tap",".add-pic",function(){
		getPicture();
	});

	//选择关联车辆
	$(".mui-content").on("tap",".selectCargoAttr",function(){
		utils.openNewWindow({
			url:"selectVehicleAttr.html",
			id:"selectVehicleAttr.html"
		});
	});
	window.addEventListener('doit', function(e){
		$(".cargoAttrText").val(e.detail.inputText);
		$(".cargoAttrId").val(e.detail.inputId);
	});
		
	$("#mainContent").on("tap",".submit-btn",function(){
		$(".input-area input[type=text]").blur();
		var iptData = {
				name:$(".driver-name").val(),
				dept:$(".dept").val(),
				vehicle:$(".vehicle").val(),
				phone:$(".phone").val(),
				time:$(".time").val()
		};
		if(validate(iptData)){
			var postData = {
				url:'',
				data:iptData,
				success:function(){

				},
				error:function(){

				}
			};
			utils.loadData(postData);
		}else{
			console.log("no");
		}
	});
});

function getImg(){
	var files = null;
	var server = '上传图片的接口地址';
	
}

function getPicture(){
	console.log('从相册中选择图片：');
    plus.gallery.pick(function(path){
    	  console.log(path);
          showImg( path );
          files = {name:"上传图片的name",path:path} ;
          $(".delete-img").show();
    }, function(e){
    	console.log('取消选择图片');
    }, {filter:'image'});
}

function showImg(url){
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

//根据id 查询车辆信息
function getVehicleInfo(vehicleId){
	utils.loadData({
		url:'./js/data/vehicleDetail.json',
		type:'get',
		data:{vehicleId:vehicleId},
		success:function(result){
			if(result.code == 0){
				renderVehicleInfo(result);
			}	
		},
		error:function(error){
			console.log(error);
		}
	})
}

function renderVehicleInfo(result){
	var data = result.data;
	if(data.img){
		var _img = '<img src="'+data.img+'" alt="" />';
		$(".add-icon-box").append(_img);
	}
	
	$(".plateNo").val(data.plateNo ? data.plateNo : '');
	$(".pn").val(data.pn ? data.pn : '');
	$(".maxWeight").val(data.maxWeight ? data.maxWeight : '');
	$(".totalLength").val(data.totalLength ? data.totalLength : '');
	$(".length").val(data.length ? data.length : '');
	$(".width").val(data.width ? data.width : '');
	$(".height").val(data.height ? data.height : '');
	$(".payload").val(data.payload ? data.payload : '');
	$(".payloadRatio").val(data.payloadRatio ? data.payloadRatio : '');
	$(".minR").val(data.minR ? data.minR : '');
	$(".powerDensity").val(data.powerDensity ? data.powerDensity : '');
}

// 验证用户输入
function validate(postData) {
	var msg = '';
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
	         _focusElem.scrollIntoViewIfNeeded();
	 }
}