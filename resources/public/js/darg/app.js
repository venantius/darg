var app = angular.module('darg', ['ngCookies', 'ngRoute']);

app.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        .when('/', {
            templateUrl: 'templates/home.html'
        })
        .when('/about', {
            templateUrl: 'templates/about.html'
        })
        .when('/faq', {
            templateUrl: 'templates/faq.html'
        })
        .when('/settings', {
            templateUrl: 'templates/settings.html'
        })
        .otherwise({
            redirectTo: '/'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
   }
]);

app.controller('DargPageCtrl', ['$scope', '$http', '$location', 
               function($scope, $http, $location) {
    $scope.header = "templates/header.html";
    $scope.footer = "templates/footer.html";

    $scope.inner = "templates/timeline.html";
    $scope.outer = "templates/outer.html";

    // $locationProvider();
    $scope.location = $location.path();
   }
]);

app.controller('DargLoginCtrl', ['$scope', '$http', '$cookies', '$cookieStore',
               function($scope, $http, $cookies, $cookieStore) {
    // Are we logged in?
    $scope.LoggedIn = function() {
        if ($cookieStore.get('logged-in') == true) {
            return true;
        } else {
            return false;
        }};

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
            console.log("Logged in.");
            $scope.Gravatar();
        });
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
            console.log("Logged out.");
        })
        .error(function(data) {
            console.log("Error logging out.");
        });
    };

    $scope.Gravatar = function() {
        $http({
            method: "get",
            url: "/api/v1/gravatar"
        })
        .success(function(data, status) {
            $scope.Gravatar_url = data;
        });
    };
    $scope.Gravatar_url = $scope.Gravatar();

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
            $scope.Gravatar()
        })
        .error(function(data) {
            console.log("Error signing up")
        });
    };
}]);
