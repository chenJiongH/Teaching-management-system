$(function () {
	 
	 $("#term").html(tenTermOptions);
	 pageAjax();
	 $("#term").change(pageAjax);
	
})

function post() {
    var flag = checkNotBlank();
    if (!flag) {
        messageShow("请输入完整的信息");
        return ;
    }
    $.ajax({
        type: "post",
        url: path + "/lbteacher",
        data: $("form").serialize(),
        success: function (data) {
            messageShow(data);
            pageAjax();
        }
    });
}

function pageAjax() {
    ajax('get', path + "/lbteacher",
        {
            'curpage': curpage,
            'term': $("#term").val(),
            'pageNumber': 20
        }).then((data) => {
        joins(data);
    });
}

function joins(data) {
    var tr = "";

    for (var i = 0; i < data.length; i++) {
        var json = data[i];
        tr += "<tr>";
        tr += "<td>" + json.phone + "</td>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.password + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += '<td onclick="del(\''+json.phone+'\')"><div class="del">删除</div></td>';
        tr += "</tr>";
    }
    $("table tr:gt(0)").remove();
    $("table").append(tr);
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);
    $("table tr:gt(0)").click(function () {
        $("input[name=phone]").val($(this).find("td:eq(0)").text());
        $("input[name=name]").val($(this).find("td:eq(1)").text());
        $("input[name=password]").val($(this).find("td:eq(2)").text());
    });
}
function del(phone) {
    zdconfirm("系统确认框", "确定删除该记录吗？", function (r) {
        if (r) {
            ajax("delete", path + "/lbteacher",{
                    "term":$("#term").val(),
                    "phone":phone
                }).then((data) => {
                    messageShow(data);
                    pageAjax();
            })
        }
    })
}
