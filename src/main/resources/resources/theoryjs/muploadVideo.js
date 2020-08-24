$(function () {
    pageAjax();
})

function videoUpload() {

    if ($("input[name=file]").val() == "" || $("input[name=summary]").val() == "") {
        messageShow("请输入完整内容！");
        return ;
    }
    var formData = new FormData($("form")[0]);
    $.ajax({
        type: "post",
        url: path + "/lbvideo",
        xhrFields: {
            withCredentials: true,
        },
        data: formData,
        contentType: false, // 使用form的 enctype
        processData: false, // 不对form表单的数据进行处理
        success: function (data) {
            messageShow(data);
            pageAjax();
        }
    });
}

function pageAjax() {
    ajax('get', path + "/lbvideo",
        {
            'curpage': curpage,
            'cname': $("#cname").val(),
            'term': $("#term").val(),
            'pageNumber': 20
        }).then((data) => {
        joins(data);
    });
}

function joins(data) {
    var tr = "";
    var start = (curpage - 1) * 20;

    for (var i = 0; i < data.length; i++) {
        var json = data[i];
        tr += "<tr onclick='clickTr(this)'>";
        tr += "<td>" + (++start) + "</td>";
        tr += "<td>" + json.summary + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.term + "</td>";
        var key = json.id;
        tr += '<td><div class="up" onclick="moveU('+ key +')">上移</div><div class="down" onclick="moveD('+ key +')">下移</div><div class="del" onclick="del(' + key + ')">删除</div></td>';
        tr += "</tr>";
    }
    $("table tr:gt(0)").remove();
    $("table").append(tr);
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);
}

function moveU(key) {
    ajax("put", path + "/lbvideo",
        {
            'id': key,
            'dir': 'u',
            'term': $("#term").val(),
            'cname': $("#cname").val()
        }).then((data) => {
            messageShow(data);
            pageAjax();
    })
}
function moveD(key) {
    ajax("put", path + "/lbvideo",
        {
            'id': key,
            'dir': 'd',
            'term': $("#term").val(),
            'cname': $("#cname").val()
        }).then((data) => {
        messageShow(data);
        pageAjax();
    })
}

function del(key) {
    zdconfirm("系统确认框", "确定删除该记录吗？", function (r) {
        if (r) {
            ajax("delete", path + "/lbvideo",{
                    'id': key
                }).then((data) => {
                    messageShow(data);
                    pageAjax();
                })
        }
    })
}

function clickTr(e) {
    $("#summary").val($(e).find("td:eq(1)").text());
}
