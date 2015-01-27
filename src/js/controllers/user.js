darg.controller('DargUserCtrl', 
    ['$cookies',
     '$cookieStore',
     '$location',
     '$scope', 
     '$http',
     '$routeParams',
     'user',
     function(
         $cookies,
         $cookieStore,
         $location,
         $scope, 
         $http, 
         $routeParams,
         user) {

    $scope.loggedIn = user.loggedIn
    $scope.CurrentUser = {};
    $scope.LoginForm = {
        email: "",
        password: ""
    };

    $scope.Login = function() {
        $http({
            method: "post",
            url: '/api/v1/login', 
            data: $.param($scope.LoginForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.getCurrentUser();
        })
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
            $location.path('/');
        })
    };

    $scope.UserSettingsProfile = {
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
        $scope.UserSettingsProfile.timezone = tz;
    }

    $scope.updateEmailHourSettings = function(hour) {
        $scope.UserSettingsProfile.email_hour = hour;
    }

    $scope.updateUserProfile = function() {
        $http({
            method: "post",
            url: "/api/v1/user",
            data: $.param($scope.UserSettingsProfile),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.loadGravatar("navbar", 40);
            $scope.loadGravatar("timeline", 100);
        })
    };

    getDefaultTeam = function() {
        if (user.info != null) {
            if (user.info.teams.length == 0) {
                return null; 
            } else if ($routeParams.teamId != null) {
                return $routeParams.teamId
            } else {
                return user.info.teams[0].id;
            }
        };
    };

    $scope.getCurrentUser = function() {
        $http({
            method: "get",
            url: "/api/v1/user"
        })
        .success(function(data) {
            user.info = data;
            $scope.CurrentUser = data;
            $scope.UserSettingsProfile.name = data.name;
            $scope.UserSettingsProfile.email = data.email;
            $scope.UserSettingsProfile.timezone = data.timezone;
            $scope.UserSettingsProfile.email_hour = data.email_hour;
            user.team = getDefaultTeam();
        })
    };

    $scope.isCurrentUser = function(id) {
        if (id == user.info.id) {
            return true;
        } else {
            return false;
        }
    };

    $scope.gravatars = {
        "navbar": null,
        "timeline": null
    }

    $scope.loadGravatar = function(target, size) {
        $http({
            method: "post",
            data: $.param({"size": size}),
            url: "/api/v1/gravatar",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data, status) {
            $scope.gravatars[target] = data;
        });
    };

    $scope.ResetForm = {
        "email": ""
    }

    $scope.LoadPasswordResetPage = function() {
        $location.path('/password_reset');
    };

    $scope.resetPassword = function() {
        $http({
            method: "post",
            url: "/api/v1/password_reset",
            data: $.param($scope.ResetForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            // TODO: Provide a tooltip or something on success?
            // No, replace the entire speech bubble
        })
        .error(function(data) {
            console.log("Failed to reset password");
            console.log(data);
        });
    };

    /* watchers */
    $scope.$watch(function() {
        return user.loggedIn()
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true) {
            $scope.getCurrentUser();
            $scope.loadGravatar("navbar", 40);
            $scope.loadGravatar("timeline", 100);
        }
    });

    $scope.$watch(function() {
        return getDefaultTeam()
    }, function(oldValue, newValue) {
        user.team = getDefaultTeam();
    });
}]);

