var allData;
var flag = false;
$(function () {
    pageAjax();

    // 十年学期写死在页面上
    $("select[id=term]").html(tenTermOptions);
    changeTerm();

    $("select[id=term]").change(changeTerm);
    $("select[id=course]").change(changeCourse);
    $("#program").change(selection);
    // 第一层请求学期
   /* $.ajax({
        type: "get",
        url: path + "/term",
        success: function (data) {
            var options = joinOptions(data);
            $("select[id=term]").html(options);
            changeTerm();
        }
    })*/
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
        data: {'term': $("select[id=term]").val()},
        success: function (data) {
            var options = joinOptions(data);
            $("select[id=course]").html(options);
            if (flag) {
                $("select[id=course] option[value=" + curClickTr.find("td").eq(0).text() + "]").prop("selected", true);
            }
            changeCourse();
        }
    })
}

function changeCourse() {

    // 第三层请求该学期该课程下的项目
    $.ajax({
        type: "get",
        url: path + "/project/getAll",
        data: {'term': $("select[id=term]").val(),
            'cname': $("select[id=course]").val()
        },
        success: function (data) {
            var options = "";
            for (var i = 0; i < data.length; i++) {
                options += "<option value=" + data[i].pname + ">" + data[i].pname + "</option>"
            }
            $("select[id=program]").html(options);
            if (flag) {
                var pname = curClickTr.find("td").eq(1).text();
                $("select[id=program] option[value=" + pname + "]").prop("selected", true);
                flag = false;
            }
            selection();
        }
    })
}

function selection() {
    // 第三层请求该学期该课程下的项目题目
    $.ajax({
        type: "get",
        url: path + "/problem",
        data: {
            'term': $("select[id=term]").val(),
            'cname': $("select[id=course]").val(),
            'pname': $("select[id=program]").val()
        },
        success: function (data) {
            allData = data;
            $("input[name=onepro]").val(data.onepro);
            $("input[name=twopro]").val(data.twopro);
            $("input[name=threepro]").val(data.threepro);
        }
    })
}

/**
 * 对查询到的数据，进行分页的拼接，并且返回字符串
 * @param data 查询到的数据
 * @returns {string} 页面table字符串
 */
function joins(data) {
    var flag =false;
    var list=[];
    if(!(data instanceof Array)) {
        list.push(data);
        flag = true;
    }
    else {
        list = data;
        $("table tr:gt(0)").remove();
    }
    var tr;
    for (var i = 0; i < list.length; i++){
        var json = list[i];
            tr += "<tr>";
            tr += "<td>" + json.cname + "</td>";
            tr += "<td>" + json.pname + "</td>";
            tr += "<td>" + json.term + "</td>";
            tr += "<td>" + json.onepro + "</td>";
            tr += "<td>" +json.twopro + "</td>";
            tr += "<td>" + json.threepro + "</td>";
            tr += "<td>删除</td>";

        tr += "</tr>"; 
    }
    if (flag) {
        $("table tr:eq(0)").after(tr);
    }
    else {
        $("table").append(tr);
    }
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);
    bindClickTr();
    bindDelete();
}

/**
 * 点击页码，然后对查询到的全局数据进行分页展示
 */
function pageAjax() {
    $.ajax({
        type:"get",
        url:path + "/problem/page",
        data:{"curpage":curpage},
        success: function (data){
            joins(data);
        }
    })
}
function post(){

    if ($("input[name=onepro]").val() == "" && $("input[name=twopro]").val() == "" && $("input[name=threepro]").val() == "") {
        messageShow("数据无效");
        return;
    }

    $.ajax ({
        type: "post",
        url: path + "/problem",
        data: $("form").serialize(),
        success: function(data) {
            $("table tr").removeAttr("style");
            if (data.code == "400") {
                // 修改成功 或者是数据无效
                if (data.message == "修改成功") {
                    // 修改成功，如果页面上已经显示了学期、课程、项目。则修改该条记录
                    $("table tr").each(function () {
                        var cname = $(this).find("td:eq(0)").text();
                        var pname = $(this).find("td:eq(1)").text();
                        var term = $(this).find("td:eq(2)").text();
                        if (cname == $("select[name=cname]").val() && pname == $("select[name=pname]").val() && term == $("select[name=term]").val()) {
                            $(this).find("td:eq(3)").text(data.json.onepro);
                            $(this).find("td:eq(4)").text(data.json.twopro);
                            $(this).find("td:eq(5)").text(data.json.threepro);
                            return;
                        }
                    })
                }
                messageShow(data.message);
                return;
            }
            messageShow(data.message);
            joins(data.json);
        }
    })
}

function bindDelete() {
    $("tr").each(function () {
        $(this).find("td").eq(6).click(function (e) {
            var curClickTd = $(this);
            var tr = curClickTd.parent();
            zdconfirm('系统确认框','确定删除该题目吗？',function(r){
                if(r) {
                    $.ajax({
                        type: "delete",
                        url: path + "/problem",
                        data: {
                            "term": tr.find('td').eq(2).text(),
                            "pname": tr.find('td').eq(1).text(),
                            "cname": tr.find('td').eq(0).text(),
                        },
                        success: function (data) {
                            tr.remove();
                        }
                    });
                }
            });
        })
    })
}

function bindClickTr() {
    $("table tr").click(function () {
        // 点击某行后，高亮该行
        $("table tr").removeAttr("style");
        $(this).attr("style", "background:#99529b;color: #fff");
        // 保存当前的点击行对象，然后用于修改成功之后，对当前行进行赋值
        curClickTr = $(this);
        var term = $(this).find("td").eq(2).text();
        $("select[name=term] option[value=" + term + "]").prop('selected', true);
        flag = true;
        changeTerm();
    });
}