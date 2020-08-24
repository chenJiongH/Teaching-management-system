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
    $("select").change(getFile);
    pageAjax();
})
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
            $("select").change();

        })
}

function showPeriod() {
    var pname = $("select").val();
    $("input[name=start]").val(project[pname][0]);
    $("input[name=end]").val(project[pname][1]);

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
    var end =$("input[name=end]").val();
    console.log(time, end);
    if (time > end) {
        ue.ready(function () {
            ue.setDisabled('fullscreen');
        })
        $(".btn").removeAttr("onclick");
    } else {
        ue.ready(function () {
            ue.setEnabled();
        })
        $(".btn").attr("onclick", "submitAn()");
    }
    ue.ready(function () {
        ue.isFocus();
    })
}

function changeProject() {
    ajax("get", path + "/lbproblem", $("form").serialize())
        .then((data) => {
            problem = data;
            // 获取该项目下的答题、评分信息，并展示题目和答案
            getGrade();
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

    try {
        lbgrade["an" + problemArray[oldpage]] = ue.getContent();
    } catch (e) {
        // e.message()
    }
    // 切换页码列表
    pageNumber();
    // 因为每一页放置两个题目
    newpage = parseInt(curpage);
    // 初始化新的页面
    // 显示题目
    $(".question label").text("题目" + newpage);
    $(".question p").html(problem[problemArray[parseInt(newpage)]]);
    // 显示已经存在的答题数据
    // 防止 lbgrade["an" + problemArray[newpage]] 为null时，抛出异常 影响下面的代码执行
    if (lbgrade["an" + problemArray[newpage]] == null) {
        ue.setContent("");
    } else {
        ue.setContent(lbgrade["an" + problemArray[newpage]]);
    }
    var number = parseInt(problem.number);
    // 判断两个富文本框是否显示
    if( number < newpage){
        $("#editor").css("visibility","hidden");
        $(".question p").html("");
    } else {
        $("#editor").css("visibility","unset");
    }
    ue.isFocus();
}
function getGrade() {
    ajax("get", path + "/lbgrade/stu", $("form").serialize())
        .then((data) => {
            lbgrade = data;

            // 回到第一页的题目
            oldpage = 1;
            newpage = 1;

            // 提取并改变页面数据，防止页面刷新时，第一页没有数据，而学生在之前已经写入了第一题的答案
            // 防止 lbgrade["an" + problemArray[newpage]] 为null时，抛出异常 影响下面的代码执行
            if (lbgrade["an" + problemArray[newpage]] == null || lbgrade["an" + problemArray[newpage]] == undefined) {
                ue.ready(function () {
                    ue.setContent("");
                })
            } else {
                try {
                    ue.ready(function () {
                        ue.setContent(lbgrade["an" + problemArray[newpage]]);
                    })
                } catch (e) {
                    // e.message();
                }
            }
            // 显示题目
            $(".question label").text("题目" + newpage);
            $(".question p").html(problem[problemArray[parseInt(newpage)]]);
            // 显示已经存在的答题数据
            // 防止 lbgrade["an" + problemArray[newpage]] 为null时，抛出异常 影响下面的代码执行
            if (lbgrade["an" + problemArray[newpage]] == null) {
                ue.ready(function () {
                    ue.setContent("");
                })
            } else {
                // 不等待会报数组undefined异常。然后设置不成功，没有数据
                setTimeout(function() {
                    ue.setContent(lbgrade["an" + problemArray[newpage]]);
                }, 500);
            }
        })
}
// 点击提交按钮
function submitAn() {
    lbgrade["an" + problemArray[newpage]] = ue.getContent();
    ajax("put", path + "/lbgrade/stu", JSON.stringify(lbgrade), 1)
        .then((data) => {
            messageShow(data);
        })
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
