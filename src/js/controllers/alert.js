darg.controller('DargAlertCtrl',
    ['$location',
     '$scope',
     'alert',
     'user',
     function(
         $location,
         $scope,
         alert,
         user) {

    this.alerts = alert;

    /*
     * watchers
     */
    $scope.$watch(function() {
        return $location.search().failed_login;
    }, function(newValue, oldValue) {
        if (newValue != null) {
            alert.setAlert(alert.failedLoginAlerts,
                           alert.failedLoginMessage);
        } else {
            alert.failedLoginAlerts = [];
        }
    });

    $scope.$watch(function() {
      return $location.path();
    }, function(newValue, oldValue) {
      if (newValue != "/login") {
        alert.failedLoginAlerts = [];
        $location.search('failed_login', null);
      }
    });

    $scope.$watch(function() {
        return user.info.confirmed_email
    }, function(newValue, oldValue) {
        if (newValue == false) {
            alert.setAlert(alert.emailConfirmationAlerts,
                           alert.emailConfirmationMessage);
        } else {
            alert.emailConfirmationAlerts = []
        }
    });

 }]);
