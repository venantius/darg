darg.controller('DargSettingsCtrl', 
    ['$cookies',
     '$cookieStore',
     '$http', 
     '$location',
     '$routeParams',
     '$scope', 
     'user',
     function(
         $cookies, 
         $cookieStore, 
         $http,
         $location,
         $routeParams,
         $scope, 
         user) {

    $scope.isSettingsProfile = function() {
        if ($routeParams.settingPage == "profile") {
            return true;
        } else {
            return false;
        }
    }

    $scope.gotoSettingsProfile = function() {
        $location.path("/settings/profile");
    }

    $scope.isSettingsAccount = function() {
        if ($routeParams.settingPage == "account") {
            return true;
        } else {
            return false;
        }
    }

    $scope.gotoSettingsAccount = function() {
        $location.path("/settings/account");
    }

}]);
