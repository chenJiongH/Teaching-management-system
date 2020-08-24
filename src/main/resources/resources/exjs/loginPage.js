$(function () {
    // 悬停变色
    $(".btn01 a").mouseenter(function () {
        $(this).css("background", "#9e2121");
        $(this).css("color", "#fff");
        // 此时图片的src：../images/导入.png；需要补充1即可
        var imgSrc = $(this).find("img").attr("src").substring(10);
        $(this).find("img").prop("src", "../images/1" + imgSrc);
    });
    $(".btn01 a").mouseleave(function () {
        $(this).css("background", "#eeeeee");
        $(this).css("color", "#9e2121");
        // 原来图片的src：../images/1导入.png；需要去掉1即可
        var imgSrc = $(this).find("img").attr("src").substring(11);
        $(this).find("img").prop("src", "../images/" + imgSrc);
    });
})

function pageAjax() {

}