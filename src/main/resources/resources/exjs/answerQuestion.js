var allData;
$(function () {

    // 十年学期写死在页面上
    $("select[id=term]").html(tenTermOptions);
    changeTerm();

    $("select[id=term]").change(changeTerm);
    $("select[id=cname]").change(changeCourse);
    $("select[id=pname]").change(selection);
    // 第一层请求学期
    /*$.ajax({
        type: "get",
        url: path + "/term",
        // url: "/term",
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
        xhrFields: {
            withCredentials: true,
        },
        // url: "/course",
        data: {'term': $("select[id=term]").val()},
        success: function (data) {
            var options = joinOptions(data);
            $("select[id=cname]").html(options);
            changeCourse();
        }
    })
}

// 是存储了所有项目的数组
var programs = {};

function changeCourse() {

    // 第三层请求该学期该课程下的项目
    $.ajax({
        type: "get",
        url: path + "/project/getAll",
        // url: "/project/getAll",
        data: {
            'term': $("select[id=term]").val(),
            'cname': $("select[id=cname]").val()
        },
        success: function (data) {
            var options = "";
            for (var i = 0; i < data.length; i++) {
                programs[data[i].pname] = data[i].start + " " + data[i].end;
                options += "<option value=" + data[i].pname + ">" + data[i].pname + "</option>"
            }
            showTime(data[0].pname);
            $("select[id=pname]").html(options);
            selection();
        }
    })
}

function selection() {
    // 第三层请求该学期该课程下的项目题目详情
    $.ajax({
        type: "get",
        url: path + "/problem",
        // url: "/problem",
        data: {
            'term': $("select[id=term]").val(),
            'cname': $("select[id=cname]").val(),
            'pname': $("select[id=pname]").val()
        },
        success: function (data) {
            showTime($("#pname").val());
            allData = data;
            if (data == "") {
                $("#onePro").text("");
                $("#twoPro").text("");
                $("#threePro").text("");
                return;
            }
            // 把题目显示在页面上
            $("#onePro").text(data.onepro);
            $("#twoPro").text(data.twopro);
            $("#threePro").text(data.threepro);

        }
    })
    // 第三层请求该学期该课程下的项目题目的答题和评分详情
    $.ajax({
        type: "get",
        url: path + "/grade/stu",
        // url: "/grade/stu",
        xhrFields: {
            withCredentials: true,
        },
        data: {
            'term': $("select[id=term]").val(),
            'cname': $("select[id=cname]").val(),
            'pname': $("select[id=pname]").val()
        },
        success: function (data) {
            showTime($("#pname").val());
            // 把学生的答题显示在题目上
            allData = data;
            $("#oneAn").val(data.onean);
            $("#twoAn").val(data.twoan);
            $("#threeAn").val(data.threean);

            // 判断当前项目是否已经过期
            var myDate = new Date();
            var time;
            var year = myDate.getFullYear();    //获取当前年
            time = year + "-";
            var month = myDate.getMonth()+1;   //获取当前月，并且格式化时间
            if (month < 10) {
                month = "0" + month;
            }
            time += month + "-";
            var date = myDate.getDate();       //获取当前日，并且格式化时间
            if (date < 10 ) {
                date = "0" + date;
            }
            time += date;

            var end = $("#end").val();
            if (time > end || data.score != null) {
                $("textarea").attr("readonly", "readonly");

                data.score = data.score == null ? "" : data.score;
                $("#message").text("分数：" + data.score);
                $("#message").css("display", "inline-block");
                $(".btn").removeAttr("onclick");
            } else {
                $(".btn").attr("onclick", "post()");
                $("#message").css("display", "none");
                $("textarea").removeAttr("readonly");
            }
        }
    })
}

function showTime(programName) {
    var time = programs[programName];
    $("#start").val(time.substring(0, time.indexOf(' ')));
    $("#end").val(time.substring(time.indexOf(' ') + 1));
}

// 学生提交答题详情
function post() {
    // 把题目都放到form
    $("input[name=onean]").val($("#oneAn").val());
    $("input[name=twoan]").val($("#twoAn").val());
    $("input[name=threean]").val($("#threeAn").val());
    $.ajax({
        type: "post",
        url: path + "/grade/stu",
        // url: "/grade/stu",
        data: $("form").serialize(),
        xhrFields: {
            withCredentials: true,
        },
        success: function (data) {
            messageShow(data);
        }
    });
}
function pageAjax() {
    
}