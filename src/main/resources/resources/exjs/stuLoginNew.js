$(function () {
    // 页面刚解析成功，请求视频、新闻、进度表三类数据，并显示在页面
    videoGet();
    newsPageAjax();
    schedulePageAjax();
    // 1、绑定新闻页码点击事件
    $(".newsPage li").click(clickNewsPage);
    $(".newsPage li").click(newsPageAjax);
    // 2、绑定进度表页码点击事件
    $(".change label").click(clickSchedulePage);
    $(".newsPage li").click(schedulePageAjax);
    // 不同角色登录的表单样式切换
    $(".tab_li>li").click(function () {
        $("#tab li").css("backgroundColor", "#4e2d68");
        $(this).css("backgroundColor", "#9e2121");
        var type = $(this).text();
        var inputType = $("input[name=type]");
        inputType.val(type);

        var palceholder = "请输入手机号",
            href = "teacherForgetPassword.html";
        if ("学生" == type) {
            palceholder = "请输入学号";
            href = "stuForgetPassword.html";
        }
        $("input[name=username]").prop("placeholder", palceholder);
        $("form a").prop("href", href);
    })
})


// 刷新页面获取视频列表，调用swiper插件，绑定点击事件
function videoGet() {
    $.ajax({
        type: "get",
        url: path + "/video",
        data: {"category": "理科"},
        success: function (data) {
            // 让主视频播放视频列表的第一个视频
            if (data[0] != undefined) {
                $("#video1").prop('src', data[0].path);
                // 拼接视频字符串，然后再使用swiper插件生成视频列表
                videoJoins(data);
            }
            var mySwiper = new Swiper('.swiper-container', {
                direction: 'vertical', // 垂直切换选项
                mousewheel: true,
                freeMode: true,
                slidesPerView: 4,
                // 如果需要滚动条
                scrollbar: {
                    el: '.swiper-scrollbar',
                    draggable: true,
                },
            })
            mySwiper.scrollbar.$el.css({
                'width': '8px',
                'cursor': 'pointer'
            });
            mySwiper.scrollbar.$dragEl.css('background', 'rgb(158, 33, 33)');
            $(".swiper-wrapper .swiper-slide").css("cursor", "pointer");

            // 点击视频列表后，在视频播放窗口播放视频，即替换src属性值
            $(".swiper-wrapper .swiper-slide").click(function () {
                var playSrc = $(this).find("video").prop("src");
                $("#video1").prop("src", playSrc);
            });

            // 鼠标进入新闻，变红色。离开新闻回复颜色
            $(".swiper-slide").mouseenter(function () {
                $(this).find("div").find("p").css("color", "#cd4849");
                $(this).find("div").find("span").css("color", "#cd4849");
            });
            $(".swiper-slide").mouseleave(function () {
                $(this).find("div").find("p").css("color", "#555555");
                $(this).find("div").find("span").css("color", "#e92121");
            });
        }
    })
}

/**
 * 拼接视频json 成html代码
 * @param data 视频json
 */
function videoJoins(data) {
    var json,
        videos = "";
    for (var i = 0; i < data.length; i++) {
        json = data[i];
        videos += '<div class="swiper-slide">';
        videos += '<video src="' + json.path + '">您的浏览器不支持播放该视频。</video>';
        videos += '<div><p>' + json.summary + '</p><span>发布时间：' + json.uptime + '</span></div>';
        videos += '</div>';
    }
    $(".swiper-wrapper").html(videos);
}

/**
 * 点击新闻页码，改变curpage变量，和页码的html代码
 */
function clickNewsPage() {
    curpage = $(this).attr('name');
    var lis = "";
    var begin,
        end;
    if (curpage >= 1 && curpage < 10) {
        begin = 1;
        end = 10;
    } else {
        begin = curpage - 8;
        end = parseInt(curpage) + 1;
    }

    for (var i = begin; i <= end; i++) {
        if (i == curpage) {
            lis += '<li class="current" name="' + i + '"></li>';
        } else
            lis += '<li name="' + i + '"></li>'
    }
    $(".newsPage").html(lis);
    // 绑定新闻页码点击事件
    $(".newsPage li").click(clickNewsPage);
    $(".newsPage li").click(newsPageAjax);
}

// 页码请求新闻
function newsPageAjax() {
    $.ajax({
        type: "get",
        url: path + "/news/page",
        data: {
            "curpage": curpage,
            "type": "学生页面"
        },
        success: function (data) {
            newsJoins(data);
            // 跳转新闻页面
            $(".newsList li").click(clickNews);
        }
    })
}

/**
 * 给每一个新闻 div 绑定点击事件。点击则发送新闻id，等待回调成功则跳转到新闻页面
 */
function clickNews() {
    var id = $(this).attr('name');
    $.ajax({
        type: "get",
        url: path + "/news/id",
        data: {"id": id},
        xhrFields: {
            withCredentials: true,
        },
        success: function (data) {
            window.location.href = 'newsInfo.html';
        }
    })
}

/**
 * 拼接新闻json 成html代码
 * @param data 视频json
 */
function newsJoins(data) {
    var news = "";
    for (var i = 0; i < data.length; i++) {
        news += '<li name="' + data[i].id + '"><div class="date">';
        news += '<div class="d_top">' + data[i].uptime.substring(data[i].uptime.lastIndexOf('-') + 1) + '</div>';
        news += '<div class="d_bottom">' + data[i].uptime.substring(0, data[i].uptime.lastIndexOf('-')) + '</div>';
        news += '</div>';
        news += '<div class="newsContent">';
        news += '<p>' + data[i].title + '</p>';
        news += '<span>' + data[i].summary + '</span>';
        news += '</div></li>';
    }
    $('.newsList').html(news);
    // 鼠标进入新闻，变红色。离开新闻回复颜色
    $(".newsList li").mouseenter(function () {
        $(this).find("p").css("color", "#cd4849");
        $(this).find("span").css("color", "#cd4849");
    });
    $(".newsList li").mouseleave(function () {
        $(this).find("p").css("color", "#555555");
        $(this).find("span").css("color", "#999999");
    });
}

// 定义一个进度表的点击变量。和新闻的点击页码变量区分开来
var scheduleClickPage = 1;

/**
 * 点击新闻页码，改变curpage变量，和页码的html代码
 */
function clickSchedulePage() {
    scheduleClickPage = $(this).attr('name');
    var lis = "";
    var begin,
        end;
    if (scheduleClickPage == 1) {
        begin = 1;
        end = 2;
    } else {
        begin = scheduleClickPage - 1;
        end = parseInt(scheduleClickPage) + 1;
    }

    lis += '<label name="' + begin + '"><</label> ';
    lis += '<label name="' + end + '">></label>';
    console.log(lis);
    $(".change").html(lis);
    // 绑定进度表页码点击事件
    $(".change label").click(clickSchedulePage);
    $(".change label").click(schedulePageAjax);
}

/**
 * 获取进度表列表
 */
function schedulePageAjax() {

    $.ajax({
        type: "get",
        url: path + "/schedule",
        data: {
            "curpage": scheduleClickPage,
            "category": "理科"
        },
        success: function (data) {
            scheduleJoins(data);
            // 点击下载进度表，函数内部使用表单来发送请求
            $(".right table span").click(clickSchedule);
        }
    })
}

// 拼接进度表列表
function scheduleJoins(data) {
    var schedules = "";
    for (var i = 0; i < data.length; i++) {
        schedules += "<tr>";
        schedules += "<td><img src='../images/下载-(12).png'>";
        schedules += "<span name='" + data[i].path + "'>" + data[i].sname + "</span></td>";
        schedules += "<td>" + data[i].uptime.substring(data[i].uptime.indexOf('-') + 1) + "</td>";
        schedules += "</tr>";
    }
    $(".right table").html(schedules);
    $("table td").mouseenter(enter);
    $("table td").mouseleave(leave);

    // 鼠标进入新闻，变红色。离开新闻回复颜色
    $("table tr").mouseenter(function () {
        $(this).css("color", "#cd4849");
    });
    $("table tr").mouseleave(function () {
        $(this).css("color", "#555");
    });
}

// 发送异步请求，下载进度表
function clickSchedule() {
    var fileDest = $(this).attr("name");
    $("input[name=fileDest]").val(fileDest);
    $("input[name=fileName]").val(fileDest.substring(fileDest.lastIndexOf('\\') + 1));
    $("#scheduleExport").submit();
}

// 为了不报异常
function pageAjax() {

}