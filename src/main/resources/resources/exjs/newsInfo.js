$(function (){
    $.ajax({
        type: "get",
        url: path + "/news",
        xhrFields: {
            withCredentials: true,
        },
        success: function (data) {
            var headerInfo = "";
            if (data.category != "工科") {
                // 已经登录
                headerInfo = ".info";
            } else {
                // 未登录
                headerInfo = ".backMain";
            }
            // 区分有没有登录来查看的新闻
            $(headerInfo).removeAttr("style");
            // 开始显示新闻
            $(".newTitle").text(data.title);
            $("#date").text(data.uptime);
            $("#author").text(data.author);
            $("#count").text(data.viewnum);
            $(".detail").html(data.content);
            var waitTime = 0;
            if (data.content.length < 500) {
                waitTime = 50;
            } else if (data.content.length < 1000) {
                waitTime = 100;
            } else {
                waitTime = 300;
            }
            setTimeout(joinsBottomBar, waitTime);
        }
    });
})

function joinsBottomBar() {
    // 底部等新闻返回时再拼接，防止页面闪动
    var footer = "\n" +
        "\t<div class=\"footerbg\">\n" +
        "\t</div>\n" +
        "\t<div id=\"footer\">\n" +
        "\t\t<div><p>&copy;2015 福州大学地址：福建省福州市福州大学城乌龙江北大道2号邮编：350108传真：86-0591-22866099</p></div>\n" +
        "\t</div>";
    $("#container").after(footer);
    // 当container高度大等于 737px 时，footer不用设置头部外边距就处于了浏览器底部（被内容顶下去了）
    if (parseFloat($("#container").height()) < "737") {
        $("#footer").css({
            position: 'absolute',
            width: '100%',
            bottom: '0px'
        })
    }
}
function pageAjax() {

}