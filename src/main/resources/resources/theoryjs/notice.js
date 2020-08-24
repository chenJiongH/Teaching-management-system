$(function (){
	// page = 3
	showNews()
	$(".last").click(changeU);
	$(".next").click(changeU);
    // 设置全局变量，保证回退正确
    var whereComing = localStorage.getItem('backLocation');
    $("#selection p a:eq(0)").text("上一级");
    $("#selection p a:eq(0)").attr("href", whereComing);
})
function showNews(){
	ajax('get',path+'/lbnotice/page').then((data)=>{
		$(".title2").text(data.title);
		$(".info .author").text(data.author);
		$(".info .time").text(data.uptime);
		$(".content2").html(data.content);
		changeU();
	})
}
function changeU(){
	ajax('get',path+'/lbnotice/getUD').then((data)=>{
		$(".last span").text(data[0].title);
		$(".last span").attr("onclick", `clickUD(${data[0].id})`);
		$(".next span").text(data[1].title);
		$(".next span").attr("onclick", `clickUD(${data[1].id})`);
	})
}
function clickUD(id) {
	ajax('get',path+'/lbnotice/id',{"id":id}).then((data)=>{
		showNews();
		
	})
}

function pageAjax(){

}