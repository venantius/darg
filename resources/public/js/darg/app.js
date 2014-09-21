var app = angular.module('darg', ['ngCookies']);

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
      email: "",
      password: ""
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

    $scope.Gravatar_url = "";
    $scope.Gravatar = $http({
        method: "get",
        url: "/api/v1/gravatar"
    })
    .success(function(data) {
        if (!data.success) {
            console.log(data.errors);
            $scope.Gravatar_url = data;
        } else {
            console.log(data.message);
            console.log(data);
            $scope.message = data.message;
        }
    });

}]);

app.controller('DargSignupCtrl', ['$scope', '$http', '$cookies', '$cookieStore',
               function($scope, $http, $cookies, $cookieStore) {
    $scope.SignupForm = {
      givenName: "",
      email: "",
      password: ""
    };

    $scope.Signup = function() {
        $http({
            method: "post",
            url: '/api/v1/signup', 
            data: $.param($scope.SignupForm),
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
}]);
