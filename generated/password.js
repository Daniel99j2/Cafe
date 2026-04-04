async function getUserAndPassword() {
    return {user: await cookieStore.get("user"), password: await cookieStore.get("password")};
}

async function setPassword(user, password) {
    await cookieStore.set({
        name: "password",
        value: password,
    });

    await cookieStore.set({
        name: "user",
        value: user
    });
}

async function login() {
    key = await getUserAndPassword();
    if(key.user == null || key.password == null) {
        params = new URLSearchParams()
        params.set("redirect", window.location.href);
        window.location.replace("http://localhost:8080/login?"+params.toString());
    }
}