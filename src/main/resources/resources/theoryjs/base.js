var curpage = 1;
var page = 1;
var tenTermOptions = "";
var curClickTr;
// var path = "http://47.92.68.218:8080";
var path = "";
$(function () {
    name();
    var url = window.location.pathname;
    $("input[name=term]").next().click(function () {
        $("input[name=term]").click();
    });
    $("input[name=cname]").next().click(function () {
        $("input[name=cname]").click();
    });
    if (url.charAt(12) == 'm' || url.indexOf("editTeacherInfo.html") != -1) {
        $(".sideBar li:last").css("display", "none");
        $(".sideBar li:eq(2) span").text("校园图片");
        $(".sideBar li:eq(3) span").text("课程资料");
    }
    // 点击logo文字，返回理论系统登录页面
    $(".logo p").css("cursor", "pointer");
    $(".logo p").click(function () {
        location.replace("main.html");
    });
    // 点击返回主页 返回理论、实验页面
    $(".backMain").click(function () {
        location.replace("../..");
    });
    // 点击主页 返回登录进来的主页面
    var homePage = $("#selection a:first")
    var url = window.location.pathname;
    if (url.indexOf("teacher") != -1) {
        homePage.attr("href", "teacherWelcome.html");
        // 导入学生信息放到第一个
        var importStuHtml = $(".sideBar li:eq(5)").html();
        $(".sideBar ul").prepend(`<li>${importStuHtml}</li>`);
        $(".sideBar li:eq(6)").remove();
        // 菜单栏文字改动
        $(".sideBar li:eq(0) span").text("学生信息");
        $(".sideBar li:eq(1) span").text("章节题目");
        $(".sideBar li:eq(2) span").text("评分管理");
        $(".sideBar li:eq(3) span").text("课程资料");
        $(".sideBar li:eq(6) span").text("个人信息");
        // 主页改成教师登陆
        $("#selection a:eq(0)").text("教师登陆");
        var tips = `<span id='tips'>提示：点击‘返回主页’返回实验、理论选择页面；点击‘无机化学系教学系统’返回登陆页面，点击‘教师登陆’返回登陆进系统主界面。</span>`;
        $("#selection").append(tips);
    } else if (url.charAt(12) == 'm') {
        homePage.attr("href", "mwelcome.html");
        // 增加个人信息修改页面
        var mChangInfo = `
				<li>
					<a href="mEditSelfInfo.html">
						<img src="../images/修改grey.png" alt="">
						<span>个人信息</span></a>
				</li>`;
        if (url.indexOf('mEditSelfInfo.html') != -1) {
            mChangInfo = `
				<li>
					<a href="mEditSelfInfo.html">
						<img src="../images/修改red.png" alt="">
						<span class="current">个人信息</span></a>
				</li>`
        }
        $(".sideBar li:last").before(mChangInfo);
        // 主页改成管理员登陆
        $("#selection a:eq(0)").text("管理员登陆");
        $(".sideBar li:eq(0) span").text("教师信息");
        $(".sideBar li:eq(1) span").text("课程信息");
        var tips = `<span id='tips'>提示：点击‘返回主页’返回实验、理论选择页面；点击‘无机化学系教学系统’返回登陆页面，点击‘管理员登陆’返回登陆进系统主界面。</span>`;
        $("#selection").append(tips);
    } else if (url.indexOf("stu") != -1) {
        homePage.attr("href", "stuLogin.html");
        // 主页改成学生登陆
        $("#selection a:eq(0)").text("学生登陆");
        var tips = `<span id='tips'>提示：点击‘返回主页’返回实验、理论选择页面；点击‘无机化学系教学系统’返回登陆页面，点击‘学生登陆’返回登陆进系统主界面。</span>`;
        $("#selection").append(tips);
    } else {
        homePage.attr("href", "main.html");
    }

    footerlink();
    $("input").attr("autocomplete", "off");
    for (var i = 2020; i <= 2030; i++) {
           var year = i+"-"+(i+1)+"学年";
           tenTermOptions += "<option value=" + year + "第一学期>" + year + "第一学期</option>";
           tenTermOptions += "<option value=" + year + "第二学期>" + year + "第二学期</option>";
       }

    $(".content .sequence li").click(clickPage);
    $(".sequence li").click(pageAjax);

    // 固定表格头不滚动
    $(".t_div").scroll(function () {
        $("th").css({
            'top': $(".t_div").scrollTop(),
            'background-color': '#f9f4fd'
        });
    })
})

// 下拉框选项的拼接
function joinOptions(data) {
    var options = "";
    for (var i = 0; i < data.length; i++) {
        options += "<option value=" + data[i] + ">" + data[i] + "</option>"
    }
    return options;
}

function joinOptionPname(data) {
    var options = "";
    for (var i = 0; i < data.length; i++) {
        options += "<option value=" + data[i].pname + ">" + data[i].pname + "</option>"
    }
    return options;
}

// 每个页面底部的链接
function footerlink() {
    $("#emial").prop("href", "");
    $("#phone").prop("href", "");
    $("#buy").prop("href", "");
    $("#lab").prop("href", "");
    $("#fznews").prop("href", "");
}

function ajax(type, url, data, number = 0) {
    var contentType = ["application/x-www-form-urlencoded; charset=utf-8", "application/json; charset=utf-8"];
    return new Promise((resolve) => {
        $.ajax({
            type: type,
            url: url,
            data: data,
            contentType: contentType[number],
            xhrFields: {
                withCredentials: true,
            },
            success: function (data) {
                resolve(data);
            }
        });
    })
}

function fileChoice(e) {
    $(e).prev().click();
    return false;
}

function fileChange(e) {
    var fileName = $(e).val().substring($(e).val().lastIndexOf('\\') + 1);
    $(e).prev().text(fileName);
    $(e).prev().val(fileName);
}

function enter() {
    if (this.offsetWidth < this.scrollWidth) {
        $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
    }
}

function leave() {
    $(this).attr('data-toggle', '');
}

function clickPage() {
    if ($(this).text() == "上一页") {
        if (curpage != 1)
            curpage = curpage - 1;
    } else if ($(this).text() == "下一页") {
        curpage = parseInt(curpage) + 1;
    } else
        curpage = $(this).text();

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

    $(".content .sequence li").click(clickPage);
    $(".sequence li").click(pageAjax);
}
function name() {
    ajax("get", `${path}/user/name`).then((data) => {
        $("#name").text(data);
        var url = location.pathname;
        if (url.indexOf("main.html") == -1 && url.indexOf("video.html") == -1 && url.indexOf("schedule.html") == -1 && url.indexOf("notice.html") == -1 && url.indexOf("news.html") == -1) {
            $(".backMain").before(`<span id="nameText">${data}</span>`);
        }
    })
}

function messageShow(data) {
    $("#message").text(data);
    $("#message").css('display', "block");
    setTimeout(function () {
        $("#message").css('display', "none");
    }, 3000);
}

// 自定义提示框和确认框
(function($) {
    $.alerts = {
        alert: function(title, message, callback) {
            if( title == null ) title = 'Alert';
            $.alerts._show(title, message, null, 'alert', function(result) {
                if( callback ) callback(result);
            });
        },

        confirm: function(title, message, callback) {
            if( title == null ) title = 'Confirm';
            $.alerts._show(title, message, null, 'confirm', function(result) {
                if( callback ) callback(result);
            });
        },


        _show: function(title, msg, value, type, callback) {

            var _html = "";

            _html += '<div id="mb_box"></div><div id="mb_con"><span id="mb_tit">' + title + '</span>';
            _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
            if (type == "alert") {
                _html += '<input id="mb_btn_ok" type="button" value="确定" />';
            }
            if (type == "confirm") {
                _html += '<input id="mb_btn_ok" type="button" value="确定" />';
                _html += '<input id="mb_btn_no" type="button" value="取消" />';
            }
            _html += '</div></div>';

            //必须先将_html添加到body，再设置Css样式
            $("body").append(_html); GenerateCss();

            switch( type ) {
                case 'alert':

                    $("#mb_btn_ok").click( function() {
                        $.alerts._hide();
                        callback(true);
                    });
                    $("#mb_btn_ok").focus().keypress( function(e) {
                        if( e.keyCode == 13 || e.keyCode == 27 ) $("#mb_btn_ok").trigger('click');
                    });
                    break;
                case 'confirm':

                    $("#mb_btn_ok").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(true);
                    });
                    $("#mb_btn_no").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(false);
                    });
                    $("#mb_btn_no").focus();
                    $("#mb_btn_ok, #mb_btn_no").keypress( function(e) {
                        if( e.keyCode == 13 ) $("#mb_btn_ok").trigger('click');
                        if( e.keyCode == 27 ) $("#mb_btn_no").trigger('click');
                    });
                    break;


            }
        },
        _hide: function() {
            $("#mb_box,#mb_con").remove();
        }
    }
    // Shortuct functions
    zdalert = function(title, message, callback) {
        $.alerts.alert(title, message, callback);
    }

    zdconfirm = function(title, message, callback) {
        $.alerts.confirm(title, message, callback);
    };
    //生成Css
    var GenerateCss = function () {

        $("#mb_box").css({ width: '100%', height: '100%', zIndex: '99999', position: 'fixed',
            filter: 'Alpha(opacity=60)', top: '0', left: '0', opacity: '0.6'
        });

        $("#mb_con").css({ zIndex: '999999', width: '20%', position: 'fixed',
            backgroundColor: 'White', borderRadius: '15px', boxShadow: '0px 3px 9px #8b8585'
        });

        $("#mb_tit").css({ display: 'block', fontSize: '15px', color: '#455fe7', padding: '10px 15px 10px 14px', //系统确认框字眼控制
            backgroundColor: '#DDD', borderRadius: '15px 15px 0 0',
            borderBottom: '3px solid #455fe7', fontWeight: 'bold'
        });
        $("#mb_btn_no").css({ border: '1px rgb(218, 220, 224) solid', backgroundColor: '#dddddd', color: '#455fe7', marginLeft: '20px' });
        $("#mb_btn_ok").css({ backgroundColor: '#455fe7', color: 'white' });

        $("#mb_msg").css({ lineHeight: '55px', textAlign: 'center',
            borderBottom: '1px dashed #DDD', fontSize: '16px'
        });

        $("#mb_ico").css({ display: 'block', position: 'absolute', right: '10px', top: '9px',
            border: '1px solid Gray', width: '18px', height: '18px', textAlign: 'center',
            lineHeight: '16px', cursor: 'pointer', borderRadius: '12px', fontFamily: '微软雅黑'
        });

        $("#mb_btnbox").css({ margin: '15px 40px 15px', textAlign: 'center' });
        $("#mb_btn_ok,#mb_btn_no").css({float: 'unset', padding: 'unset', borderRadius: '5px', width: '85px', height: '30px' });


        //右上角关闭按钮hover样式
        $("#mb_ico").hover(function () {
            $(this).css({ backgroundColor: 'Red', color: 'White' });
        }, function () {
            $(this).css({ backgroundColor: '#DDD', color: 'black' });
        });

        var _widht = document.documentElement.clientWidth; //屏幕宽
        var _height = document.documentElement.clientHeight; //屏幕高

        var boxWidth = $("#mb_con").width();
        var boxHeight = $("#mb_con").height();

        //让提示框居中
        $("#mb_con").css({ top: (_height - boxHeight)/2 + "px", left: (_widht - boxWidth)/2 + "px" });
    }


})(jQuery);

/**
 * 该函数用来判断表单内的所有input项是否都有值
 */
function checkNotBlank() {
    var flag = true;
    $("form input").each(function () {
        if ($(this).val() == "") {
            flag = false;
            return false;
        }
    })
    return flag;
}