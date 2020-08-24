var dict ={};

$(function () {
    pageAjax();
    ajax('get',path + "/lbterm").then((data)=>{
        var options = joinOptions(data);
        $("#term").html(options);
        changerCname();
    })
    $("#term").change(changerCname);
    $("#cname").change(pageAjax);
})
function changerCname() {
    ajax('get', path + "/lbcname", {"term":$("#term").val()}).then((data)=>{
        var options = joinOptions(data);
        $("#cname").html(options);
        pageAjax();
    })
}
function upload() {
    if ($("input[name=file]").val() == "") {
        messageShow("请上传文件！");
        $("input[class=file]").val("");
        return ;
    }
    var formData = new FormData($(".video_f")[0]);
    $.ajax({
        type: "post",
        url: path + "/lbstuImport",
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
// 提交新增学生信息
function post() {

    $("input[name=sno]").val($("#sno").val());
    $("input[name=name]").val($("#name").val());
    $("input[name=password]").val($("#password").val());
    $("input[name=phone]").val($("#phone").val());
    // 检验信息是否完整
    // var flag = true;
    // $(".video_f input").each(function () {
    //     if ($(this).val() == "") {
    //         if ($(this).attr("name") != "file" ) {
    //             flag = false;
    //             return false;
    //         }
    //     }
    // });
    ajax('post',path+'/lbstu',$(".video_f").serialize()).then((data)=>{
        messageShow(data);
        pageAjax();
    })
    cancel();
}
// 新增学生按钮
function postStu(){
    $("#curtain").css("display","block");
    $("#add").css("display","block");
}
// 新增学生点击×关闭
function cancel() {
    $("#curtain").css("display","none");
    $("#add").css("display","none");
}
function pageAjax() {
    ajax('get', path + "/lbstu",
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
    dict = {};
    var term = $("#term").val();
    for (var i = 0; i < data.length; i++) {
        var json = data[i];
        tr += "<tr>";
        tr += "<td>" + json.sno + "</td>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.password + "</td>";
        console.log(json.phone);
        json.phone = json.phone == "null"?"":json.phone;
        tr += "<td>" + json.phone + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += '<td onclick="del(' + i + ')">删除</td>';
        tr += "</tr>";
        dict[i] = [json.term,json.cname,json.sno];
    }
    $(".t_div table tr:gt(0)").remove();
    $(".t_div table").append(tr);
    $(".t_div table td").mouseenter(enter);
    $(".t_div table td").mouseleave(leave);
}



function del(i) {

    zdconfirm("系统确认框", "确定删除该记录吗？", function (r) {
        if (r) {
            var term = dict[i][0];
            var cname = dict[i][1];
            var sno = dict[i][2];
            ajax("delete", path + "/lbstu",
                {
                    'term': term,
                    'cname': cname,
                    'sno': sno
                }).then((data) => {
                messageShow(data);
                pageAjax();
            })
        }
    })
}
