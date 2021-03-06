$(function () {
     ajax('get',path + "/lbterm").then((data)=>{
        var options = joinOptions(data);
        console.log(options);
        $("#term").html(options);
        changerCname();
    });
    $("#term").change(changerCname);
    $("#cname").change(pageAjax);
})
function changerCname() {
    ajax('get', path + "/lbcname", {"term":$("#term").val()}).then((data)=>{
        var options = joinOptions(data);
        console.log(options);
        $("#cname").html(options);
        pageAjax();
    })
}
function uploadPro() {
    $("input[name=content]").val(ue.getContent());
    $("input[name=summary]").val(ue.getContentTxt());

    if ($("input[name=content]").val() == "" || $("input[name=title]").val() == "") {
        messageShow("请输入完整内容！");
        return ;
    }
    $.ajax({
        type: "post",
        url: path + "/lbnotice",
       data:$("form").serialize(),
        success: function (data) {
            messageShow(data);
            pageAjax();
        }
    });
}

function pageAjax() {
    ajax('get', path + "/lbnotice",
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
        tr += "<td style='display: none'>" + json.content + "</td>";
        tr += "<td>" + (++start) + "</td>";
        tr += "<td>" + json.title + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.term + "</td>";
        var key = json.id;
        tr += '<td><div class="up" onclick="moveU('+ key +')">上移</div><div class="down" onclick="moveD('+ key +')">下移</div><div class="del" onclick="del(' + key + ')">删除</div></td>';
        tr += "</tr>";
    }
    $(".t_div table tr:gt(0)").remove();
    $(".t_div table").append(tr);
    $(".t_div table td").mouseenter(enter);
    $(".t_div table td").mouseleave(leave);
}

function moveU(key) {
    ajax("put", path + "/lbnotice",
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
    ajax("put", path + "/lbnotice",
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
            ajax("delete", path + "/lbnotice",{
                'id': key
            }).then((data) => {
                messageShow(data);
                pageAjax();
            })
        }
    })

}
function clickTr(e) {
    $("tr").removeAttr("style");
    $(e).attr("style", "background-color:#ffd6d6");
    ue.setContent($(e).find("td:eq(0)").html());
    $("#noticeTi").val($(e).find("td:eq(2)").text());
}
