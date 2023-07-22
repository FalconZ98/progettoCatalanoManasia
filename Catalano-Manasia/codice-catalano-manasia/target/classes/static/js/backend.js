if(page){
    $("#" + page + "-li").addClass("active")
}

const openModal = (t) =>{
    const userId = t.getAttribute("customer-id")
    $("#userId").val(userId)
}

const setModal = (modal, t) =>{
    const creditCardId = t.getAttribute("credit-card-id")
    const actualBalance = t.getAttribute("credit-card-balance")
    $("#" + modal + "CreditCardId").val(creditCardId)
    $("#" + modal + "Balance").text(actualBalance)
    $("." + modal + "-max").attr("max",actualBalance)
}

$("#creditCardFilter").keyup(function(e){
    const filter = e.target.value
    const rows = $(".owner-username, .credit-card-number").closest(".credit-card-row")

    for(row of rows){
        if(row.innerText.includes(filter) || row.innerText.includes(filter)){
            row.closest(".credit-card-row").style.display = "table-row"
        }
        else{
            row.closest(".credit-card-row").style.display = "none"
        }
    }
})

$('#balanceUpCardFilter').keyup(function (e) {
    const filter = e.target.value
    const rows = $(".owner-username, .credit-card-number").closest(".credit-card-row")

    for(row of rows){
        const rowBalance = parseFloat($(row).find('.credit-card-balance').text().replace(' €', ''));
        const enteredBalance = parseFloat(filter);

        if (isNaN(enteredBalance) || rowBalance > enteredBalance) {
            $(row).closest(".credit-card-row").fadeIn('slow');
        } else {
            $(row).closest(".credit-card-row").fadeOut('slow');
        }
    }
})

$('#balanceDownCardFilter').keyup(function (e) {
    const filter = e.target.value
    const rows = $(".owner-username, .credit-card-number").closest(".credit-card-row")

    for(row of rows){
        const rowBalance = parseFloat($(row).find('.credit-card-balance').text().replace(' €', ''));
        const enteredBalance = parseFloat(filter);

        if (isNaN(enteredBalance) || rowBalance < enteredBalance) {
            $(row).closest(".credit-card-row").fadeIn('slow');
        } else {
            $(row).closest(".credit-card-row").fadeOut('slow');
        }
    }
})

$("#customerFilter").keyup(function(e){
    console.log('hi');

    const customerFilter = e.target.value;
    const usernames = $(".customer-row > .customer-username");
    const emails = $(".customer-row > .customer-email");
    const firstNames = $(".customer-row > .customer-firstName");
    const lastNames = $(".customer-row > .customer-lastName");
    const ids = $(".customer-row > .customer-id");

    for(let i = 0; i < usernames.length; i++){
        const username = usernames[i];
        const email = emails[i];
        const firstName = firstNames[i];
        const lastName = lastNames[i];
        const id = ids[i];

        if(username.innerText.includes(customerFilter) || email.innerText.includes(customerFilter) || firstName.innerText.includes(customerFilter) || lastName.innerText.includes(customerFilter) || id.innerText.includes(customerFilter)){
            username.closest(".customer-row").style.display = "table-row";
        }
        else{
            username.closest(".customer-row").style.display = "none";
        }
    }
});

$("#merchantFilter").keyup(function(e){
    console.log('hi');

    const merchantFilter = e.target.value;
    const usernames = $(".merchant-row > .merchant-username");
    const emails = $(".merchant-row > .merchant-email");
    const firstNames = $(".merchant-row > .merchant-firstName");
    const lastNames = $(".merchant-row > .merchant-lastName");
    const stores = $(".merchant-row > .merchant-store");
    const ids = $(".merchant-row > .merchant-id");

    for(let i = 0; i < usernames.length; i++){
        const username = usernames[i];
        const email = emails[i];
        const firstName = firstNames[i];
        const lastName = lastNames[i];
        const store = stores[i];
        const id = ids[i];

        if(username.innerText.includes(merchantFilter) || email.innerText.includes(merchantFilter) || firstName.innerText.includes(merchantFilter) || lastName.innerText.includes(merchantFilter) || id.innerText.includes(merchantFilter) || store.innerText.includes(merchantFilter)){
            username.closest(".merchant-row").style.display = "table-row";
        }
        else{
            username.closest(".merchant-row").style.display = "none";
        }
    }
});

$("#showActiveCards").change(function() {
    const showActiveOnly = $("#showActiveCards").is(":checked");

    const rows = $(".owner-username, .credit-card-number").closest(".credit-card-row");

    for (row of rows) {
        const isEnabled = $(row).find('.credit-card-enabled').text().trim() === 'Yes';

        if (showActiveOnly && !isEnabled) {
            $(row).closest(".credit-card-row").fadeOut('slow');
        } else {
            $(row).closest(".credit-card-row").fadeIn('slow');
        }
    }
});


