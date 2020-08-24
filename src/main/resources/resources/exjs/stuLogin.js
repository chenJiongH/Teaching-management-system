$(function() {

	schedulePageAjax();
	$(".change label").click(clickSchedulePage);
    $(".change label").click(schedulePageAjax);

    $(".downLoad td").click(schedulePageAjax);

})

var scheduleClickPage = 1;

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
            $(".downLoad table span").click(clickSchedule);
        }
    })
}
// 拼接进度表列表
function scheduleJoins(data) {
    var schedules = "";
    for (var i = 0; i < data.length; i += 3){
        schedules += "<tr>";
        schedules += "<td><img src='../images/下载-(12).png'>";
        schedules += "<span name='" + data[i].path + "'>" + data[i].sname + "</span></td>";
        schedules += "<td>" + data[i].uptime.substring(data[i].uptime.indexOf('-') + 1) + "</td>";
        if (i+1 <data.length) {
            schedules += "<td><img src='../images/下载-(12).png'>";
            schedules += "<span name='" + data[i+1].path + "'>" + data[i+1].sname + "</span></td>";
            schedules += "<td>" + data[i+1].uptime.substring(data[i+1].uptime.indexOf('-') + 1) + "</td>";
        }
        if (i+2 <data.length) {
            schedules += "<td><img src='../images/下载-(12).png'>";
            schedules += "<span name='" + data[i+2].path + "'>" + data[i+2].sname + "</span></td>";
            schedules += "<td>" + data[i+2].uptime.substring(data[i+2].uptime.indexOf('-') + 1) + "</td>";
        }
        schedules += "</tr>";
    }
    $(".downLoad table").html(schedules);
    $("table td").mouseenter(enter);
     $("table td").mouseleave(leave);
}
// 发送异步请求，下载进度表
function clickSchedule() {
    var fileDest = $(this).attr("name");
    $("input[name=fileDest]").val(fileDest);
    $("input[name=fileName]").val(fileDest.substring(fileDest.lastIndexOf('\\') + 1));

    $("#scheduleExport").submit();
}

function pageAjax() {

}