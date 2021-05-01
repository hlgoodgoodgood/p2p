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

//注册协议确认
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


    var phoneTag=0;
	$("#phone").blur(function () {
       // phoneTag=0;
	   var phone= $.trim($("#phone").val());
	    if(phone==null||phone==""){
            showError("phone","手机号码不能为空");
            return ;
        }
        if(phone.length!=11){
            showError("phone","手机号码位数不正确");
            return ;
        }
        if(!/^1[1-9]\d{9}$/.test(phone)){
            showError("phone","请输入正确的手机号码");
            return ;
        }


        //服务端验证
        $.get("/005-p2p-web/loan/page/checkPhone", { phone: phone },
            function(data){
              if(data.code=="1"){
                  showSuccess("phone");
                  phoneTag=1;
                  //alert(2222);
                  // if(phoneTag==1&&passwdTag==1){
                  //   //提交数据
                  // }



              }
              if(data.code=="0"){
                  showError("phone","该手机号码已被注册");
              }
          });

    });

	//{ 123=xx{}; return }
    //追踪变量
    var passwdTag=0;

    $("#loginPassword").blur(function () {
        passwdTag=0;
        var loginPassword= $.trim($("#loginPassword").val());
        if(loginPassword==null||loginPassword==""){
            showError("loginPassword","密码不能为空");
            return ;
        }
        if(loginPassword.length<6||loginPassword.length>20){
            showError("loginPassword","密码必须是6-20位");
            return ;
        }
        if(!/^[0-9a-zA-Z]+$/.test(loginPassword)){
            showError("loginPassword","密码只可使用数字和大小写英文字母");
            return ;
        }
        if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
            showError("loginPassword","密码应同时包含英文和数字");
            return ;
        }
        showSuccess("loginPassword");
        passwdTag=1;

    });


    $("#messageCodeBtn").click(function () {
        $("#phone").blur();
        $("#loginPassword").blur();
        var phoneErr=$.trim($("#phoneErr").html());
        var loginPasswordErr=$.trim($("#loginPasswordErr").html());
        //课后：完成验证错误情况
        if(phoneErr==""&&loginPasswordErr==""){

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

    //课后：加上验证码验证


    $("#btnRegist").click(function () {
        //模拟触发
        //var ret=$("#phone").blur();
       // alert(ret);
       //var passwdTag= $("#loginPassword").blur();
        //alert(console.log(passwdTag));
        $("#phone").blur();
        $("#loginPassword").blur();
       // alert(1111);
       // alert("phoneTag-->"+phoneTag);
        //alert("passwdTag-->"+passwdTag);

        var  messageCode= $.trim($("#messageCode").val());
        if(messageCode==null||messageCode==""){
            showError("messageCode","密码不能为空");
            return ;
        }
        if(messageCode.length!=6){
            showError("messageCode","请输入6位验证码");
            return ;
        }


        if(phoneTag==1&&passwdTag==1){
            //alert("提交数据");
            var phone= $.trim($("#phone").val());
            var loginPassword= $.md5($.trim($("#loginPassword").val()));
            //设置密码框为密文
            $("#loginPassword").val(loginPassword);

            $.post("/005-p2p-web/loan/page/registSubmit", { phone: phone, loginPassword: loginPassword,messageCode:messageCode },
                function(data){
                    if(data.code==1){
                        window.location.href="http://localhost:8005/005-p2p-web/loan/page/realName"
                    }
                    if(data.code==0){
                        alert("注册失败");
                    }
                });

        }
    });


});
