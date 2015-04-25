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

    var self = this;

    $scope.isSettingsProfile = function() {
        if ($routeParams.settingPage == "profile") {
            return true;
        } else {
            return false;
        }
    }

    $scope.timezones = moment.tz.names();

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

    this.emailConfirmationAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content};
    };

    $scope.$watch(function() {
        return $location.search().confirmation_token
    }, function(newValue, oldValue) {
        if (newValue != null) {
            user.confirmEmail(newValue)
            .then(function(data) {
                self.setAlert(self.emailConfirmationAlerts,
                          "E-mail address confirmed!");
            }, function(data) {
                console.log(data) 
            });
        };
    });

}]);
