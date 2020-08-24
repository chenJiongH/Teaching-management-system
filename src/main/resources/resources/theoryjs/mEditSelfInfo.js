$(function (){
	ajax('get',path+'/user/lbmessage').then((data)=>{
		$("#phone").val(data.phone);
		$("#name").val(data.name);
	})
})
function put(){
    if ($("#password").val() != $("#pw1").val()) {
        messageShow("确认密码和新密码不一致！");
        return ;
    }
	ajax('put',path+'/user/lbtmessage',$("form").serialize()).then((data)=>{
        messageShow(data);
	})
}
function pageAjax(){

}