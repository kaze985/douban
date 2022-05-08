let audio=document.querySelector("audio");
let songName=document.querySelector(".songName");
function next(url,name){
    audio.src=url;
    songName.innerHTML=`当前歌曲：${name}`;
}