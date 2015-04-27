darg.controller('DargUserCtrl', 
    ['$cookieStore',
     '$location',
     '$scope', 
     '$http',
     '$routeParams',
     'alert',
     'auth',
     'user',
     function(
         $cookieStore,
         $location,
         $scope, 
         $http, 
         $routeParams,
         alert,
         auth,
         user) {

    var self = this;

    $scope.auth = auth;

    $scope.loggedIn = function() {
        if ($cookieStore.get('logged-in') == true) {
            return true;
        } else {
            return false;
        }
    };

    $scope.LoginForm = {
        email: "",
        password: ""
    };

    $scope.getCurrentUser = function() {
        user.getCurrentUser()
        .then(function(data) {
            user.info = data;
        }, function(data) {
            console.log(data)
        });
    };

    $scope.isCurrentUser = function(id) {
        if (id == user.info.id) {
            return true;
        } else {
            return false;
        }
    };

    $scope.ResetForm = {
        "email": ""
    }

    $scope.LoadPasswordResetPage = function() {
        $location.path('/password_reset');
        $location.search('failed_login', null);
    };

    $scope.resetPassword = user.resetPassword;

    $scope.goToSignupPage = function() {
        $location.path('/signup');
    };

    this.sendEmailConfirmation = user.sendEmailConfirmation;

    /* watchers */
    $scope.$watch(function() {
        return $scope.loggedIn()
    }, function(newValue, oldValue) {
        if ($scope.loggedIn() == true) {
            $scope.getCurrentUser();
        }
    });

    $scope.$watch(function() {
        return user.info
    }, function(newValue, oldValue) {
        if (newValue != null) {
            $scope.currentUser = newValue;
        }
    });
}]);

