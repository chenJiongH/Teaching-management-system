function get() {

    if ($("input[name=sno]").val() == "" && $("input[name=name]").val() == "" && $("input[name=phone]").val() == "") {
        messageShow("请输入任意查询条件");
        return;
    }
	$.ajax({
		type:"get",
		url:path + "/stu/selectOne",
		data:$("form:eq(0)").serialize(),
		success: function (data){
			if (data == "") {
                messageShow("查询失败");
                return;
			}
			$("input[name=oriName]").val(data.name);
			$("input[name=name]").val(data.name);
			$("input[name=oriSno]").val(data.sno);
			$("input[name=sno]").val(data.sno);
			$("input[name=oriPhone]").val(data.phone);
			$("input[name=phone]").val(data.phone);
			$("input[name=password]").val(data.password);
		}
	})
}
function put() {
    if ($("input[name=oriName]").val() == '' && $("input[name=oriSno]").val() == '' && $("input[name=oriPhone]").val() == '') {
        messageShow("请先查询出学生信息");
        return;
    }
	$.ajax({
		type:"put",
		url:path + "/stu",
		data:$("form").last().serialize(),
		success:function (data) {
            messageShow(data);
		}
	})
}
function pageAjax() {
	
}