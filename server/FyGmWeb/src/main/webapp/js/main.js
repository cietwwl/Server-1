 jQuery(document).ready(function(){
	 //表格选择条数的时候iframe自动高度
	jQuery('select[name=tableInfo_length]').live('change',function(){
		refreshFrame();
		refreshDeepFrame();
	});

	//全选
 	jQuery("#selectAll").click(function(){  
 		jQuery("[name='serverId']").attr("checked",'true');
 	    jQuery("[name='server']").attr("checked",'true');
 	   jQuery("[name='union']").attr("checked",'true');
 	   jQuery("[name='gs_server']").attr("checked",'true');
 	   jQuery("[name='ser']").attr("checked",'true');
 	});
 	//取消全选
	jQuery("#selectCancel").click(function(){
		jQuery("[name='serverId']").removeAttr("checked");
		jQuery("[name='server']").removeAttr("checked");
		jQuery("[name='union']").removeAttr("checked");
		jQuery("[name='gs_server']").removeAttr("checked");
		jQuery("[name='ser']").removeAttr("checked");
	});
	
    //选择提示
	jQuery('#selectAll').tooltip();
	jQuery('#selectCancel').tooltip();
	jQuery("input[data-toggle='tooltip']").tooltip();
	
	//默认勾选第一个服务器
	jQuery("[name='server']:first").attr('checked','true');
	
	//默认勾选第一个渠道商
	jQuery("[name='union']:first").attr('checked','true');
	
	//选择表格条数后刷新iframe高度	
	jQuery('select[name=tableInfo_length]').live('change',function(){
		refreshFrame();
	});
	
	//初始化日期控件
    jQuery('.form_datetime').datetimepicker({
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        showMeridian: 1,
		pickerPosition:'bottom-left'
    });
    
	jQuery('.form_date').datetimepicker({
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 2,
		forceParse: 0,
		pickerPosition:'bottom-left'
    });
    
    //关闭按钮
    jQuery(".close").live("click",function(){
    	jQuery(this).parent().hide();
    });
    
});

function setTipPosition(element){
	var top= jQuery(window).height()*0.75 + document.body.scrollTop;
	var left=document.body.clientWidth*0.5 - jQuery('#tips').width()/2;
	jQuery('#tips').css({
		  "top":top,
		  "left":left
		  });
}
//显示提示框
function showTip(data){
	jQuery('#tips').html(data);
	setTipPosition();
	jQuery('#tips').slideDown(200).delay(3000).slideUp(200);
}

//刷新iframe高度
function refreshFrame(){
	var iframe = jQuery(window.parent.document).find("#content");
	var height = jQuery(document.body).height()+50;
	if(height<700)
		height=700;
	iframe.height(height);
}
//刷新iframe高度
function refreshDeepFrame(){
	var iframe = jQuery(window.parent.document).find("#show");
	var height = jQuery(document.body).height()+50;
	if(height<700)
		height=700;
	iframe.height(height);
	
	var iframe_parent = jQuery(window.parent.parent.document).find("#content");
	var height_parent = jQuery(document.body).height()+400;
	iframe_parent.height(height_parent);
	
}

//刷新iframe高度
function refreshChildFrame(height){
	if(height<700)
		height=700;
	jQuery("#content").height(height);
	jQuery("#content").css("height",height);
}