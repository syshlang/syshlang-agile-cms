<!DOCTYPE html>
<html>
<head>
	<title>文档管理系统</title>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <link rel="stylesheet" href="${uiPath}/css/layui.css">
    <script type="text/javascript"  src="${uiPath}/layui.js"></script>
    <link rel="stylesheet" type="text/css" href="/app/layui/login/css/login.css">
    <script type="text/javascript" src="/app/layui/login/js/login.js"></script>
    <script>
        if (window != window.top) top.location.href = self.location.href;
    </script>
</head>
<body>
    <canvas id="canvas"></canvas>
    <div id="container">
        <div class="admin-login-background">
            <form class="layui-form">
                <div class="admin-header">
                    <h2>登录</h2>
                </div>
                <div>
                    <i class="layui-icon layui-icon-username admin-icon admin-icon-username"></i>
                    <input type="text" id="username" name="username" placeholder="请输入用户名"
                           autocomplete="off"
                           class="layui-input admin-input admin-input-username">
                </div>
                <div>
                    <i class="layui-icon layui-icon-password admin-icon admin-icon-password"></i>
                    <input type="password" id="password" name="password"
                           placeholder="请输入密码"
                           autocomplete="off"
                           class="layui-input admin-input">
                </div>
                <div >
                    <input type="text" id="verify" name="verify"
                           placeholder="请输入验证码"
                           autocomplete="off"
                           class="layui-input admin-input admin-input-verify">
                    <img class="admin-captcha" src="" onclick="updateVerify()">
                </div>
                <div>
                    <i class="layui-icon layui-icon-rememberMe admin-icon admin-icon-rememberMe"></i>
                    <input type="checkbox" checked="" id="rememberMe" name="rememberMe" lay-skin="switch" lay-text="是|否">
                    <label class="layui-form-label admin-rememberMe">是否记住</label>
                </div>
                <button type="button" class="layui-btn admin-button" lay-submit="" onClick="loginAction()">登陆</button>
                <div id="msg" style="margin-top:5px;color:red;display:none;"></div>
            </form>
        </div>
    </div>
</body>
<script type="text/javascript" src="/app/layui/login/js/canvas_star.js"></script>
<script>
    layui.use('form', function(){
        var form = layui.form;
        $ = layui.jquery;
        form.render();
    });
</script>
</html>
