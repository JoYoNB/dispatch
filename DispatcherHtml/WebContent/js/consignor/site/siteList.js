var SiteList={
	deptWidget:null,
	tableWidget:null,
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		
		//设置底部菜单连接
		Footer.nav("站点管理","consignor/site/siteList.html");
		
		self.deptWidget=$("#deptWidget").DeptWidget({
		});
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/consignor/site/queryList.json",
			pageSize:20,
			cells:[{
				field:"op",
				text:"操作",
				render:function(value,row){
					/*if(row.type=="system"){
						return "";
					}*/
					var str = $.param(row);
					var _url_modify="/"+Constant.PROJECT_NAME+"/consignor/site/siteModify.html?"+str;
					var _html="<a href='"+_url_modify+"'>编辑 </a>";
					_html+="<a href='javascript:SiteList._deleteSite("+row.id+");'>删除</a>";
					return _html;
				}
			},{
				field:"siteName",
				text:"名称"
			},{
				field:"deptName",
				text:"部门"
			},{
				field:"linkName",
				text:"联系人"
			},{
				field:"linkPhone",
				text:"手机号"
			},{
				field:"address",
				text:"位置"
			},{
				field:"createTime",
				text:"创建时间"
			}]
		});
	},
	_bind:function(){
		var self=this;
		//删除站点
		$("#batchDelete").unbind('click').click(function(){
			//删除动作
			self._batchDelete();	
		});
		//查询
		$("#queryList").unbind('click').click(function(){
			self._queryList();
		});
		
		/*$('input:checkbox').change(function(){
			var _this= $(this);
			if(_this.is(':checked')){
				$('#batchDelete').removeClass('disabled');
			}
		});*/
	},
	_batchDelete:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		MessageUtil.confirm("是否要删除?",function(){
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/site/deleteList.json",
			data:{siteId:ids},
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//删除后重新加载
						self.tableWidget.reload();
					});
				}else if(result.code==120002){
					MessageUtil.alert("站点被订单使用，不能删除");
				}else{
					MessageUtil.alert(result.msg);
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
		});
	},
	_deleteSite:function(id){
		var self = this;
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/consignor/site/delete.json",
				data:{siteId:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else{
						MessageUtil.alert(result.msg);
					}
				},
				error:function(result){
					MessageUtil.alert("失败");
				}
			});
		});
	},
	_queryList:function(){
		var self = this;
		var param={};
		var deptId=self.deptWidget.getValue();
		var siteName=$("#name").val();
		var linkMan=$("#linkMan").val();
		var linkPhone=$("#linkPhone").val();
		if(deptId&&deptId!=""){
			param.deptId=deptId;
		}
		if(siteName&&siteName!=""){
			param.name=siteName;
		}
		if(linkMan&&linkMan!=""){
			param.linkMan=linkMan;
		}
		if(linkPhone&&linkPhone!=""){
			param.linkPhone=linkPhone;
		}
		console.log(param);
		self.tableWidget.query(param);
	}
	
}
$(function(){
	SiteList.init();
});