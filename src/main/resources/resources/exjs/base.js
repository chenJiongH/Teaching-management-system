var curpage = 1;
var curClickTr;
var tenTermOptions = "";
// var path = "http://47.92.68.218:8080";
var path = "";
$(function () {

    // 十年学期写死在页面上
    for (var i = 2020; i <= 2030; i++) {
        var year = i+"-"+(i+1)+"学年";
        tenTermOptions += "<option value=" + year + "第一学期>" + year + "第一学期</option>";
        tenTermOptions += "<option value=" + year + "第二学期>" + year + "第二学期</option>";
    }

    $(".t_word .name").css({
        width: '133px',
        display: 'inline-block'
    })

    userGet()
    // 点击页码，更新页码 和发送页码请求
    $(".sequence li").click(clickPage);
    $(".sequence li").click(pageAjax);

    // 固定表格头不滚动
    $(".table").scroll(function () {
        $("th").css({
            'top' : $(".table").scrollTop(),
            'background-color' : '#f9f4fd'
        });
    })

    // 禁用文本框的自动完成
    $(":input").prop('autocomplete', 'off');

    // 绑定每个页面的退出登录按钮
    $(".exitLogin").click(function () {
        $.ajax({
            type: "get",
            url: path + "/user/exit",
            success: function () {
                location.replace("main.html");
            }
        })
    });
    $(".person").click(function () {
        var url = window.location.pathname;
        if (url.indexOf("stuLoginNew.html") != -1 || url.indexOf("answerQuestion.html") != -1 || url.indexOf("stuSelf_editInfo.html") != -1) {
            location.replace("stuLoginNew.html");
        } else if (url.indexOf("teacherLogin.html") != -1 || url.indexOf("teacherMark.html") != -1 || url.indexOf("markDownLoad.html") != -1 || url.indexOf("teacherEditInfo.html") != -1) {
            location.replace("teacherLogin.html");
        } else if (url.indexOf("newsInfo.html") != -1) {
            location.replace("main.html");
        } else {
            location.replace("managerLogin.html");
        }
    });
    
});
function enter() {
        if (this.offsetWidth < this.scrollWidth) {
            $(this).attr('data-toggle', 'tooltip').attr('title', $(this).text());
        }
    }
function leave(){
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

    console.log(begin + " " + end);
    for (var i = begin; i <= end; i++) {
        if (i == curpage) {
            lis += '<li class="current">' + i + '</li>';
        } else
            lis +=  '<li>' + i + '</li>'
    }

    lis += '<li class="special">下一页</li>';

    $(".sequence").html(lis);

    $(".sequence li").click(clickPage);
    $(".sequence li").click(pageAjax);
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

function messageShow(data) {
    $("#message").text(data);
    $("#message").css('display', "block");
    setTimeout(function () {
        $("#message").css('display', "none");
    }, 3000);
}

function userGet() {
    $.ajax({
        type: "get",
        url: path + "/user/name",
        // url: "/user/name",
        xhrFields: {
            withCredentials: true,
        },
        success: function (data) {
            $(".person p").text(data);
            $(".t_word .name").text(data);
            $(".name").text(data);
        }
    });
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

        $("#mb_btnbox").css({ margin: '15px 70px 15px', textAlign: 'center' });
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