var allData;
var oldpage = 1;
var newpage = 1;
var lbgrade ={};
var key = ["ll","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"];
var sno = {};
$(function () {

    $(".sequence li").unbind("click");
     ajax('get',path + "/lbterm").then((data)=>{
        var options = joinOptions(data);
        $("#term").html(options);
        $("#term").change();
    })
    $("#term").change(changerCname);
    $("#cname").change(changePname);
    $("#pname").change(changeProblem);
    $(".content .sequence li").click(showCurGrade);
    $("#mark .sequence li").click(clickPages);
})

function changerCname() {
    console.log(2);
    ajax('get', path + "/lbcname", {"term":$("#term").val()}).then((data)=>{
        var options = joinOptions(data);
        $("#cname").html(options);
        changePname();
    })
}
function changePname(){
    console.log(3);
    ajax('get', path + "/lbproject",{"term":$("#term").val(),"cname":$("#cname").val()}).then((data)=>{
        var options = joinOptionPname(data);
        $("#pname").html(options);
        changeProblem();
    });
}
function pageAjax() {
    console.log(14);
    ajax('get', path + "/lbgrade",
        {
            'curpage': curpage,
            'cname': $("#cname").val(),
            'term': $("#term").val(),
            'pname': $("#pname").val()
        }).then((data) => {
        joins(data);
        // 用来上传打印的数据
        allData = data;
        // 用来双击之后，定位该学生
        // sno = {"3178907204":{"anone":"niu"}, "3178907246":{}}  
        for (i = 0; i < data.length; i++){
            lbgrade[data[i].sno] = data[i];
        }

    });
}
function changeProblem(){
    console.log(15);
    ajax('get', path + "/lbproblem",
        {
            'page': page,
            'cname': $("#cname").val(),
            'term': $("#term").val(),
            'pname': $("#pname").val()
        }).then((data) => {
            lbproblem = data;
    });
    pageAjax();
}

// 改变页面的题目，并保存
// function changePage() {
//     oldpage = newpage;
//     $(".question span").text(newpage);
//     $(".question p").html(lbproblem[key[newpage]]);
//      // lbgrade.sno["an" + key[newpage]]
//      // lbgrade.sno[key[newpage]];
//     $(".sequence li").click(changePage);

// }
function post(){
    console.log(16);
    lbgrade[sno][key[newpage]] = $(".grade input").val();
    lbgrade[sno].score = 0;
    for(var j = 1; j <= lbproblem.number; j++){
        if (lbgrade[sno][key[j]] != null && lbgrade[sno][key[j]] != "") {
            lbgrade[sno].score = parseInt(lbgrade[sno].score) + parseInt(lbgrade[sno][key[j]]);
        }
    }
    
    ajax('put',path+'/lbgrade',JSON.stringify(lbgrade[sno]),1).then((data)=>{
        messageShow(data);
       pageAjax();
    })

    cancel();
}

function joins(data) {
    console.log(17);
    var tr = "";
     for (var i = (curpage - 1) * 20;  i < data.length && i < curpage * 20; i++) {
        var json = data[i];
        tr += "<tr>";
        tr += "<td>" + json.sno + "</td>";
        tr += "<td>" + json.name + "</td>";
        tr += "<td>" + json.cname + "</td>";
        tr += "<td>" + json.pname + "</td>";
        json.score = json.score == null?"":json.score;
        tr += "<td>" + json.score+ "</td>";
        tr += "</tr>";
    }
    $(".t_div table tr:gt(0)").remove();
    $(".t_div  table").append(tr);
    $(".t_div table td").mouseenter(enter);
    $(".t_div table td").mouseleave(leave);
    $(".t_div table tr").dblclick(show);
}


function cancel() {
    console.log(18);
    $("#mark .sequence li:eq(1)").text("1");
    $("#mark .sequence li:eq(1)").click();
    $("#curtain").css("display","none");
    $("#mark").css("display","none");
}
function show() {
    console.log(19);
    $("#curtain").css("display","block");
    $("#mark").css("display","block");
    // lbgrade ==> {"3178907204":{"anone":"niu"}, "3178907246":{}} 
    sno = $(this).find("td:eq(0)").text();
    // 页面刚刷新时，要点击一次第一页。或者是点击了同一页，则把
    curpage = newpage = 1;
    $(".grade input").val(lbgrade[sno][key[newpage]]);
    // $(".question span").text(newpage);
    $(".question p").html(lbproblem[key[newpage]]);
    $(".answer p").html( lbgrade[sno]["an" + key[newpage]]);
}
function get(){
    console.log(10);
     pageAjax();
}
function clickPages() {
    console.log("分数为：" + lbgrade[sno].one);
    console.log(111);
    oldpage = newpage;
    if ($(this).text() == "上一页") {
        if (page != 1)
            page = page - 1;
    } else if ($(this).text() == "下一页") {
        page = parseInt(page) + 1;
    } else
        page = $(this).text();
    newpage=page;

    if(lbproblem.number < newpage) {
        $(".question").css("display","none");
        $(".answer").css("display","none");
    }
    else {
        $(".question").css("display","block");
        $(".answer").css("display","block");
        // $(".question span").text(newpage);
        $(".question p").html(lbproblem[key[newpage]]);
        $(".answer p").html( lbgrade[sno]["an" + key[newpage]]);
    }
    lbgrade[sno][key[oldpage]] = $(".grade input").val();
    lbgrade[sno][key[newpage]] = lbgrade[sno][key[newpage]] == null?"":lbgrade[sno][key[newpage]];

    $(".grade input").val(lbgrade[sno][key[newpage]]);
    var lis = "";
    var begin,
        end;
    if (page == 1) {
        lis += '<li class="special fontColor">上一页</li>';
        begin = 1;
        end = 10;
    } else if (page < 10 && page > 1) {
        lis += '<li class="special">上一页</li>';
        begin = 1;
        end = 10;
    } else {
        lis += '<li class="special">上一页</li>';
        begin = page - 8;
        end = parseInt(page) + 1;
    }

    for (var i = begin; i <= end; i++) {

        if (i == page) {
            lis += '<li class="current">' + i + '</li>';
        } else
            lis += '<li>' + i + '</li>'
    }

    lis += '<li class="special">下一页</li>';

    $("#mark .sequence").html(lis);

    $("#mark .sequence li").click(clickPages);
    console.log("分数为：" + lbgrade[sno].one);

}



 function printtable() {
    $.ajax({
        type: "post",
        url: path + "/lbgrade/makeFile",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(allData),
        success: function (data) {
            console.log(data);
            $("input[name=fileDest]").val(data);
            $("#gradeExport").submit();
        }
    })
    var fileDest = $(this).attr("name");
    $("input[name=fileDest]").val(fileDest);
    $("input[name=fileName]").val(fileDest.substring(fileDest.lastIndexOf('\\') + 1));
    $("#scheduleExport").submit();
}
function showCurGrade() {
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

    $(".content .sequence").html(lis);
    $(".content .sequence li").click(showCurGrade);

    joins(allData);

}