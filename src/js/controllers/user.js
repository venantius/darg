darg.controller('DargUserCtrl', 
    ['$cookieStore',
     '$location',
     '$scope', 
     '$http',
     '$routeParams',
     'auth',
     'user',
     function(
         $cookieStore,
         $location,
         $scope, 
         $http, 
         $routeParams,
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



    $scope.UserProfile = {
        "name": "",
        "email": "",
        "timezone": "",
        "email_hour": ""
    };

    $scope.times = [
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


    $scope.updateTimezoneSetting = function(tz) {
        $scope.UserProfile.timezone = tz;
    }

    $scope.updateEmailHourSettings = function(hour) {
        $scope.UserProfile.email_hour = hour;
    }

    $scope.updateProfile = user.updateProfile;
    getDefaultTeam = function() {
        if (user.info != null) {
            if (user.info.team.length == 0) {
                return null; 
            } else if ($routeParams.teamId != null) {
                return $routeParams.teamId
            } else {
                return user.info.team[0].id;
            }
        };
    };

    $scope.getCurrentUser = function() {
        user.getCurrentUser()
        .then(function(data) {
            user.info = data;
            $scope.UserProfile.name = data.name;
            $scope.UserProfile.email = data.email;
            $scope.UserProfile.timezone = data.timezone;
            $scope.UserProfile.email_hour = data.email_hour;
            user.current_team = getDefaultTeam();
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
    };

    $scope.resetPassword = user.resetPassword;

    $scope.goToSignupPage = function() {
        $location.path('/signup');
    };

    this.emailConfirmationAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content}
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
            if ($scope.currentUser.confirmed_email == false) {
                self.setAlert(self.emailConfirmationAlerts,
                              user.emailConfirmationMessage);
            };
        }
    });

    $scope.$watch(function() {
        return getDefaultTeam()
    }, function(newValue, oldValue) {
        user.current_team = getDefaultTeam();
    });
}]);

