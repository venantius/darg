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

app.controller('DargLoginCtrl', ['$scope', '$http', '$cookies', '$cookieStore',
               function($scope, $http, $cookies, $cookieStore) {
    // Are we logged in?
    $scope.LoggedIn = function() {
        if ($cookieStore.get('logged-in') == true) {
            return true;
        } else {
            return false;
        }}

    $scope.LoginForm = {
      email: "test-user2@darg.io",
      password: "samurai"
    };

    $scope.Login = function() {
        $http({
            method: "post",
            url: '/api/v1/login', 
            data: $.param($scope.LoginForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
            .success(function(data) {
                console.log(data);
                if (!data.success) {
                    // if not successful, bind errors to error variables
                    $scope.errorSuperhero = data.errors.superheroAlias;
                } else {
                    // if successful, bind success message to message
                    console.log(data.message);
                    $scope.message = data.message;
                }
            });
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
            console.log("Logged out.");
            if (!data.success) {
                $scope.errorSuperhero = data.errors.superheroAlias;
            } else {
                console.log(data.message);
                $scope.message = data.message;
            }
        });
    };

}]);
