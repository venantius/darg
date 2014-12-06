darg.controller('DargLoginCtrl',
       ['$scope', '$http', '$cookies', '$cookieStore', '$location', 'user',
       function($scope, $http, $cookies, $cookieStore, $location, user) {

    $scope.loggedIn = user.loggedIn;
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
            $scope.loadGravatar("navbar", 40);
            $scope.loadGravatar("timeline", 100);
            $scope.getCurrentUser();
        });
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
        })
        .error(function(data) {
            console.log("Error logging out.");
            console.log(data);
        });
    };

    $scope.ResetForm = {
        "email": ""
    }

    $scope.LoadPasswordResetPage = function() {
        $location.path('/password_reset');
    };

    $scope.resetPassword = function() {
        $http({
            method: "post",
            url: "/api/v1/password_reset",
            data: $.param($scope.ResetForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            // TODO: Provide a tooltip or something on success?
            // No, replace the entire speech bubble
        })
        .error(function(data) {
            console.log("Failed to reset password");
            console.log(data);
        });
    };

    $scope.gravatars = {
        "navbar": null,
        "timeline": null
    }
    $scope.loadGravatar = function(target, size) {
        $http({
            method: "post",
            data: $.param({"size": size}),
            url: "/api/v1/gravatar",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data, status) {
            $scope.gravatars[target] = data;
        });
    };
    $scope.loadGravatar("navbar", 40);
    $scope.loadGravatar("timeline", 100);

}]);

