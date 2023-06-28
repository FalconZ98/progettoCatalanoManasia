
  var selectElement = document.getElementById("store");

  selectElement.addEventListener("change", function() {
    var selectedOption = this.options[this.selectedIndex];
    selectedOption.style.backgroundColor = "purple";
    selectedOption.style.color = "white";
  });
