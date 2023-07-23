// Seleziona l'elemento select con l'ID "store"
var selectElement = document.getElementById("store");

// Aggiungi un listener per l'evento "change" all'elemento select
selectElement.addEventListener("change", function() {
  // Ottieni l'opzione selezionata
  var selectedOption = this.options[this.selectedIndex];

  // Modifica lo stile dell'opzione selezionata
  selectedOption.style.backgroundColor = "purple";
  selectedOption.style.color = "white";
});