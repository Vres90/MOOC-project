var url = contextRoot;

 /* This script was meant to be an anti-spam filter for the registration form, but it and its 
 * variations stopped working halfway through writing the project, particularly the checking of var z. 
 * Pilalle meni!

var z = 0;

window.onload = function dice() {
    var x = Math.floor((Math.random() * 30) + 1);
    var y = Math.floor((Math.random() * 30) + 1);
    z = x + y;

    document.getElementById("a").innerHTML = x;
    document.getElementById("b").innerHTML = y;
};

function validation() {
    var answer = document.getElementById("answer").value;
    if (answer !== z) {
        alert("Not so fast! Did you fill all the fields and give the correct answer to anti-spam question? answer: " + answer);
        return false;
    }
};
 */

function editField(id) {
    var penImage = document.getElementById(id);
    const field = document.createElement("input");
    field.name = "newAttributeValue";
    
    var submitNewInfo = document.createElement("form");
    submitNewInfo.method = "POST";
    submitNewInfo.action = "/account/edit/" + id;
    submitNewInfo.appendChild(field);
    
    penImage.parentNode.replaceChild(submitNewInfo, penImage);
    
}