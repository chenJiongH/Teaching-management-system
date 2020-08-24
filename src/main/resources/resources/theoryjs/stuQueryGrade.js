// 设置project字典，里面以pname为键，保存该pname的开始时间和结束时间
var project = {};
// 设置problem字典，存放当前项目的题目信息
var problem = {};
// 设置answer字典，存放当前项目学生的答题信息
var lbgrade = {};

var oldpage = 1;
var newpage = 1;
var problemArray = ["ll","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"];
$(function () {
    $(".sequence li").unbind("click");
    // 改变页面的题目，并保存
    $(".sequence li").click(changePage);
    // 使用设置好的project数组，里面以pname为键，保存该pname的开始时间和结束时间
    $("select").change(showPeriod);
    // 获取该项目下的题目信息
    $("select").change(changeProject);
    pageAjax();
});
function pageAjax() {
    // 获取用户姓名
    ajax("get", path + "/user/name")
        .then((data) => {
            $("#nick").val(data);
        })
    // 获取当前的学期、课程
    ajax("get", path + "/lbstu/start")
        .then((data) => {
            $(".term").html("&nbsp&nbsp" + data[1]);
            $(".term").val(data[1]);
            $(".cname").html("&nbsp&nbsp" + data[0]);
            $(".cname").val(data[0]);
            return ajax("get", path + "/lbproject", $("form").serialize())
        })
        // 获取当前的所有项目
        .then((data) => {
            var tr = "";
            for (var i = 0; i < data.length; i++) {
                project[data[i].pname] = [data[i].start, data[i].end, data[i].number];
                tr += "<option value=" + data[i].pname + ">" + data[i].pname + "</option>";
            }
            $("select").html(tr);
            getFile();
            $("select").change();
        })
}
function showPeriod() {
    var pname = $("select").val();
    $("input[name=start]").val(project[pname][0]);
    $("input[name=end]").val(project[pname][1]);
}

function changeProject() {
    ajax("get", path + "/lbproblem", $("form").serialize())
        .then((data) => {
            problem = data;
            // 获取该项目下的答题、评分信息，并展示题目和答案
            getGrade();
        })
}

function getGrade() {
    ajax("get", path + "/lbgrade/stu", $("form").serialize())
        .then((data) => {
            lbgrade = data;
            if (data == "" ||data.score == null || data.score == undefined) {
                $(".totalScore").text(`总分数：`);
            } else
                $(".totalScore").text(`总分数：${data.score}`);
            // 回到第一页的题目
            oldpage = 1;
            newpage = 1;
            // 因为getGrade函数，获取评分数据需要时间
            setTimeout(function() {
                $(".sequence li:eq(1)").click()
            }, 500);
        })
}
// 根据当前页码显示题目、评分数据
// 改变页面的题目，并保存
function changePage() {
    var tempPage = curpage;
    // 获取当前页码
    if ($(this).text() == "上一页") {
        if (tempPage != 1)
            tempPage = tempPage - 1;
    } else if ($(this).text() == "下一页") {
        tempPage = parseInt(tempPage) + 1;
    } else
        tempPage = $(this).text();
    // 页码限制20页
    if (tempPage > 20) {
        return ;
    }
    curpage = tempPage;
    oldpage = newpage;
    pageNumber()
    newpage = parseInt(curpage);
    // 初始化新的页面
    // 显示题目
    $(".ques label").text("题目" + newpage);
    $(".ques div:eq(0)").html(problem[problemArray[parseInt(newpage)]]);
    // 显示已经存在的答题数据
    var number = parseInt(problem.number);
    // 防止 lbgrade["an" + problemArray[newpage]] 为null时，抛出异常 影响下面的代码执行
    if (lbgrade["an" + problemArray[newpage]] == null) {
        $(".ques div:eq(1)").html("");
    } else {
        $(".ques div:eq(1)").html(lbgrade["an" + problemArray[newpage]]);
    }
    // 显示评分
    $(".grade li").text(lbgrade[problemArray[newpage]]);
    // 判断两个富文本框是否显示
    if( number < newpage){
        $(".ques").css("visibility","hidden");
        $(".grade li").text("");
    } else {
        $(".ques").css("visibility","unset");
    }
}
function pageNumber() {

    var lis = "";
    var begin,
        end;
    if (curpage == 1) {
        lis += '<li class="special fontColor">上一页</li>';
        begin = 1;
        end = 10;
    } else if (curpage < 10 && curpage > 1) {
        lis += '<li class="special">上一页</li>';
        begin = 1;
        end = 10;
    } else if (curpage == 20) {
        lis += '<li class="special">上一页</li>';
        begin = 11;
        end = 20;
    } else {
        lis += '<li class="special">上一页</li>';
        begin = curpage - 8;
        end = parseInt(curpage) + 1;
    }

    for (var i = begin; i <= end; i++) {
        if (i == curpage) {
            lis += '<li class="current">' + i + '</li>';
        } else
            lis += '<li>' + i + '</li>'
    }

    lis += '<li class="special">下一页</li>';
    $(".sequence").html(lis);
    $(".sequence li").click(changePage);
}
