function post() {
    $("input[name=content]").val(ue.getContent());
    $("input[name=summary]").val(ue.getContentTxt().substring(0, 250));
    if ($("input[name=content]").val() == "" || $("input[name=title]").val() == "" || $("input[name=uptime]").valueOf() == "") {
        messageShow("数据无效");
        return;
    }
    $.ajax({
        type: "post",
        url: path + "/news",
        data: $("form").serialize(),
        success: function (data) {
            if (data == "") {
                messageShow("请控制新闻字数");
                return;
            }
            joins(data);
        }
    })
}

function joins(data) {
    var flag = false;
    var list = [];
    var json, tr;
    if (!(data instanceof Array)) {
        list.push(data);
        flag = true;
    }
    else {
        list = data;
        console.log($(".table tr")[0]);
        $(".table tr:gt(0)").remove();
    }
    var tr = "";
    for (var i = 0; i < list.length; i++) {
        json = list[i];
        tr += "<tr>";
        tr += "<td>" + json.title + "</td>";
        tr += "<td>" + json.uptime + "</td>";
        tr += "<td>" + json.summary + "</td>";
        tr += "<td id ='" + json.id + "'>删除</td>"
        tr += "</tr>";
    }
    if (flag) {
        $(".table tr:eq(0)").after(tr);
    }
    else {
        $(".table table").append(tr);
    }
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);

    $(".table tr").each(function () {
        $(this).find('td').eq(3).click(function () {
            var curClickTd = $(this);
            var tr = $(this).parent();

            zdconfirm('系统确认框', '确定删除该新闻通知吗？', function (r) { //项目删除确定框
                if (r) {
                    $.ajax({
                        type: "delete",
                        url: path + "/news",
                        // url: "/news",
                        data: {"id": curClickTd.attr("id")},
                        success: function (data) {
                            if (data == "删除失败") {
                                messageShow(data);
                            }
                            tr.remove();
                        }
                    })
                }
            });
        })
    })
}

$(function () {
    pageAjax();
})

function pageAjax() {
    $.ajax({
        type: "get",
        url: path + "/news/page",
        data: {
            "curpage": curpage,
            "type": "新闻详情页面"
        },
        success: function (data) {
            joins(data);
        }
    });
}