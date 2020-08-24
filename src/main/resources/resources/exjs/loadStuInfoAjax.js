$(function () {
    pageAjax();

        $("select[id=term]").html(tenTermOptions);

})

function pageAjax() {
    $.ajax({
        type: "get",
        url: path + "/stu",
        data: {"curpage": curpage},
        success: function (data) {
            joins(data);
        }
    })
}

var stuCourseInfo;
/**
 * 导入管理员的学生课程文件，返回文件的内容
 * @returns {boolean}
 */
function stuImport() {
    jQuery.support.cors = true;
    if ($("input[type=file]").val() == "") {
        messageShow("请选择导入文件");
        return ;
    }
    var formData = new FormData($("form")[0]);
    $.ajax({
        type: "post",
        url: path + "/stu/import",
        data: formData,
        contentType: false, // 使用form的 enctype
        processData: false, // 不对form表单的数据进行处理
        success: function (data) {
            if (data == "") {
                messageShow("信息导入失败");
                return;
            }
            messageShow("导入成功");
            stuCourseInfo = data;
            joins(data);
        }
    });
}

function stuUpload() {
    if (stuCourseInfo == undefined) {
        messageShow("请先导入文件");
        return ;
    }
    $.ajax({
        type: "post",
        url: path + "/stu",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(stuCourseInfo),
        success: function (data) {
            messageShow(data);
        }
    })
}

function joins(data) {
    $("table tr:gt(0)").remove();

    for(var i = 0; i < data.length; i++) {
        var json = data[i];
        var tr = "<tr>";
        tr += "<td>" + json.sno + "</td>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.password + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += "</tr>";
        $("table").append(tr);
    }
    $("table td").mouseenter(enter);
     $("table td").mouseleave(leave);
}
