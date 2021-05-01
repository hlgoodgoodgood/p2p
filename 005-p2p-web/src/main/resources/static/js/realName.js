
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

$(function () {
    //手机号码验证
    //姓名验证

    //身份证验证

    //验证码验证


    $("#messageCodeBtn").click(function () {

        //课后：完成验证错误情况
        // if(phoneErr==""&&loginPasswordErr==""){
          if(true){
            var phone= $.trim($("#phone").val());
            var _this=$(this);
            if(!$(this).hasClass("on")){

                $.get("/005-p2p-web/loan/page/messageCode", { phone: phone },
                    function(data){
                        if(data.code=="1"){
                            alert(data.msg);

                            $.leftTime(60,function(d){
                                if(d.status){
                                    _this.addClass("on");
                                    _this.html((d.s=="00"?"60":d.s)+"秒后重新获取");
                                }else{
                                    _this.removeClass("on");
                                    _this.html("获取验证码");
                                }
                            });
                        }
                        if(data.code=="0"){
                            alert(data.msg);
                        }
                    });
            }
        }
    });


    $("#btnRegist").click(function () {


        var  messageCode= $.trim($("#messageCode").val());
        if(messageCode==null||messageCode==""){
            showError("messageCode","密码不能为空");
            return ;
        }
        if(messageCode.length!=6){
            showError("messageCode","请输入6位验证码");
            return ;
        }


        //if(phoneTag==1&&passwdTag==1){
         if(true){
            var phone= $.trim($("#phone").val());
            var realName= $.trim($("#realName").val());
            var idCard= $.trim($("#idCard").val());


            $.post("/005-p2p-web/loan/page/realNameSubmit", { phone: phone, realName: realName, idCard: idCard,messageCode:messageCode },
                function(data){
                    if(data.code==1){
                        window.location.href="http://localhost:8005/005-p2p-web/index"
                    }
                    if(data.code==0){
                        alert("注册失败");
                    }
                });

        }
    });


});