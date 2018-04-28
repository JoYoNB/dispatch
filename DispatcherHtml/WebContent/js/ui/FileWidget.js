(function(){
	var _filewidget={
		id:null,
		el:null,
		config:null,
		defaultDescription:"请上传大小不超过3M的附件，格式为zip、png、jpg。",
		fileCount:0,
		valueList:{},
		isBatch:true,//是否支持批量
		
		init:function(c){
			var self=this;
			var _c=c||{};
			self.config=_c;
			if(_c.isBatch==false){
				self.isBatch=false;
			}
			
			self._render();
			self._bind();
		},
		_render:function(){
			var self=this;
			var c=self.config;
			
			if(!$(self.el).hasClass("upload-box")){
				//加上默认样式
				$(self.el).addClass("upload-box")
			}
			if(!$(self.el).hasClass("clearfix")){
				//加上默认样式
				$(self.el).addClass("clearfix")
			}
			
			var _description=c.description||self.defaultDescription;
			
			var _html='<p class="upload-tips">'+_description+'</p>'
				+'<div id="fileList_'+self.id+'" class="upload-file-box">'
				//+'' //图片载体
				+'</div>'
				//上传按钮
				+'<div id="uploadff_'+self.id+'" class="upload-img-btn">'
				+'	<img src="/images/upload_icon.png" alt="">'
				+'	<p class="img-btn-tips">点击上传</p>'
				//+'	<input id="uploadFile_'+self.id+'" type="file">'
				+'</div>'
				//点击查看大图
				+'<div id="preview_'+self.id+'" class="preview-img">'
				+'	<div id="previewclose_'+self.id+'" class="close-preview-img"><span class="close-icon">x</span></div>'
				+'	<div id="previmgBtn_'+self.id+'" class="prev-img"><span class="prev-img-icon"></span></div>'
				+'	<div class="big-img-box">'
				+'		<img id="viewimg_'+self.id+'" src="../images/_pic2.jpg" alt="">'
				+'		<p id="viewimgname_'+self.id+'"></p>'
				+'      <input id="ctViewImg_'+self.id+'" type="hidden" value="" />'
				+'	</div>'
				+'	<div id="nextimgBtn_'+self.id+'" class="next-img"><span class="next-img-icon"></span></div>'
				+'</div>'
				//克隆体，值域
				+'<div id="valueList_'+self.id+'" class="hide"></div>';
				
			$(self.el).html(_html);
		},
		_bind:function(){
			var self=this;
			
			//文件上传按钮事件
			$("#uploadff_"+self.id).click(function(){
				//var fileEl='<input id="xxt" type="file" />';
				//显示小图片
				var fileItemId=self._genFileItem();
				
				var fileEl=document.createElement("input");
				fileEl.type="file";
				//'vf_'+fileItemId
				fileEl.id='vf_'+fileItemId;
				
				$("#valueList_"+self.id).append(fileEl);
				//绑定load事件
				$(fileEl).change(function(e1){
					if(!$(fileEl).val()||$(fileEl).val()==""){
						return;
					}
					var name=e1.currentTarget.files[0].name;
					var file=$(this).get(0).files[0];
					var reader=new FileReader();
					reader.readAsDataURL(file);
					reader.onload=function(e){
						setTimeout(function(){
							var _ii=name.split(".");
							//console.info(_ii[1]);
							var isImg=false;
							if(_ii[1]=="jpg"||_ii[1]=="gif"||_ii[1]=="png"||_ii[1]=="bmp"){
								isImg=true;
							}
							if(isImg){
								$('#img_'+fileItemId).attr("src",e.target.result);
							}else{
								//默认文件图片
								$('#img_'+fileItemId).parent().addClass("file-item");
								$('#img_'+fileItemId).attr("src","/images/file_icon.png");
							}
							$('#fname_'+fileItemId).html(name);
							//显示文件
							$("#fitem_"+fileItemId).removeClass("hide");
							
							if(!self.isBatch){
								//不支持批量，则把上传按钮隐藏
								$("#uploadff_"+self.id).addClass("hide");
							}
						},500);
					}
					
					
				});
				//触发点击事件
				$(fileEl).click();
			});
			
			//图片删除
			$(self.el).on("click", ".img-delete", function(){
				//alert(1);
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				var itemId=$(this).attr("idata");
				$("#fitem_"+itemId).remove();
				/*//从存储中删除克隆体，克隆对象
				var clone=self.valueList[itemId];
				if(clone){
					delete self.valueList[itemId];
					$(clone).remove();
				}*/
				//删除valueList中的fileEl
				$("#vf_"+itemId).remove();
				
				//只要发生删除都要把上传按钮显示，以便可以上上次新的图片
				$("#uploadff_"+self.id).removeClass("hide");
				if(self.config.onDeleted){
					//查找该图片的src
					var imgEl=$(this).prev();
					var _src=$(imgEl).attr("src");
					if(_src.lastIndexOf("data:image")==0||"/images/file_icon.png"==_src){
						//本地上传的图片，不处理删除事件
					}else{
						self.config.onDeleted(_src);
					}
				}
			});
			
			//点击预览大图
			$(self.el).on("click", ".img-mask", function(){
				var _imgEl=$(this).parent().children(":first");
				var _imgElId=$(_imgEl).attr("id");
				var _fileItemId=_imgElId.replace("img_","");
				
				var _src=$(_imgEl).attr("src");
				//如果是文件，则不能预览
				if("/images/file_icon.png"==_src){
					return;
				}
				
				$("#preview_"+self.id).show().css("display","table");
				$(".city-main-scroll").css({"overflow":"hidden","z-index":"200"});
				//加载点击的图片放到预览大图上
				
				$("#viewimg_"+self.id).attr("src",_src);
				//图片名称
				var _fname=$("#fname_"+_fileItemId).text();
				$("#viewimgname_"+self.id).html(_fname);
				//设置当前预览的图片id
				var _items=_fileItemId.split("_");
				var _item1=_items[1];
				var _currentImgIndex=_item1.replace("file","");
				$("#ctViewImg_"+self.id).val(_currentImgIndex);
			});
			//关闭预览大图
			$(self.el).on("click", "#previewclose_"+self.id, function(){
				$("#preview_"+self.id).hide();
				$(".city-main-scroll").css({"overflow":"auto","z-index":"80"});
			});
			
			//上一个图片按钮
			$("#previmgBtn_"+self.id).click(function(){
				var _currentImgIndex=$("#ctViewImg_"+self.id).val();
				_currentImgIndex=_currentImgIndex*1;
				_currentImgIndex--;
				//展示上一张图片
				//img_filew15234209677640026408458102051835_file1
				var _imgElId="img_"+self.id+"_file"+_currentImgIndex;
				if($("#"+_imgElId).length>0){
					var _src=$("#"+_imgElId).attr("src");
					$("#viewimg_"+self.id).attr("src",_src);
					//fname_filew152342142400706016768437156215_file1
					var _imgNameElId="fname_"+self.id+"_file"+_currentImgIndex;
					var _fname=$("#"+_imgNameElId).text();
					$("#viewimgname_"+self.id).html(_fname);
					//切换当前的图片索引
					$("#ctViewImg_"+self.id).val(_currentImgIndex);
				}
				
			});
			//下一个图片按钮
			$("#nextimgBtn_"+self.id).click(function(){
				var _currentImgIndex=$("#ctViewImg_"+self.id).val();
				_currentImgIndex=_currentImgIndex*1;
				_currentImgIndex++;
				var _imgElId="img_"+self.id+"_file"+_currentImgIndex;
				if($("#"+_imgElId).length>0){
					//如果图片存在
					var _src=$("#"+_imgElId).attr("src");
					$("#viewimg_"+self.id).attr("src",_src);
					//切换当前的图片索引
					$("#ctViewImg_"+self.id).val(_currentImgIndex);
					var _imgNameElId="fname_"+self.id+"_file"+_currentImgIndex;
					var _fname=$("#"+_imgNameElId).text();
					$("#viewimgname_"+self.id).html(_fname);
				}
			});
			
		},
		_genFileItem:function(url){
			var self=this;
			self.fileCount++;
			
			var fileItemId=self.id+'_file'+self.fileCount;
			var fileItemName='file'+self.fileCount;
			
			var _hide='hide';
			var _url='';
			if(url){
				_url=url;
				_hide='';
			}
			
			var _html='<div id="fitem_'+fileItemId+'" class="img-item-box '+_hide+'">'
				+'	<div class="img-item">'
				+'		<img id="img_'+fileItemId+'" src="'+_url+'" alt="">'
				//删除按钮
				+'		<span class="img-delete" idata="'+fileItemId+'"></span>'
				//预览按钮
				+'		<div id="viewbtn_'+fileItemId+'" class="img-mask"><span class="img-view-icon"></span></div>'
				+'	</div>'
				+'	<div id="fname_'+fileItemId+'" class="img-name"></div>'
				+'</div>';
			
			$("#fileList_"+self.id).append(_html);
			/*if(source){
				//克隆一份file到隐藏域
				var cloneEl=$(source).clone();
				//改变克隆对象的Id和name
				var _id=Math.random()+"";
				_id=_id.replace(".","");
				$(cloneEl).attr('id', 'vf_'+fileItemId);
				$(cloneEl).attr('name', fileItemName);
				//克隆对象添加到form表单
				var valueList=$("#valueList_"+self.id);
				$(cloneEl).appendTo(valueList);
				//把克隆体放入存储
				self.valueList[fileItemId]=cloneEl;
			}*/
			/*if(!self.isBatch){
				//不支持批量，则把上传按钮隐藏
				$("#uploadff_"+self.id).addClass("hide");
			}*/
			
			return fileItemId;
		},
		getValue:function(){
			var self=this;
			var arr=[];
			//获取valueList中所有的孩子
			$("#valueList_"+self.id).find("input").each(function(){
				arr.push($(this).attr("id"));
			});
			//var $("#valueList_"+self.id).children();
			return arr;
		},
		setValue:function(list){
			var self=this;
			/*
			 * ['http://127.0.0.1:8080/FileService/common/121212.jpg']
			 * 
			 * */
			if(!list||list.length<1){
				return;
			}
			for(var i=0,len=list.length;i<len;i++){
				self._genFileItem(list[i]);
			}
			if(!self.isBatch){
				//不支持批量，则把上传按钮隐藏
				$("#uploadff_"+self.id).addClass("hide");
			}
		}
	}
	
	//封装成jquery的控件
	$.fn.FileWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_filewidget);//后面的覆盖前面的
		_o.id="filew"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.FileWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();