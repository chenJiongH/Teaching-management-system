var dict = {};
var oldpage = 1;
var newpage = 1;
var problem = ["ll", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen"];
// 区分是点击了修改按钮还是添加按钮。可以用来点击时发送put请求或者是post请求
var type = "";
// 是一个存放纯文本的题目数组，提交时转化为字符串，以@#$%相连
var plainProblem = [];
$(function () {
    ajax('get', path + "/lbterm").then((data) => {
        var options = joinOptions(data);
        $("#term").html(options);
        changerCname();
    })
    $("#term").change(changerCname);
    $("#cname").change(pageAjax);
    $("#set .sequence li").click(clickPages);
    // $("#set .sequence li").click();
})

function changerCname() {
    ajax('get', path + "/lbcname", {"term": $("#term").val()}).then((data) => {
        var options = joinOptions(data);
        $("#cname").html(options);
        pageAjax();
    })
}

function pageAjax() {
    ajax('get', path + "/lbprojectAndProblem",
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

    for (var i = 0; i < data.length; i++) {
        var json = data[i];
        tr += "<tr  ondblclick='dbclickTr(this)'>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.pname + "</td>";
        tr += "<td>" + json.term + "</td>";
        tr += "<td>" + json.number + "</td>";
        tr += "<td>" + json.start + "</td>";
        tr += "<td>" + json.end + "</td>";
        json = json.lbproblem;
        var plaintext = json.plaintext;// 保存所有题目纯文本的字符串
        plaintext = plaintext.split("@#$%");
        for (var j = 1; j <= 15; j++) {
            // 数组未定义会输出undefined
            if (plaintext[j] == undefined) {
                plaintext[j] = "";
            }
            // 自定义一个zdy标签，用来存放题目的html代码，实际显示的是题目的纯文本代码。
            tr += `<td>${plaintext[j]}<zdy style="display: none">${json[problem[j]]}</zdy></td>`;
        }
        if (i % 2 == 0) {
            tr += '<td onclick="del(' + i + ')" style="background-color: #ffffff;right: 3227px;\n' +
                '    position: relative;">删除</td>';
        } else {
            tr += '<td onclick="del(' + i + ')" style="background-color: #fbfbfb;right: 3227px;\n' +
                '    position: relative;">删除</td>';
        }
        tr += "</tr>";
        dict[i] = [json.term, json.cname, json.pname];
    }
    $(".t_div table tr:gt(0)").remove();
    $(".t_div table").append(tr);
    $(".t_div table td").mouseenter(enter);
    $(".t_div table td").mouseleave(leave);
    // 给每一行绑定点击事件
    $("table tr").click(function () {
        curClickTr = $(this);
        $("#term option[value=" + $(this).find("td").eq(0).text() + "]");
        $("#cname option[value=" + $(this).find("td").eq(2).text() + "]");
        $("#pname").val($(this).find("td").eq(1).text());
        $("#number").val($(this).find("td").eq(3).text());
        $("#start").val($(this).find("td").eq(4).text());
        $("#end").val($(this).find("td").eq(5).text());
        $("input[name=oriCname]").val($(this).find("td").eq(0).text());
        $("input[name=oriTerm]").val($(this).find("td").eq(2).text());
        $("input[name=oriPname]").val($(this).find("td").eq(1).text());
        // 把点击行的题目全部放到form表单上去
        var k = 6;
        for (var i = 0; i < $("#number").val(); i++) {
            // 各个题目框的html代码
            $(`#lbproblem${problem[k - 5]}`).val($(this).find(`td:eq(${k})`).find("zdy").html());
            // 各个题目的纯文本
            plaintext[i + 1] = $(this).find(`td:eq(${k})`).text();
            k++;
        }
        $(`#lbproblemplaintext`).val(plaintext.join("@#$%"));
    })

    // 滚动条左右滚动，固定最右面的一列
    $(".t_div").scroll(function () {
        $("th:last").css({
            'right': 3227 - $(".t_div").scrollLeft() + "px",
        });
        $("tr").each(function () {
            $(this).find("td:last").css({
                'right': 3227 - $(".t_div").scrollLeft() + "px",
            })
        })
    })
}

function del(i) {
    zdconfirm("系统确认框", "确定删除该记录吗？", function (r) {
        if (r) {
            var term = dict[i][0];
            var cname = dict[i][1];
            var pname = dict[i][2];
            ajax("delete", path + "/lbproject",
                {
                    "term": term,
                    "cname": cname,
                    "pname": pname

                }).then((data) => {
                messageShow(data);
                pageAjax();
            })
        }
    })
}

function clickPages() {
    // 刚点击添加修改时，给ueditor赋值 - 已弃用，会导致从刷新时的第一页，点击第二页时，因为old和new都相同，导致第一页的数据没有被保存，就被覆盖了
    // if (oldpage == newpage && type == "put") {
    //     ue.setContent($(`#lbproblem${problem[newpage]}`).val());
    // }

    oldpage = newpage;
    var content = ue.getContent();
    $("#lbproblem" + problem[oldpage]).val(content);
    plainProblem[oldpage] = ue.getContentTxt();

    ue.execCommand('cleardoc');
    if ($(this).text() == "上一页") {
        if (page != 1)
            page = page - 1;
    } else if ($(this).text() == "下一页") {
        page = parseInt(page) + 1;
    } else
        page = $(this).text();
    newpage = page;

    var number = parseInt($("#number").val());
    UE.getEditor('editor').execCommand('insertHtml', $("#lbproblem" + problem[newpage]).val());
    if (number < page) {
        $("#editor").css("visibility", "hidden");
    }
    else {
        $("#editor").css("visibility", "unset");
    }
    var lis = "";
    var begin,
        end;
    if (page == 1) {
        lis += '<li class="special fontColor">上一页</li>';
        begin = 1;
        end = 10;
    } else if (page < 10 && page > 1) {
        lis += '<li class="special">上一页</li>';
        begin = 1;
        end = 10;
    } else {
        lis += '<li class="special">上一页</li>';
        begin = page - 8;
        end = parseInt(page) + 1;
    }

    for (var i = begin; i <= end; i++) {

        if (i == page) {
            lis += '<li class="current">' + i + '</li>';
            $(".que").text("题目" + i + ":");


        } else
            lis += '<li>' + i + '</li>'
    }

    lis += '<li class="special">下一页</li>';

    $("#set .sequence").html(lis);

    $("#set .sequence li").click(clickPages);
}

function postProblem() {
    $("#lbproblem" + problem[newpage]).val(ue.getContent());
    plainProblem[newpage] = ue.getContentTxt();
    $("#lbproblemplaintext").val(plainProblem.join("@#$%"));
    // 点击提交以后，恢复到第一页，方便之后再次打开
    // 防止用户往后点多页，而第一页不是1
    $("#set .sequence li:eq(1)").text("1");
    $("#set .sequence li:eq(1)").click();
    // 提交增加
    if (type == "post") {
        ajax('post', path + '/lbproject', $(".video_f").serialize()).then((data) => {
            messageShow(data);
            pageAjax();
            cancel();
        });
    } else if (type == "put") {
        // 提交修改
        ajax('put', path + '/lbproject', $(".video_f").serialize()).then((data) => {
            messageShow(data);
            pageAjax();
            cancel();
        });
    }
}

// 点击修改时，首先查询出该项目的所有题目，并赋值给题目文本框，这样用户就可以在富文本框中修改题目
function put() {
    if ($("#pname").val() == "" || $("#start").val() == "" ||  $("#end").val() == "" ) {
        messageShow("请输入完整的信息！");
        return ;
    }
    type = "put";
    ajax("get", `${path}/lbproblem`, $(".video_f").serialize()).then((data) => {
        if (data == "") {
            messageShow("该章节名不存在，无法修改！");
            return ;
        }
        // 各个题目的纯文本
        $(`#lbproblemplaintext`).val(data.plaintext);
        if (data.plaintext == null) {
            data.plaintext = "";
        }
        plainProblem = data.plaintext.split("@#$%");
        for (var i = 0; i < data.number; i++) {
            $("#lbproblem" + problem[i + 1]).val(data[problem[i + 1]]);
        }
        // 展示第一页的数据
        ue.setContent($(`#lbproblemone`).val());
        // 显示隐藏的题目输入框
        oldpage = newpage = 1;
        $("#curtain").css("display", "block");
        $("#set").css("display", "block");
    })
}

function post() {
    if ($("#pname").val() == "" || $("#start").val() == "" ||  $("#end").val() == "" ) {
        messageShow("请输入完整的信息！");
        return ;
    }
    type = "post";
    $("#curtain").css("display", "block");
    $("#set").css("display", "block");
}

// 设置题目点击×关闭、所有的题目框删除、新页码处于第一页
function cancel() {
    // 防止用户往后点多页，而第一页不是1
    $("#set .sequence li:eq(1)").text("1");
    $("#set .sequence li:eq(1)").click();
    $("input[name^=lb]").val("");
    // 各个题目的纯文本
    $(`#lbproblemplaintext`).val("");
    plainProblem = [];
    ue.setContent("");
    $("#curtain").css("display", "none");
    $("#set").css("display", "none");
}
function dbclickTr(e) {
    // 双击某一行之后开始修改
    $(e).click();
    setTimeout($(".purpleBtn").click(), 500);
}