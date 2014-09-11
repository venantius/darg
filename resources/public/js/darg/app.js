(function(){
    var app = angular.module('darg', []);
    
    app.controller('DargCtrl', function(){
        this.products = gems;
    });

    var gems = [
        {
            name: "Dodecahedron",
            price: 2.95,
            description: ". . . ",
            canPurchase: true,
        },
        {
            name: "Pentagonal Gem",
            price: 5.95,
            description: ". . .",
            canPurchase: false
        }]
})();
