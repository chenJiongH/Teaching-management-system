$(function (){
	// page = 3
	showVideo()
	$(".last").click(changeU);
	$(".next").click(changeU);
    // 设置全局变量，保证回退正确
	var whereComing = localStorage.getItem('backLocation');
    $("#selection p a:eq(0)").text("上一级");
	$("#selection p a:eq(0)").attr("href", whereComing);
})
function showVideo(){
	ajax('get',path+'/lbvideo/page').then((data)=>{
		$(".title2").text(data.vname);
		$(".info .author").text(data.author);
		$(".info .time").text(data.uptime);
		$("video").prop("src",data.path);
		changeU();
	})
}
function changeU(){
	ajax('get',path+'/lbvideo/getUD').then((data)=>{
		$(".last span").text(data[0].summary);
		$(".last span").attr("onclick", `clickUD(${data[0].id})`);
		$(".next span").text(data[1].summary);
		$(".next span").attr("onclick", `clickUD(${data[1].id})`);
	})
}
function clickUD(id) {
	ajax('get',path+'/lbvideo/id',{"id":id}).then((data)=>{
		showVideo();
		
	})
}

function pageAjax(){

}