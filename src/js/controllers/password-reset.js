darg.controller('DargPasswordResetCtrl',
    ['$location',
     '$scope',
     'alert',
     'auth',
     function(
       $location,
       $scope,
       alert,
       auth) {
    var self = this;

    this.auth = auth;

    $scope.ResetForm = {
        "email": ""
    }

    $scope.resetPassword = function(params) {
      auth.resetPassword(params)
      .then(function(data) {
        console.log("success!")
        alert.setAlert(alert.passwordResetAlerts,
                       alert.passwordResetSuccessMessage,
                      "dialog-success");
      }, function(data) {
        alert.setAlert(alert.passwordResetAlerts,
                       data.message,
                       "dialog-danger");
        console.log(data)
      })
    };

    this.setNewPassword = function(params) {
      auth.setNewPassword(params)
      .then(function(data) {
        $location.path('/');
        $location.search('token', null);
      }, function(data) {
        console.log(data);
      })
    };

    this.passwordResetForm = {
      "password": "",
      "confirm_password": ""
    };

    $scope.$watch(function() {
      return $location.search().token
    }, function(newValue, oldValue) {
      if (newValue != null) {
        self.passwordResetForm.token = newValue
      }
    });
}]);
