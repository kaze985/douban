let search=document.querySelector(".search input");
let ul=document.querySelector(".searchContent ul");
search.addEventListener("input",getContent);
function getContent(){
    fetch(
        `/searchContent?keyword=${search.value}`
    )
        .then(function(response) {
            return response.json();
        })
        .then(function(result) {
            ul.innerHTML=``;
            let songs=result.songs;
            for(let song of songs){
                let li=document.createElement("li");
                li.innerHTML = `<img class="avatar" src="${song.cover}" />
                                <div class="name">${song.name}</div>`;
                ul.appendChild(li);
            }
        });
}
