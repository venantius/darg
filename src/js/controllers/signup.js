darg.controller('DargSignupCtrl', 
    ['$scope', 
     '$http',
     '$location',
     function($scope, $http, $location) {

    this.SignupForm = {
        name: "",
        email: "",
        password: "",
        token: $location.search().token
    };

    this.Signup = function() {
        $http({
            method: "post",
            url: '/api/v1/user', 
            data: $.param(this.SignupForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $location.path('/');
        })
        .error(function(data) {
            console.log(data)
        });
    };
}]);
