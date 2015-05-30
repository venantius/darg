darg.controller('DargSettingsCtrl', 
    ['$cookies',
     '$cookieStore',
     '$http', 
     '$location',
     '$routeParams',
     '$scope', 
     'alert',
     'user',
     function(
         $cookies, 
         $cookieStore, 
         $http,
         $location,
         $routeParams,
         $scope,
         alert,
         user) {

    var self = this;

    this.timezones = moment.tz.names();
    this.times = [
        "midnight",
        "1am",
        "2am",
        "3am",
        "4am",
        "5am",
        "6am",
        "7am",
        "8am",
        "9am",
        "10am",
        "11am",
        "noon",
        "1pm",
        "2pm",
        "3pm",
        "4pm",
        "5pm",
        "6pm",
        "7pm",
        "8pm",
        "9pm",
        "10pm",
        "11pm"];

    this.emailConfirmationAlerts = [];
    this.profileUpdateAlerts = [];

    this.userProfile = {
        "name": "",
        "email": "",
        "timezone": "",
        "email_hour": "",
        "digest_hour": "",
    };

    this.updateTimezoneSetting = function(tz) {
        this.userProfile.timezone = tz;
    }

    this.updateEmailHourSettings = function(hour) {
        this.userProfile.email_hour = hour;
    }

    this.updateDigestHourSettings = function(hour) {
      this.userProfile.digest_hour = hour;
    }

    this.updateProfile = function(user_profile) {
        user.updateProfile(user_profile)
        .then(function(data) {
            console.log("success!");
            console.log(data);
            alert.setAlert(self.profileUpdateAlerts,
                           "Profile updated!",
                          "alert-success");
        }, function(data) {
            message = "Failed to update profile: " + data.message;
            alert.setAlert(self.profileUpdateAlerts,
                           message,
                           "alert-danger");
        });
    }

    /*
     * Watchers
     */
    $scope.$watch(function() {
        return $location.search().confirmation_token
    }, function(newValue, oldValue) {
        if (newValue != null) {
            user.confirmEmail(newValue)
            .then(function(data) {
                alert.setAlert(self.emailConfirmationAlerts,
                          "E-mail address confirmed!");
                user.info.confirmed_email = true;
            }, function(data) {
                console.log(data) 
            });
        };
    });

    $scope.$watch(function() {
        return user.info
    }, function(newValue, oldValue) {
        self.userProfile = newValue;
    });


}]);
