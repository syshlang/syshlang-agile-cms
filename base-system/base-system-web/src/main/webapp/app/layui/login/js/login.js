layui.use(['form', 'layer'], function() {
    $ = layui.jquery;
    var layer = layui.layer,
        form = layui.form;

    //自定义验证规则
    form.verify({
        username: function (value, item) {
            if (value.length == 0) {
                return '用户名不能为空！';
            }
            if(!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)){
                return '用户名不能有特殊字符';
            }
        },
        password: [/(.+){6,12}$/, '密码必须6到12位']
    });

    //监听提交
    form.on('submit(login)', function (data) {
        var username = $('#username').val();
        var password = $('#password').val();
        var rememberMe = $('#rememberMe').val();
        var params = {username:username,password:password,rememberMe:rememberMe};
        var loading = layer.load(1);
        $.post("/system/user/login.json",params,function(data,status){
            if(data.code == 10000){
                window.location.href="/system/home/index.html";
            }else{
                //alert('提示',data.desc);
                $("#msg").css('display','block');
                $('#msg').html(data.desc);
            }
            layer.close(loading);
        });
        return false;
    });
});