
//  选中ul标签
const content = document.querySelector(".content")
//  选中input标签
const input = document.querySelector("input")
//  获取keyword的值
const keyword = input.value;
//  访问的url
const url = 'http://localhost:8080/searchContent?keyword=' + keyword;
//  利用input的回车提交事件
input.addEventListener("keydown",function (e) {
    if (e.keyCode === 13) {
        fetch(
            url,{
                method:"get"
            }
        )
            .then(function (response) {
                return response.json()
            })
            .then(function (myjson) {
                const songs = myjson.songs;
                for (let i = 0 ; i < songs.length; i++) {
                    //  创建li标签用于存储每一个模块
                    const li = document.createElement('li');
                    li.innerHTML = `<img src="${songs[i].cover}"><div>${songs[i].name}</div>`;
                    content.append(li);
                    if(i == 4){
                        break;
                    }
                }
            })
    }
})


