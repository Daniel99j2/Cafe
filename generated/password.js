async function login() {
    const request = new XMLHttpRequest();
    request.open("POST", "api/login");
    request.onload = async function () {
        if (request.status === 200) {
            console.log("Login successful");
        } else if (request.status === 401) {
            params = new URLSearchParams()
            params.set("redirect", window.location.href);
            window.location.replace("http://localhost:8080/login?" + params.toString());
        } else {
            alert("Something went wrong whilst logging in");
        }
    }
    request.send();
}