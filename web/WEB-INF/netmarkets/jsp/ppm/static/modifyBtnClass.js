//统一变更按钮样式
$(function () {
    $.each($("button"),function(i,n){
        $(n).removeClass("btn")
            .removeClass("btn-default")
            .removeClass("btn-info")
            .removeClass("btn-primary")
            .addClass("x-btn-text")
            .addClass("blist");
    });
})