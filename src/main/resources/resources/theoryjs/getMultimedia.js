// 用来存储scheduleCount有多少页
var scheduleCount;
$(function () {
    $(".left_top .turn li:eq(0)").attr("style", "color: #eee6e6;");
    //该方法执行获取视频、进度表、通知
    // 课程资料翻页
    $(".left_top li").click(changeSchedule);
    // 通知翻页
    $(".left_bo li").click(changeNotice);
})

// 在学期课程都有了以后调用这个函数
function getFile() {
    // 分别发送三个多媒体文件获取请求
    try {
        getVideo();
    } catch (e) {
        e.message;
    }
    getSchedule();
    getNotice();
}

function getVideo() {
    $("input[name=pageNumber]").val(100);
    ajax("get", path + "/lbvideo", $(".video_f").serialize())
        .then((data) => {
            var tr = "";
            for (var i = 0; i < data.length; i++) {
                tr += `
                    <div onclick="seeThis(${data[i].id}\, 'v')" onmouseenter="videoEnter(this)" onmouseleave="videoLeave(this)"" class="swiper-slide swiper-slide-active" style="height: 72.5px; cursor: pointer;">
                        <video src="${data[i].path}"></video>
                        <p>${data[i].summary}</p>
                        <span>发布时间：${data[i].uptime}</span>
                    </div>
                        `
            }
            $(".swiper-wrapper").html(tr);
            videoSwiper();
        })
}

function getSchedule() {
    $("input[name=pageNumber]").val(4);
    ajax("get", path + "/lbschedule", $(".video_f").serialize())
        .then((data) => {
            var li = "";
            for (var i = 0; i < data.length; i++) {
                data[i].path = data[i].path.replace(/\\/g, "/");
                li += `<li onclick="seeThis(${data[i].id}\, 's'\, '${data[i].path}')" onmouseenter="enter(this)" onmouseleave="leave(this)"><p>${data[i].sname}</p></li>`;
            }
            $(".left_top .contents").html(li);
            getScheduleCount();
        })
}
function getScheduleCount() {
    // 计算该课程下的资料有多少页，传过去的每页个数用于除法计算
    var url = location.pathname;
    if (url.indexOf("main.html") != -1) {
        $("input[name=pageNumber]").val(10);
    } else {
        $("input[name=pageNumber]").val(7);
    }
    ajax("get", path + "/lbschedule/count", $(".video_f").serialize())
        .then((data) => {
            scheduleCount = data;
            // 判断现在是不是最后一页
            if ($(".left_top .turn li:eq(1)").attr("name") > scheduleCount) {
                $(".left_top .turn li:eq(1)").attr("style", "color: #eee6e6;");
            }
        })
}

function getNotice() {
    $("input[name=pageNumber]").val(4);
    ajax("get", path + "/lbnotice", $(".video_f").serialize())
        .then((data) => {
            var li = "";
            for (var i = 0; i < data.length; i++) {
                li += `<li onclick="seeThis(${data[i].id}, 'n')" onmouseenter="enter(this)" onmouseleave="leave(this)">
                            <p>${data[i].title}</p>
                            <p>${data[i].summary}</p>
                       </li>`;
            }
            $(".left_bo .contents").html(li);
        })
}

// 点击翻页之后做的操作
function changeSchedule() {
    if ($(this).attr("name") > scheduleCount) {
        return;
    }
    // 判断点击的是左边的页码还是右边的页码。点击左边页码不为1的话，后退一步。点击右边页码前进一步
    if ($(this).attr("dir") == "r" && $(this).attr("name") <= scheduleCount) {
        // 把点击项的页码放到curpage表单项中
        $("input[name=curpage]").val($(this).attr("name"));
        $(this).prev().attr("name", $(this).attr("name"));
        $(this).attr("name", parseInt($(this).attr("name")) + 1);
        // 清除上一页的虚色，判断自己是否需要虚色（最后一页）
        $(this).prev().removeAttr("style");
        if ($(this).attr("name") > scheduleCount) {
            $(this).attr("style", "color: #eee6e6;");
        } else {
            $(this).removeAttr("style");
        }
    } else if ($(this).attr("name") != 1) {
        // 把点击项的页码放到curpage表单项中
        $("input[name=curpage]").val($(this).attr("name") - 1);
        $(this).next().attr("name", $(this).attr("name"));
        $(this).attr("name", parseInt($(this).attr("name")) - 1);

        // 第一页箭头虚色
        if ($(this).attr("name") == 1) {
            $(this).attr("style", "color: #eee6e6;");
        } else {
            $(this).removeAttr("style");
        }
        // 后一页箭头实色
        if ($(this).next().attr("name") <= scheduleCount) {
            $(this).next().removeAttr("style");
        }
    }
    getSchedule();
}

function changeNotice() {

    $(".left_bo .turn li").removeAttr("class");
    $(this).attr("class", "current");
    // 判断点击的是左边的页码还是右边的页码。点击左边页码不变，点击右边页码前进一步
    if ($(this).attr("dir") == "r") {
        // 把点击项的页码放到curpage表单项中
        $("input[name=curpage]").val($(this).attr("name"));
        $(this).prev().attr("name", $(this).attr("name"));
        $(this).attr("name", parseInt($(this).attr("name")) + 1);
    } else if ($(this).attr("name") != 1) {
        $("input[name=curpage]").val($(this).attr("name") - 1);
        $(this).next().attr("name", $(this).attr("name"));
        $(this).attr("name", parseInt($(this).attr("name")) - 1);
    }
    getNotice();
}

function seeThis(id, type, fileDest) {
    // 设置各个资源的返回主页页面的。学生主页或者登录页面
    var url = window.location.pathname;
    if (url.indexOf("main.html") != -1) {
        localStorage.setItem('backLocation', 'main.html');
    } else if (url.indexOf("stuAnswerQuestion.html") != -1) {
        localStorage.setItem('backLocation', 'stuAnswerQuestion.html');
    } else if (url.indexOf("stuQueryGrade.html") != -1) {
        localStorage.setItem('backLocation', 'stuQueryGrade.html');
    }
    // 资料如果不是excel、ppt、word、pdf，则可以下载，否则跳转浏览页面
    if (type == "s") {
        // alert(fileDest);
        var fileSuffix = fileDest.substring(fileDest.lastIndexOf(".") + 1).toLowerCase();
        var pptExcelPdfWordSuffix = `pptx,pptm,ppt,potx,potm,pot,ppsx,ppsm,ppsm,ppam,ppa,xml,pptx,odp, xls,xlsx, pdf, docx,doc`;
        // 该文件不是可以浏览的四种文件之一
        if (pptExcelPdfWordSuffix.indexOf(fileSuffix) == -1) {
            clickSchedule(fileDest);
            return ;
        }
    }
    var url = "";
    if (type == "s") {
        url = "schedule";
    } else if (type == "n") {
        url = "notice";
    } else if (type == "v") {
        url = "video";
    } else if (type == "ne") {
        url = "news";
    }

    ajax("get", `${path}/lb${url}/id`, {"id": id}).then((data) => {
        if (data == "绑定成功") {
            location.replace(`${url}.html`);
        }
    });

}

function videoEnter(e) {
    $(e).find("p").attr("style", "color:#d61e1e");
}

function videoLeave(e) {
    $(e).find("p").removeAttr("style");
}

function enter(e) {
    $(e).find("p").attr("style", "color:#d61e1e;cursor:pointer");
    $(e).find("label").attr("style", "color:#d61e1e;cursor:pointer");
    $(e).find("td").attr("style", "color:#d61e1e;cursor:pointer");
}

function leave(e) {
    $(e).find("p").removeAttr("style");
    $(e).find("label").removeAttr("style");
    $(e).find("td").removeAttr("style");
}

// 发送异步请求，下载进度表
function clickSchedule(fileDest) {
    console.log(fileDest);
    $("input[name=fileDest]").val(fileDest);
    $("input[name=fileName]").val(fileDest.substring(fileDest.lastIndexOf('\\') + 1));
    $("#scheduleExport").submit();
}