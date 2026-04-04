function getCart() {
    let elementNames = document.getElementById("_cartItems").textContent;
    let cart = [];
    totalCost = 0;
    for (let name of elementNames.split(" ")) {
        if(name == " " || name == "" || name == null) continue;
        elementName = "_cartItem_"+name;
        console.log(elementName);
        console.log(document.getElementById(elementName+"_Input"));
        quantity = document.getElementById(elementName+"_Input").value;
        cost = document.getElementById(elementName).dataset.cost;
        cart.push({name: name, quantity: quantity, totalCost: cost*quantity, cost: cost});
        totalCost += cost*quantity;
    }

    const formatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    });
    document.getElementById("_cartCost").textContent = "Total Cost: "+formatter.format(totalCost);
    return {cart: cart, totalCost: totalCost};
}

function initCart() {
    document.getElementById("Cart").addEventListener("click", () => {
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