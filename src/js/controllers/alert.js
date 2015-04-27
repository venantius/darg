darg.controller('DargAlertCtrl',
    ['$location',
     '$scope',
     'alert',
     function(
         $location,
         $scope,
         alert) {

    this.alerts = alert;

    $scope.$watch(function() {
        return $location.search().failed_login;
    }, function(newValue, oldValue) {
        console.log("nuts");
        if (newValue != null) {
            alert.setAlert(alert.failedLoginAlerts,
                           alert.failedLoginMessage);
        } else {
            alert.failedLoginAlerts = [];
        }
    });

 }]);
