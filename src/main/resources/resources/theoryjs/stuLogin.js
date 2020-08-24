$(function () {
    pageAjax();
})
function pageAjax() {
    ajax("get", path + "/lbstu/course")
        .then((data) => {
            joins(data);
        })
}

function joins(data) {
    var tr = "";
    for (var i = 0; i < data.length; i++) {
        var json = data[i];
        tr += "<div class=\"one\"> " +
            "     <div class=\"one1\"> " +
            "      <div class=\"top1\"> " +
            "       <p> " +
            "        <span>课程：</span> " +
            "        <span>"+ json.cname +"</span> " +
            "       </p> " +
            "       <p> " +
            "        <span>学期：</span> " +
            "        <span>"+ json.term +"</span> " +
            "       </p> " +
            "      </div> " +
            "      <div class=\"bottom1\"> " +
            "       <button onclick='start(this)' type=\"button\" class=\"btn\">课程预习</button> " +
            "       <span></span> " +
            "       <button onclick='find(this)' type=\"button\" class=\"btn purpleBtn\">查询成绩</button> " +
            "      </div> " +
            "     </div> " +
            "    </div>"
    }
    $(".four_div").append(tr);
    $(".sideBar").css("height", $("#container").css("height"));
}

function start(e) {
    // 获取方框内的学期、课程
    var term = $(e).parent().prev().find("p:eq(1)").find("span:eq(1)").text();
    var cname = $(e).parent().prev().find("p:eq(0)").find("span:eq(1)").text();
    ajax("post", path + "/lbstu/start", {"cname": cname, "term": term})
        .then((data) => {
            location.replace("stuAnswerQuestion.html")
        })
}

function find(e) {
    var term = $(e).parent().prev().find("p:eq(1)").find("span:eq(1)").text();
    var cname = $(e).parent().prev().find("p:eq(0)").find("span:eq(1)").text();
    ajax("post", path + "/lbstu/start", {"cname": cname, "term": term})
        .then((data) => {
            location.replace("stuQueryGrade.html")
        })
}