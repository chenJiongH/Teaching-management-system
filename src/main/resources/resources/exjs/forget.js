$(function () {

    // 禁用文本框的自动完成
    $(":input").prop('autocomplete', 'off');


});
function pageAjax() {

}
function sendMessage() {
    $.ajax({
        type: "get",
        url: path + "/user/password",
        // url: "/user/password",
        data: $("form").serialize(),
        success: function (data) {

        }
    });
}