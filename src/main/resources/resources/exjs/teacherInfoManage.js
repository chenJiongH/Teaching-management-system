var blankOptions = "";
// 用来判断课程是由于点击单元行生成的。还是学期下拉框被用户改变而造成的。
var flag = false;
$(function () {


    $("select[name=term]").html(tenTermOptions);
    changeTerm();
    // 方便点中管理员的时候，清空option下拉框
    blankOptions = '<option hidden value=""></option>';
    $("select").append(blankOptions);

    $("select[id=term]").change(changeTerm);
    pageAjax();
})

function changeTerm() {
    // 第二层请求学期下的课程
    $.ajax({
        type: "get",
        url: path + "/course",
        data: {'term': $("select[id=term]").val()},
        success: function (data) {
            var options = joinOptions(data);
            $("select[id=cname]").html(options);
            if ($(curClickTr).find("td").eq(3).text() == "" && flag) {
                $("select[id=cname]").children().remove();
                flag = false;
            } else {
                if ($(curClickTr).find("td").eq(3).text() != "") {
                    $("select[name=cname] option[value=" + $(curClickTr).find("td").eq(3).text() + "]").prop('selected', true);
                }
            }
        }
    })
}
function joinOptions(data) {
    var options = "";
    for (var i = 0; i < data.length; i++) {
        options += "<option value=" + data[i] + ">" + data[i] + "</option>"
    }
    return options;
}

function pageAjax() {
    $.ajax({
        type: "get",
        url: path + "/teacher",
        data: {"curpage": curpage},
        success: function (data) {
            console.log(data);
            joins(data);
            // 点击行，然后把行数据挪动到上面或者清空
            if ($("table tr").eq(1).length == 0) {
                $("input").val("");
                $("select[name=term] option").eq(0).prop("selected", true);
            } else
                $("table tr").eq(1).click();
        }
    })
}

function joins(data) {
    $("table tr:gt(0)").remove();

    for(var i = 0; i < data.length; i++) {
        var json = data[i];
        var tr = "<tr>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.phone + "</td>";
        tr += "<td>" + json.password + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += "<td>删除</td>";
        tr += "</tr>";
        $("table").append(tr);
    }
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);
    // 单元行的点击事件，把数据从 table 挪动到 form
    bindClickTr();
    // 绑定点击单元行的最后一个删除操作单元格，发送 delete 请求，并删除页面点击行
    bindDelete();

}
function bindClickTr() {
    $("table tr").click(function () {
        // 点击某行后，高亮该行
        $("table tr").removeAttr("style");
        $(this).attr("style", "background:#99529b;color: #fff");
        // 保存当前的点击行对象，然后用于修改成功之后，对当前行进行赋值
        curClickTr = $(this);
        // 给用户看的效果
        $("input[name=name]").val($(this).find("td").eq(0).text());
        $("input[name=phone]").val($(this).find("td").eq(1).text());
        $("input[name=password]").val($(this).find("td").eq(2).text());

        // 给计算机看的，保存该条记录原来的手机号、课程名、学期名
        $("input[name=oriCname]").val($(this).find("td").eq(3).text());
        $("input[name=oriTerm]").val($(this).find("td").eq(4).text());
        $("input[name=oriPhone]").val($(this).find("td").eq(1).text());
        // 学期课程下拉框的选中。放在最后是因为若td为空，会导致异常
        if ($(this).find("td").eq(4).text() == "") {
            $("select[name=term] option").last().prop('selected', true);
            $("select[id=cname] option").remove();
            flag = true;
            changeTerm();
        } else {
            $("select[name=term] option[value=" + $(this).find("td").eq(4).text() + "]").prop('selected', true);
            changeTerm();
        }
    });
}
function bindDelete() {
    // 点击单元行的最后一个删除操作单元格，发送 delete 请求，并删除页面点击行
    $("tr").each(function () {
        $(this).find('td').eq(5).click(function () {
            var tr = $(this).parent();
            // 给计算机看的，保存该条记录原来的手机号、课程名、学期名
            $("input[name=oriCname]").val(tr.find("td").eq(3).text());
            $("input[name=oriTerm]").val(tr.find("td").eq(4).text());
            $("input[name=oriPhone]").val(tr.find("td").eq(1).text());

            zdconfirm('系统确认框','确定删除该教学记录吗？',function(r){ //项目删除确定框
                if(r) {
                    $.ajax({
                        type: "delete",
                        url: path + "/teacher",
                        data: $("form").serialize(),
                        dataType: 'text',
                        success: function (data) {
                            tr.remove();
                        }
                    })
                }
            });
        })
    })
}
function teacherPost() {
    var type = "input";
    var notNullFlag = "notnull";
    if (judge(type,  notNullFlag) != 0) {
        messageShow("请输入完整数据");
        return ;
    }
    type = "select";
    if (judge(type,  notNullFlag) != 0) {
        messageShow("请输入完整数据");
        return ;
    }
    $.ajax({
        type: "post",
        url: path + "/teacher",
        data: $("form").serialize(),
        dataType: 'text',
        success: function (data) {
            messageShow(data);
            $("table tr").removeAttr("style");
            if (data.code == "200" || data == "添加成功") {
                var trs = "<tr>";
                trs += "<td>" + $("input[name=name]").val() + "</td>";
                trs += "<td>" + $("input[name=phone]").val() + "</td>";
                trs += "<td>" + $("input[name=password]").val() + "</td>";
                trs += "<td>" + $("select[name=cname]").val() + "</td>";
                trs += "<td>" + $("select[name=term]").val() + "</td>";
                trs += "<td>删除</td>";
                trs += "</tr>";
                // 管理员记录始终在第一行，新添加的教师记录位于第二行
                if ($("tr:eq(1)").find("td").eq(4).text() == "") {
                    $("tr").eq(1).after(trs);
                } else
                    $("tr").eq(0).after(trs);
                // 绑定点击单元行的最后一个删除操作单元格，发送 delete 请求，并删除页面点击行
                // 否则新增加的行没有删除功能
                $("table tr").unbind();
                bindClickTr();
                bindDelete();
            }
        }
    })
}

function teacherPut() {

    $.ajax({
        type: "put",
        url: path + "/teacher",
        // url: "/teacher",
        data: $("form").serialize(),
        dataType: 'text',
        success: function (data) {
            if (data == '修改成功') {
                curClickTr.find("td").eq(0).text($("input[name=name]").val());
                curClickTr.find("td").eq(1).text($("input[name=phone]").val());
                curClickTr.find("td").eq(2).text($("input[name=password]").val());
                curClickTr.find("td").eq(3).text($("select[name=cname]").val());
                curClickTr.find("td").eq(4).text($("select[name=term]").val());
                // 当修改成功时，把ori类型也改动，防止用户点击一行记录，连续改动了两次主键。则第二次以后，主键已经被修改了
                $("input[name=oriCname]").val($("select[name=cname]").val());
                $("input[name=oriTerm]").val($("select[name=term]").val());
                $("input[name=oriPhone]").val($("input[name=phone]").val());
            } else {
                messageShow(data);
            }
        }
    })
}

function judge(type, notNullFlag) {
    var notNullNumber = 0;
    $(type + "[" + notNullFlag + "]").each(function () {
        if ($(this).val() == "") {
            notNullNumber ++;
        }
        console.log($(this).val());
    });
    return notNullNumber;
}