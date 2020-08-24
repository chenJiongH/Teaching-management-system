function scheduleUpload() {
    if ($(".fileP").text() === "") {
        messageShow("请选择文件");
        return;
    }
    var formData = new FormData($("form")[0]);
    $.ajax({
        type: "post",
        url: path + "/schedule",
        data: formData,
        contentType: false, // 使用form的 enctype
        processData: false, // 不对form表单的数据进行处理
        success: function (data) {
            if (data == "") {
                messageShow("请控制文件大小");
                return;
            }
            joins(data);
        }
    });
}

function joins(data) {
//用于标记是导入还是翻页，如果是导入的为false把记录数据插入到表格
    // 如果是翻页的话需要删除表格的数据替换的数据
    var flag=false;

    var list = [];
    // 判断data是不是数组类型，如果不是数组类型的就是导入，也就是插入到第一行
    // 如果是数组的话就将表格数据删除，进行翻页操作
    if (!(data instanceof Array)) {
        list.push(data);
        flag = true;
    }
    else {
        list=data; 
        $("table tr:gt(0)").remove(); 
    }

    for(var i = 0; i < list.length; i++) {
        var json = list[i];
        var tr = "<tr>";
        tr += "<td>" + json.sname + "</td>";
        tr += "<td>" + json.category + "</td>";
        tr += "<td>" + json.size + "</td>";
        tr += "<td>" + json.author + "</td>";
        tr += "<td>" + json.uptime + "</td>";
        tr += "<td id='" + json.id + "'>删除</td>";
        tr += "</tr>";
        if (flag) {
            // 在第一行插入数据
            $("table tr:eq(0)").after(tr);
        }
        else {
            $("table").append(tr);
        }  
      }
      $("table td").mouseenter(enter);
     $("table td").mouseleave(leave);
   
    // 点击单元行的最后一个删除操作单元格，发送 delete 请求，并删除页面点击行
    $("tr").each(function () {

        $(this).find('td').eq(5).click(function () {
            var curClickTd = $(this);
            var tr = $(this).parent();
            // 增加确认框
            zdconfirm('系统确认框','确定删除该进度表吗？',function(r){ //项目删除确定框
                if(r) {
                    $.ajax({
                        type: "delete",
                        url: path + "/schedule",
                        data: {"id" : curClickTd.attr("id")},
                        dataType: 'text',
                        success: function (data) {
                            tr.remove();
                        }
                    })
                }
            });

        })
    })

}

$(function () {

    pageAjax();

})

function pageAjax() {
    $.ajax({
        type: "get",
        url: path + "/schedule",
        data: {"curpage": curpage},
        success: function (data) {
            console.log(data);
            joins(data);
            // 点击行，然后把行数据挪动到上面或者清空
            if ($("table tr").eq(1).length == 0) {
                $("input").val("");
                $("option").eq(0).prop("selected", true);
            } else
                $("table tr").eq(1).click();
        }
    })
}
