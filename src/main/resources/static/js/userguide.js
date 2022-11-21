
const flush = document.querySelector(".change");
flush.addEventListener("click",function () {
    window.location.reload();
})
//  增加选中变红的功能
//  获取每一个喜欢的按钮
const likes = document.querySelectorAll(".like");
//  获取每一个mask
const masks = document.querySelectorAll(".mask");
//  获取底下的五个头像
const imgs = document.querySelectorAll(".people img");
//  获得歌手的头像
const avatars = document.querySelectorAll(".avatar");
let index = 0;
//  用一个map来记录各个位置
const map = new Map();
//  得到五个头像的总标签
const people = document.querySelector(".people");
//  得到整个whole
const whole = document.querySelector(".whole");
const bigmask = document.querySelector(".bigmask");
for(let i = 0; i < likes.length; i++){
    likes[i].addEventListener('click',function () {
        if(likes[i].innerText == "喜欢"){
            masks[i].style.display = "block";
            likes[i].innerText = "已喜欢";
            imgs[index].src = avatars[i].src;
            map.set(i,index);
            index++;
            console.log(index);
            if(index == 5){
                whole.style.display = "none";
                bigmask.style.display = "block";
                bigmask.appendChild(people);
            }
        }else{
            masks[i].style.display = "none";
            likes[i].innerText = "喜欢";
            //  先找出该头像的位置
            const position = map.get(i);
            if (position == index-1){
                imgs[position].src ="img/people.png";
            }
            for(let j = position; j < index ; j++){
                imgs[j].src = imgs[j+1].src;
            }
            index--;
        }
    })
}