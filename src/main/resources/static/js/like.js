function likeSinger(id) {
    fetch(`/fav?itemId=${id}&itemType=singer`).then(r => console.log("success"));
}
function likeSong(id) {
    fetch(`/fav?itemId=${id}&itemType=song`).then(r => console.log("success"));
}