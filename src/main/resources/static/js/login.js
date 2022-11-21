//  输入框输入后的效果
const input = document.querySelectorAll("input");
input[0].addEventListener("change",function () {
        input[0].style.backgroundColor="rgba(231.62,243.78,255,1)";
});
//  输入错误的效果
const loginButton = document.querySelector('.enter-btn');
//  获取错误标签
const error = document.querySelector(".error");
//  获取用户姓名
const name = input[0].value;
//  获取密码
const password = input[1].value;
//  访问的url
const url = "http://localhost:8080/authenticate?name=" + name +"&password=" +password;
loginButton.addEventListener("click",function (e) {
    fetch(
        url,{
            method:"post"
        }
    )
        .then(function (response) {
            return response.json()
        })
        .then(function (myjson) {
            const result = myjson.result;
            if(result == "false"){
                error.style.display = "block";
            }
        })
})
