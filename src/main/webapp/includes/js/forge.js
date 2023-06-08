$(function (){
    $("#inventory").on("drop", dropInventory);
    $("#inventory").on("dragover", allowDrop);
    $(".u-inventory-item").on("dragstart", drag);
    $(".u-forge-slot").on("drop", drop);
    $(".u-forge-slot").on("dragover", allowDrop);
    $(".u-clear").on("click", clearSlot);
});

function refreshSlotsCss(){
    $(".u-forge-slot").each(function(){
        if ($(this).children().length===0){
            $(this).css("border", "1px dashed #222222");
        }
    });
}

function clearSlot(ev){
    const tg = $(ev.originalEvent.target);
    const att = tg.attr("data-target");
    var slot = $("#"+att);
    if (slot.children().length>0){
        $("#inventory").append(slot.children(":first"));
        refreshSlotsCss();
        console.log("Moved to inventory");
    }
    console.log("Target: #"+att);
}

function allowDrop(ev) {
    ev.originalEvent.preventDefault();
}

function drag(ev) {
    ev.originalEvent.dataTransfer.setData("text", ev.originalEvent.target.id);
}

function drop(ev) {
    const tg = $(ev.originalEvent.target);
    console.log("Current target children: ID="+tg.attr("id")+" L=" +tg.children().length);
    if ( tg.children().length < 1 && tg.hasClass("u-forge-slot")) {
        ev.originalEvent.preventDefault();
        var data = ev.originalEvent.dataTransfer.getData("text");
        var el = document.getElementById(data);
        var amount_max = el.getAttribute("data-amount-max");
        var amount = prompt("Choisir le nombre", amount_max);

        el.setAttribute("data-amount",amount);
        tg.append(el);
        tg.css("border","1px solid orange");
    }

}

function dropInventory(ev){
    ev.originalEvent.preventDefault();
    var data = ev.originalEvent.dataTransfer.getData("text");
    var el = document.getElementById(data);
    el.removeAttribute("data-amount");
    $("#u-inventory-item").append(el);
    refreshSlotsCss();
}

function enableForgeButton(){
    //$("#forge").remove()
}