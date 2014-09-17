var app = angular.module('darg', ['ngCookies']);

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

app.controller('DargLoginCtrl', ['$scope', '$cookies', '$cookieStore',
               function($scope, $cookies, $cookieStore) {
    // Are we logged in?
    $scope.LoggedIn = $cookieStore.get('logged-in');

    $scope.LoginForm = {
      email: "",
      password: ""
    };
}]);
