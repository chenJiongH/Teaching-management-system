var allData;
// 该标记用来判断项目下拉框是第几次被请求，如果是页面一刷新的时候请求，则去请求评分。不是的话等待用户点击了查询按钮之后再去请求评分详情
var flag = true;
$(function () {
    pageAjax();

    // 十年学期写死在页面上
    // 版本2.学期只显示该教师有教课的学期
    // $("select[id=term]").html(tenTermOptions);
    // changeTerm();

    $("select[id=term]").change(changeTerm);
    $("select[id=course]").change(changeCourse);
    
    // 第一层请求学期
    $.ajax({
        type: "get",
        url: path + "/term",
        // url: "/term",
        success: function (data) {
            var options = joinOptions(data);
            $("select[id=term]").html(options);
            changeTerm();
        }
    })
});

function joinOptions(data) {
    var options = "";
    for (var i = 0; i < data.length; i++) {
        options += "<option value=" + data[i] + ">" + data[i] + "</option>"
    }
    return options;
}

function changeTerm() {
    // 第二层请求学期下的课程
    $.ajax({
        type: "get",
        url: path + "/course",
        // url: "/course",
        xhrFields: {
            withCredentials: true,
        },
        data: {'term': $("select[id=term]").val()},
        success: function (data) {
            console.log(data);
            var options = joinOptions(data);
            $("select[id=course]").html(options);
            changeCourse();
        }
    })
}

function changeCourse() {
    $.ajax({
        type: "get",
        url: path + "/project/getAll",
        // url: "/project/getAll",
        data: {'term': $("select[id=term]").val(),
            'cname': $("select[id=course]").val()
        },
        success: function (data) {
            var options = "";
            for (var i = 0; i < data.length; i++) {
                options += "<option value=" + data[i].pname + ">" + data[i].pname + "</option>"
            }
            $("select[id=program]").html(options);
            // 页面一刷新，用户不用点击查询按钮，就去查询评分。
            if (flag) {
                flag = false;
                get();
            }
        }
    })
}

function get() {
    $.ajax({
        type: "get",
        url: path + "/grade/conditions",
        // url: "/grade/conditions",
        data: {'term': $("select[id=term]").val(),
            'cname': $("select[id=course]").val(),
            'pname': $("select[id=program]").val()
        },
        success: function (data) {
            allData = data;
            // i 从当前页的第一条数据开始显示，一直到数据结束 或者 i到了下一页的数据，则结束
            var trs = joins(allData);
            $("table tr:gt(0)").remove();
            $("table").append(trs);
            $("table td").mouseenter(enter);
            $("table td").mouseleave(leave);
        }
    })
}

/**
 * 对查询到的数据，进行分页的拼接，并且返回字符串
 * @param data 查询到的数据
 * @returns {string} 页面table字符串
 */
function joins(data) {
    var trs = "";
    if (data != null)
    for (var i = (curpage - 1) * 20;  i < data.length && i < curpage * 20; i++) {
        var json = data[i];
        var tr = "<tr>";
        tr += "<td>" + json.sno + "</td>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.pname + "</td>";
        // 防止 score 显示 null
        json.score = json.score == null?"":json.score;
        tr += "<td>" + json.score + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += "</tr>";

        trs += tr;
    }
    return trs;
}

/**
 * 点击页码，然后对查询到的全局数据进行分页展示
 */
function pageAjax() {
    // i 从当前页的第一条数据开始显示，一直到数据结束 或者 i到了下一页的数据，则结束
    var trs = joins(allData);
    $("table tr:gt(0)").remove();
    $("table").append(trs);
    $("table td").mouseenter(enter);
     $("table td").mouseleave(leave);
}

/**
 * 1、异步请求（json只能由异步请求发送）传送查询到的数据给后端，后端把他们写入文件，然后发送文件在服务器上的路径。
 * 2、表单请求（下载文件只能由表单请求下载）根据返回的路径，从服务器中下载该文件
 */
function gradeExport() {
    $.ajax({
        type: "post",
        url: path + "/grade/makeFile",
        // url: "/grade/makeFile",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(allData),
        success: function (data) {
            $("input[name=fileDest]").val(data);
            if (data == "导出评分失败") {
                messageShow(data);
                return;
            }
            $("#gradeExport").submit();
        }
    })
}





