var allData;
// 用来判断课程是由于点击单元行生成的。还是学期下拉框被用户改变而造成的。
var flag = false;
$(function () {
    pageAjax();
    // 十年学期写死在页面上
    $("select[id=term]").html(tenTermOptions);
    changeTerm();

    $("select[id=term]").change(changeTerm);
    // 第一层请求学期
    /*$.ajax({
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
        // url: "/course",
        data: {'term': $("select[id=term]").val()},
        success: function (data) {
            var options = joinOptions(data);
            $("select[id=course]").html(options);
            // 证明此时是点击行时间触发的课程改变
            if (flag) {
                flag = false;
                $("select[name=cname] option[value=" + $(curClickTr).find("td").eq(3).text() + "]").prop('selected', true);
            }
        }
    })
}



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
            tr += "<td title="+ json.cname +">" + json.cname + "</td>";
            tr += "<td>" + json.term + "</td>";
            tr += "<td title="+ json.pname +">" + json.pname + "</td>";
            tr += "<td>" + json.start + "</td>";
            tr += "<td>" + json.end + "</td>";
            tr += "<td id ='"+json.id+"'>删除</td>"

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
    // 绑定行点击事件
    bindClickTr();
    // 绑定行尾删除事件
    bindDelete();
}

/**
 * 点击页码，然后对查询到的全局数据进行分页展示
 */
function pageAjax() {
    $.ajax({
        type:"get",
        url:path + "/project/getPage",
        data:{"curpage":curpage},
        success:function(data){
            console.log(data);
            joins(data);
        }
    })
}
function post() {

    if ($("#program").val() === "" || $("#startDate").val() > $("#endtDate").val() || $("#startDate").val() === "") {

        messageShow("数据无效");
        return ;
    }
    // 200 添加成功，400 修改成功 添加失败
    $.ajax ({
        type: "post",
        url: path + "/project",
        // url: "/project",
        data:$("form").serialize(),
        success: function(data) {
            $("table tr").removeAttr("style");
            messageShow(data.message);
            if (data.code == 200) {
                joins(data.json);
                // 否则新增加的行没有删除功能
                $("table tr").unbind();
                bindClickTr();
                bindDelete();
            }
            if (data.message == "修改成功") {
                data = data.json;
                $("table tr").each(function () {
                    if ($(this).find("td:eq(1)").text() == $("select[name=term]").val()) {
                        if ($(this).find("td:eq(0)").text() == $("select[name=cname]").val()) {
                            if ($(this).find("td:eq(2)").text() == $("input[name=pname]").val()) {
                                $(this).find("td:eq(3)").text(data.start);
                                $(this).find("td:eq(4)").text(data.end);
                                return;
                            }
                        }
                    }
                });

            }
        }
    })
}

function bindDelete() {
    $("tr").each(function () {
        $(this).find("td:eq(5)").click(function () {
            var curClickTd = $(this);
            var tr = $(this).parent();
            zdconfirm('系统确认框','确定删除该项目吗？',function(r){
                if(r) {
                    $.ajax({
                        type: "delete",
                        url: path + "/project",
                        data: {
                            "cname": tr.find('td:eq(0)').text(),
                            "term": tr.find('td:eq(1)').text(),
                            "pname": tr.find('td:eq(2)').text()
                        },
                        dataType: 'text',
                        success: function () {
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
        var term = $(this).find("td").eq(1).text();
        var cname = $(this).find("td").eq(2).text();
        // 给用户看的效果
        $("input[name=pname]").val($(this).find("td").eq(2).text());
        $("input[name=start]").val($(this).find("td").eq(3).text());
        $("input[name=end]").val($(this).find("td").eq(4).text());

        $("select[name=term] option").last().prop('selected', true);
        $("select[id=cname] option").remove();

        $("select[name=term] option[value=" + term + "]").prop("selected", true);
        flag = true;
        changeTerm();
    });
}