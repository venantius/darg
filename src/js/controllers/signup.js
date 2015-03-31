darg.controller('DargSignupCtrl', ['$scope', '$http', '$cookies', '$cookieStore',
               function($scope, $http, $cookies, $cookieStore) {

    $scope.SignupForm = {
        name: "",
        email: "",
        password: ""
    };

    $scope.Signup = function() {
        $http({
            method: "post",
            url: '/api/v1/user', 
            data: $.param($scope.SignupForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.loadGravatar("navbar", 40)
            $scope.loadGravatar("timeline", 100)
            $scope.getCurrentUser();
        })
        .error(function(data) {
            console.log("Error signing up");
            console.log(data);
        });
    };
}]);
