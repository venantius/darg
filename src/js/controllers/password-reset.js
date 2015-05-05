darg.controller('DargPasswordResetCtrl',
    ['$location',
     '$scope',
     'auth',
     function(
       $location,
       $scope,
       auth) {
    var self = this;

    this.auth = auth;

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
      console.log(self.passwordResetForm);
    });
}]);
