$(function () {
    $(".changeNews li").css("cursor", "pointer");
    allFile();
    $(".changeNews li").click(changeNews);
    $(".left_bo .change").click(changeNotice);
})

function allFile() {
    getFirstNews();
    getNews();
    getVideo();
    getNotice()
    getView();
    getSchedule();
}

// 登录板块
function get() {
    if ($("input[name=username]").val() == "" || $("input[name=password]").val() == "") {
        messageShow("请输入用户名和密码！");
        return;
    }
    ajax('get', path + '/user/lblogin', $(".loginCo").serialize()).then((data) => {
        if (data != "账号或密码错误！") {
            if (data == "学生") {
                location.replace("stuLogin.html");
            }
            else if (data == "教师") {
                location.replace("teacherWelcome.html");
            }
            else if (data == "管理员") {
                location.replace("mwelcome.html");
            }
        } else {
            messageShow(data);
        }
    })
}

// 新闻板块
function getNews() {
    ajax('get', path + '/lbnews',
        {
            "curpage": curpage,
            "pageNumber": 4
        }).then((data) => {
        joinNews(data);
    })

}

function joinNews(data) {
    var lis = "";
    var json;
    for (var i = 0; i < data.length; i++) {
        json = data[i];
        lis += '<li onclick="seeThis(' + json.id + '\, \'ne\')"  onmouseenter="enter(this)" onmouseleave="leave(this)"><p>' + json.summary + '</p><span>' + json.uptime.substring(5) + '</span></li>';
    }
    $(".list li").remove();
    $(".list").append(lis);
    $(".list li p").mouseenter(enter);
    $(".list li p").mouseleave(leave);

}

function changeNews() {
    $(".changeNews li").removeAttr("class");
    $(this).attr("class", "current");
    curpage = $(this).attr("name");
    getNews();
}

function getFirstNews() {
    ajax('get', path + '/lbnews/first').then((data) => {
        $(".conts .newsT").text(data.title);
        $(".conts .newsC").html(data.summary +"...<span style='color: #952929;'>[详情]</span>");
        $(".conts .newsTime").text(data.uptime);
        $(".news .top2").attr("onclick", "seeThis(" + data.id + ", 'ne')");
        // 查找里面的第一个图片
        if (data.content != null) {
            var end;
            var start = data.content.indexOf("src=\"");
            for (var i = start + 5; i < data.content.length; i++) {
                if (data.content[i] == "\"") {
                    end = i;
                    break;
                }
            }
            var imgUrl = data.content.substring(start + 5, end);
            $(".pic img").attr("src", imgUrl);
        }
    })
}

// 视频列表
function getVideo() {
    $("input[name=pageNumber]").val(100);
    ajax("get", path + "/lbvideo", $(".video_f").serialize())
        .then((data) => {
            var tr = "";
            for (var i = 0; i < data.length; i++) {
                tr += `
                    <div onclick="seeThis(${data[i].id}\, 'v')" onmouseenter="videoEnter(this)" onmouseleave="videoLeave(this)"" class="swiper-slide swiper-slide-active" style="height: 72.5px; cursor: pointer;">
                        <div><video src="${data[i].path}"></video>
                        <p>${data[i].summary}</p>
                        <span>发布时间：${data[i].uptime}</span></div>
                    </div>
                        `
            }
            $(".left_c .swiper-wrapper").html(tr);
            videoSwiper();
        })
}


// 通知板块
function getNotice() {
    $("input[name=pageNumber]").val(5);
    ajax("get", path + "/lbnotice", $(".video_f").serialize())
        .then((data) => {
            var li = "";
            for (var i = 0; i < data.length; i++) {
                li += `<li onclick="seeThis(${data[i].id}, 'n')" onmouseenter="enter(this)" onmouseleave="leave(this)">
                            <div class="date">
                               <div class="day">${data[i].uptime.substring(8)}</div>
                               <div class="ym">${data[i].uptime.substring(0, 7)}</div>
                            </div>
                            <div class="desc">
                              <p>${data[i].title}</p>
                              <label>${data[i].summary}</label>
                            </div>
                       </li>`;
            }
            $(".left_bo .contents").html(li);
        })
}


// 校园风景
function getView() {
    $("input[name=pageNumber]").val(100);
    ajax('get', path + '/lbview', $(".video_f").serialize()).then((data) => {

        var slide = "";
        for (var i = 0; i < data.length; i++) {
            slide += `<div class="swiper-slide">
                    <img src="${data[i].path}" >
                </div>`;
        }
        $(".view .swiper-wrapper").html(slide);
        var swiper = new Swiper('.view .swiper-container', {
            autoplay:true,
            autoplay: {
                disableOnInteraction: false,
            },
            pagination: {
                el: '.swiper-pagination',
                clickable :true,
            },
        });
        $(".view .swiper-wrapper img").css({"width": "100%", "height": "100%"});
        $(".swiper-pagination span:last").css('margin-right', '15px');
    })

}

// 下载列表
function getSchedule() {
    $("input[name=pageNumber]").val(10);
    ajax("get", path + "/lbschedule", $(".video_f").serialize())
        .then((data) => {
            var tr = "";
            for (var i = 0; i < data.length; i++) {
                data[i].path = data[i].path.replace(/\\/g, "/");
                console.log(data[i].path);
                tr += `<tr onclick="seeThis(${data[i].id}\, 's'\, '${data[i].path}')" onmouseenter="enter(this)" onmouseleave="leave(this)">
                <td>${data[i].sname}</td>
                <td>${data[i].uptime.substring(6)}</td>
                </tr>`;
            }
            $(".left_top .t_div table").html(tr);
            getScheduleCount();
        })
}

// 防止报异常
function pageAjax() {

}
