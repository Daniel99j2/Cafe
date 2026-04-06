lastCart = {};

function updateErrors() {
    canPurchase = true;
    if(document.getElementById('deliveryLocation').value === "") {
        document.getElementById("_error").textContent = "Please select a delivery location"
        canPurchase = false;
    } else if(document.getElementById('users').value === "null") {
        document.getElementById("_error").textContent = "Please select a delivery option"
        canPurchase = false;
    } else if(lastCart.tooExpensive) {
        document.getElementById("purchaseButton").disabled = true
        document.getElementById("_error").textContent = "Cost exceeds balance"
        canPurchase = false;
    } else if(lastCart.cart.length === 0) {
        document.getElementById("_error").textContent = "Your cart is empty"
        canPurchase = false;
    }

    if(canPurchase) {
        document.getElementById("purchaseButton").disabled = null
        document.getElementById("_error").hidden = true
    } else {
        document.getElementById("purchaseButton").disabled = true
        document.getElementById("_error").hidden = null
    }
}

function getCart() {
    let elementNames = document.getElementById("_cartItems").textContent;
    let cart = [];
    for (let name of elementNames.split("\n")) {
        if(name === " " || name === "" || name == null) continue;
        elementName = "_cartItem_"+name;
        quantity = document.getElementById(elementName+"_Input").value;
        cost = document.getElementById(elementName).dataset.cost;
        if(quantity > 0) cart.push({name: name, quantity: quantity, totalCost: cost*quantity, cost: cost});
    }

    lastCart = {cart: cart, deliverer: document.getElementById('users').value, deliveryLocation: document.getElementById('deliveryLocation').value};

    const request = new XMLHttpRequest();
    request.open("POST", "api/cart");
    request.onload = async function () {
        if(request.status === 200) {
            json = JSON.parse(request.responseText);
            lastCart.cost = parseFloat(json.cost);
            lastCart.tooExpensive = json.tooExpensive;
            lastCart.estimatedTime = parseFloat(json.estimatedTime);

            const formatter = new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
            });
            document.getElementById("_cartCost").textContent = "Total Cost: "+formatter.format(lastCart.cost);
            document.getElementById("_estimatedTime").textContent = "Estimated time: "+new Date(lastCart.estimatedTime * 60 * 1000).toISOString().substring(14, 19);

            lastCart.totalCost = json.totalCost;
            lastCart.deliveryLocation = document.getElementById("deliveryLocation").value

            updateErrors();
        }
    }
    request.send(JSON.stringify(lastCart));
    return lastCart;
}

function initCart() {
    window.addEventListener("click", () => {
        getCart()
    });
    getCart()
}

function getItem(elementId) {
    let elementNames = document.getElementById(elementId).textContent;
}

function purchaseAllItems() {
    const request = new XMLHttpRequest();
    try {
        request.open("POST", "api/purchase");
        request.setRequestHeader("Cart", JSON.stringify(getCart()));
        request.onload = async function () {
            if(request.status === 200) {
                window.location.href = request.responseText;
            }
        }
        request.send();
    } catch (error) {
        console.error(`XHR error ${request.status}`);
    }
}