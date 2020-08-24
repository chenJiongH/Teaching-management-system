var allGrade;
// 记录当前点击行，用来把教师提交的评分回写到table行里面。
var nowClickTr;
$(function () {

    var x = 10;
    var y = 20;
    var newtitle = '';
    $('.right p').mouseover(function(e) {
        newtitle = $(this).text();
        if (this.offsetHeight < this.scrollHeight) {
            $('body').append('<div id="tooltip">' + newtitle + '</div>');
            $('#tooltip').css({
                'left': (e.pageX + x + 'px'),
                'top': (e.pageY + y + 'px')
            }).show();
        }
    }).mouseout(function() {
        $('#tooltip').remove();
    }).mousemove(function(e) {
        $('#tooltip').css({
            'left': (e.pageX + x + 'px'),
            'top': (e.pageY + y + 'px')
        }).show();
    })
/*
    $(".right p").mouseenter(function () {
        console.log(this.offsetHeight);
        console.log(this.scrollHeight);
        if (this.offsetHeight < this.scrollHeight)
            $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
    });
    $(".right p").mouseleave(function () {
        $(this).attr('data-toggle', '');
    });
    */
    // 版本1.十年学期硬编码在页面上
    // 版本2.学期只显示该教师有教课的学期
    // $("select[id=term]").html(tenTermOptions);
    // changeTerm();

    $("#term").change(changeTerm);
    $("#course").change(changeCourse);
    $("#program").change(get);

    // 第一层请求学期
    $.ajax({
        type: "get",
        url: path + "/term",
        xhrFields: {
            withCredentials: true,
        },
        success: function (data) {
            var options = joinOptions(data);
            $("#term").html(options);
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
        xhrFields: {
            withCredentials: true,
        },
        // url: "/course",
        data: {'term': $("select[id=term]").val()},
        success: function (data) {
            var options = joinOptions(data);
            $("#course").html(options);
            changeCourse();
        }
    })
}

function changeCourse() {
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
            $("#program").html(options);
            get();
        }
    })
}

function get() {
    $.ajax({
        type: "get",
        url: path + "/grade/conditions",
        data: {'term': $("select[id=term]").val(),
            'cname': $("select[id=course]").val(),
            'pname': $("select[id=program]").val()
        },
        success: function (data) {
            joins(data);
            pro();
            allGrade = {};
            for (var i = 0; i <data.length; i++) {
                allGrade[data[i].sno] = [data[i].onean, data[i].twoan, data[i].threean];
            }
            if (data.length == 0) {
                $("#onean").text("");
                $("#twoan").text("");
                $("#threean").text("");
            }
            // 让页面刷新显示第一个学生的答案
            $("table tr:eq(1)").click();
        }
    })
}
function pro(){
    $.ajax({
        type: "get",
        url: path + "/problem",
        // url: "/problem",
        data: $("form").serialize(),
        xhrFields: {
            withCredentials: true,
        },
        success:function (data) {
            if (data == "") {
                $("#onepro").text("");
                $("#twopro").text("");
                $("#threepro").text("");
                return;
            }
            $("#onepro").text(data.onepro);
            $("#twopro").text(data.twopro);
            $("#threepro").text(data.threepro);
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
            tr += "<td>" + json.sno + "</td>";
            tr += "<td>" + json.name + "</td>";
            json.score = json.score == null?"":json.score
            tr += "<td>" + json.score + "</td>";
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
    $("table tr").click(function () {
        nowClickTr = $(this);
        var sno = $(this).find('td:eq(0)').text();
        $("#onean").text(allGrade[sno][0]);
        $("#twoan").text(allGrade[sno][1]);
        $("#threean").text(allGrade[sno][2]);
        // 用于发送评分请求
        $("input[name=sno]").val(sno);
        $("input[name=name]").val($(this).find('td:eq(1)').text());
        // 用于显示该学生的分数到评分框
        $("input[name=score]").val($(this).find('td:eq(2)').text());
    });
}

function put() {
    if($("input[name=score]").val() === "" || $("input[name=score]").val() < 0 || parseFloat($("input[name=score]").val()) > 100) {
        messageShow("请输入有效评分");
        return;
    }
    $.ajax({
        type:"put",
        url:path + "/grade/teacher",
        // url:"/grade/teacher",
        xhrFields: {
            withCredentials: true,
        },
        data:$("form").serialize(),
        success:function(data) {
            messageShow(data);
            if (data == "提交失败") {
                return;
            }
            nowClickTr.find("td:eq(2)").text($("input[name=score]").val());
        }
    })
}

function pageAjax() {

}

