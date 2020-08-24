$(function () { 
	 $("#term").html(tenTermOptions);
	 pageAjax();
	 $("#term").change(pageAjax);
})
var dict ={}; 
function post() {
    var flag = checkNotBlank();
    if (!flag) {
        messageShow("请输入完整信息！");
        return;
    }
    $.ajax({
        type: "post",
        url: path + "/lbcourse",
        data: $("form").serialize(),
        success: function (data) {
            messageShow(data);
            pageAjax();
        }
    });
}

function pageAjax() {
    ajax('get', path + "/lbcourse",
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
    dict ={};

    for (var i = 0; i < data.length; i++) {
        var json = data[i];
        tr += "<tr>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.phone + "</td>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += '<td><div class="del" onclick="del('+i+')">删除</div></td>';
        tr += "</tr>";
        dict[i]= [json.phone,json.cname];
    }
    $("table tr:gt(0)").remove();
    $("table").append(tr);
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);
    $("tr:gt(0)").click(function () {
        $("input[name=phone]").val($(this).find("td:eq(1)").text());
        $("input[name=cname]").val($(this).find("td:eq(0)").text());
    })
}
function del(i) {
    zdconfirm("系统确认框", "确定删除该记录吗？", function (r) {
        if (r) {
            var phone = dict[i][0];
            var cname = dict[i][1];
            ajax("delete", path + "/lbcourse",{
                    "term":$("#term").val(),
                    "cname":cname,
                    "phone":phone
                }).then((data) => {
                    messageShow(data);
                    pageAjax();
            })
        }
    })
}
