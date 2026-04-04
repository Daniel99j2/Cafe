async function init() {
    let hasAsked = false;
    window.addEventListener("load", () => {
        if(hasAsked) return;
        Notification.requestPermission().then(result => {
            hasAsked = true;
            if(result !== "granted") alert("You must enable notifications for this com.daniel99j.site")
            else {

            }
        });
    });
}