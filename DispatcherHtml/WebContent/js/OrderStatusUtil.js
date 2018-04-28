var OrderStatusUtil = {
	getStatusName: function(orderStatus){
		var name = '';
		if(!orderStatus)
			return '';
		switch (orderStatus){
			case 10:
			  name = "待发布";
			  break;
			case 20:
			  name = "已发布";
			  break;
			case 30:
			  name = "已失效";
			  break;
			case 40:
			  name = "已接单";
			  break;
			case 50:
			  name = "已分配";
			  break;
			case 60:
			  name = "已取消";
			  break;
			case 70:
			  name = "待提货";
			  break;
			case 80:
			  name = "配送中";
			  break;
			case 90:
			  name = "已结束";
			  break;
		}
		return name;
	}
}