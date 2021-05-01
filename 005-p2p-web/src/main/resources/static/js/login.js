var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


function Login() {
    var phone= $.trim($("#phone").val());
    var loginPassword= $.md5($.trim($("#loginPassword").val()));
    $.ajax({
        type: "POST",
        url: "/005-p2p-web/loan/page/loginSubmit",
        data: {phone:phone,loginPassword:loginPassword},
        success: function(msg){
            if (msg.code=="1"){
                window.location.href=$("#returnUrl").val();
            }
            if(msg.code=="0"){
                alert(msg.msg);
            }
        },
        error:function () {
            alert( "系统繁忙" );
        }
        
    });
}