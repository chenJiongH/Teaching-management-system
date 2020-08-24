var imgsStr;
var imgsArr = [];
$(function (){
    showNews();
    // 设置全局变量，保证回退正确
    var whereComing = localStorage.getItem('backLocation');
    $("#selection p a:eq(0)").text("上一级");
    $("#selection p a:eq(0)").attr("href", whereComing);

    $(".index1").keypress(function(event){
        if(event.which === 13) {
            //点击回车要执行的事件
            curpage = $(".index1").val();
            $(".conts").html(`<img src="${imgsArr[curpage - 1]}" />`);
        }
    })
})
function showNews(){
	ajax('get',path+'/lbschedule/page').then((data)=>{
		$(".title2").text(data.sname);
		$(".info .author").text(data.author);
		$(".info .time").text(data.uptime);
		$(".content2").html(data.content);
        imgsStr = data.size;
        imgsArr = imgsStr.split(",");
        // 因为后端是每个都拼接，号，所以最后一个是没有用的。strB.append(imgslocation.get(i).substring(startIndex) + ",");
        $(".change span").text(imgsArr.length - 1);
        changeDir('u')
	})
}
function changeDir(dir){
    if (dir == 'u') {
        if (curpage != 1) {
            curpage --;
        }
    } else {
        if (curpage != $(".change span").text()) {
            curpage ++;
        }
    }
    $(".index1").val(curpage);
    $(".conts").html(`<img src="${imgsArr[curpage - 1]}" />`);
}

function pageAjax(){

}
function videoSwiper() {
    var mySwiper = new Swiper('.left_c .swiper-container', {
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
}