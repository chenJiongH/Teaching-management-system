$(function () {
	$.ajax({
		type: "get",
		url: path + "/teacher/self",
        xhrFields: {
            withCredentials: true,
        },
		success:function(data) {
			// 传送过去时，需要有这两个值，故增加了这两个隐藏的文本框
			$("input[name=name]").val(data.name);
			$("input[name=phone]").val(data.phone);
			// 原来的密码、手机号
			$("input[name=oriPhone]").val(data.phone);
		}
	})
})

function put() {
    if ($("input[name=password]").val() != $("input[name=pw2]").val()) {
        messageShow("密码和确认密码不一致！");
        return ;
    }

	$.ajax({
		type: "put",
	    url: path + "/teacher/self",
	    data: $("form").serialize(),
	    success: function (data) {
		    messageShow(data)
	    }
	})
}
function pageAjax() {

}