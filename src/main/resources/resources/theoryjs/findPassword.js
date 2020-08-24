$(function (){

})
var url = "";
var type="";
function post(){
    if ($("input[name=phone]").val() == "" || $("input[name=username]").val() == ""    ) {
        messageShow("请输入用户名和手机号！");
        return ;
    }
	ajax('get',path+"/user/send", $("form").serialize()).then((data)=>{
        if (data == "用户名不存在！") {
            messageShow(data);
        }
	 	type = data;
	 })
}
function put(){
    var flag = checkNotBlank();
    if (!flag) {
        messageShow("请输入完整信息！");
        return ;
    } else if ($("input[name=pw1]").val() != $("input[name=password]").val()) {
        messageShow("新密码和确认密码不一致！");
        return ;
    }

	if (type == "学生"){
		url = "/user/lbsmessage";
	}
	else if (type == "教师"){
		url = "/user/lbtmessage";
	}

	ajax('put',path+url,$("form").serialize()).then((data)=>{
        messageShow(data);
	})
}

function pageAjax(){

}