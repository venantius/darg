darg.controller('DargUserCtrl', 
    ['$cookieStore',
     '$location',
     '$scope', 
     '$http',
     '$routeParams',
     'user',
     function(
         $cookieStore,
         $location,
         $scope, 
         $http, 
         $routeParams,
         user) {

    $scope.loggedIn = user.loggedIn
    $scope.currentUser = {};
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
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
            $cookieStore.remove('logged-in');
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
        url = "/api/v1/user/" + $cookieStore.get('id');
        $http({
            method: "post",
            url: url,
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
        url = "/api/v1/user/" + $cookieStore.get('id');
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            user.info = data;
            $scope.currentUser = data;
            $scope.UserSettingsProfile.name = data.name;
            $scope.UserSettingsProfile.email = data.email;
            $scope.UserSettingsProfile.timezone = data.timezone;
            $scope.UserSettingsProfile.email_hour = data.email_hour;
            user.current_team = getDefaultTeam();
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
        user.current_team = getDefaultTeam();
    });
}]);

