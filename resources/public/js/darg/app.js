var darg = angular.module('darg', 
                          ['ngCookies', 
                           'ngRoute', 
                           'ngLoadScript',
                           'ui.date',
                           'ui.gravatar']);

darg.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        // outer
        .when('/', {
            templateUrl: '/templates/home.html'
        })
        .when('/about', {
            templateUrl: '/templates/about.html'
        })
        .when('/api', {
            templateUrl: '/templates/api.html'
        })
        .when('/faq', {
            templateUrl: '/templates/faq.html'
        })
        .when('/integrations', {
            templateUrl: '/templates/integrations.html'
        })
        .when('/password_reset', {
            templateUrl: '/templates/password_reset.html'
        })

        // inner
        .when('/team', {
            templateUrl: '/templates/team.html',
            controller: 'DargTeamCtrl',
            controllerAs: 'Team'
        })
        .when('/team/:teamId', {
            templateUrl: '/templates/team/settings.html',
            controller: 'DargTeamCtrl',
            controllerAs: 'Team'
        })
        .when('/team/:teamId/timeline', {
            templateUrl: '/templates/team/timeline.html',
            controller: 'DargTimelineCtrl',
            controllerAs: 'Timeline'
        })
        .when('/settings/profile', {
            templateUrl: '/templates/settings/profile.html',
            controller: 'DargSettingsCtrl',
            controllerAs: 'Settings'
        })

        .otherwise({
            redirectTo: '/'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
   }
]);

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

}]);

darg.controller('DargSignupCtrl', ['$scope', '$http', '$cookies', '$cookieStore',
               function($scope, $http, $cookies, $cookieStore) {

    $scope.SignupForm = {
        name: "",
        email: "",
        password: ""
    };

    $scope.Signup = function() {
        $http({
            method: "post",
            url: '/api/v1/user', 
            data: $.param($scope.SignupForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $scope.loadGravatar("navbar", 40)
            $scope.loadGravatar("timeline", 100)
            $scope.getCurrentUser();
        })
        .error(function(data) {
            console.log("Error signing up");
            console.log(data);
        });
    };
}]);

darg.controller('DargTaskCreationCtrl', 
    ['$scope', 
     '$http', 
     '$routeParams',
     '$cookies', 
     '$cookieStore', 
     'user',
     function(
         $scope, 
         $http, 
         $routeParams,
         $cookies, 
         $cookieStore, 
         user) {

    $scope.TaskForm = {
        "date": "",
        "task": "",
        "team_id": ""
    };

    $scope.PostTask = function(darg) {
        $scope.TaskForm.date = darg.date;
        $scope.TaskForm.team_id = $routeParams.teamId;
        $http({
            method: "post",
            url: "/api/v1/task",
            data: $.param($scope.TaskForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            console.log("Posted new task!");
            $scope.GetTimeline(user.current_team);
        })
    }

}]);

darg.controller('DargTeamCtrl',
    [
    '$cookies',
    '$cookieStore',
    '$http',
    '$location',
    '$routeParams',
    '$scope',
    'team',
    'user',
    function(
        $cookies,
        $cookieStore,
        $http,
        $location,
        $routeParams,
        $scope,
        team,
        user) {

    this.creationForm = {
        name: "",
    };
    this.team = {}

    this.createTeam = function() {
        $http({
            method: "post",
            url: "/api/v1/team",
            data: $.param(this.creationForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            url = "/timeline/" + data.id
            $location.path(url);
        })
    };

    /* $watch section*/
    var self = this;

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return $routeParams.teamId
    }, function(oldValue, newValue) {
        if (newValue != null) {
            team.getTeam(newValue)
            .then(function(data) {
                self.team = data
            }, function(data) {
                console.log("Failed to update team.");
            });
        }
    });
}]);

darg.controller('DargTimelineCtrl', 
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

    $scope.formatDateString = function(date) {
        return Date.parse(date);
    }

    /* 
     * Display this user if they have content, or if they're the currently
     * logged-in user 
     */
    $scope.showUser = function(user) {
        if ($scope.userHasTasks(user) || $scope.isCurrentUser(user.id)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Does this user have any tasks listed for today?
     */
    $scope.userHasTasks = function(user) {
        if (user.task.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    $scope.GetTimeline = function(id) {
        if (id != null) {
            url = "/api/v1/darg/team/" + id
            $http({
                method: "get",
                url: url
            })
            .success(function(data) {
                url = "/team/" + id + "/timeline";
                $location.path(url);
                $scope.Timeline = data;
            })
            .error(function(data) {
                console.log("Failed to get timeline");
            });
        } else {
            console.log("Something is fucked");
        }
    };

    $scope.$watch(function() {
        return user.current_team
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true && user.current_team != null) {
            $scope.GetTimeline(user.current_team);
        }
    });
}]);

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
        }
    });

    $scope.$watch(function() {
        return getDefaultTeam()
    }, function(oldValue, newValue) {
        user.current_team = getDefaultTeam();
    });
}]);


darg.service('team', function($http, $q) {
    this.getTeam = function(id) {
        url = "/api/v1/team/" + id;
        var deferred = $q.defer();
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        return deferred.promise;
    };
});

darg.factory('user', function($cookieStore) {
    var service = {};
    var info = null;
    var current_team = null;

    service.loggedIn = function() {
        if ($cookieStore.get('logged-in') == true) {
            return true;
        } else {
            return false;
        }
    };

    return service;
});

/* 
* This little bit of code is needed in order to load Javascript within "partials"
* - the html templates we use that get loaded by Angular's ng-include directive.
*
* At some point I'll refactor it out into its own file (which should generally
* happen with all of the Angular code), but I'm not yet certain how to do that.
*/

(function (ng) {
  'use strict';

  var darg = ng.module('ngLoadScript', []);

  darg.directive('script', function() {
    return {
      restrict: 'E',
      scope: false,
      link: function(scope, elem, attr) {
        if (attr.type==='text/javascript-lazy') {
          var s = document.createElement("script");
          s.type = "text/javascript";
          var src = elem.attr('src');
          if(src!==undefined) {
              s.src = src;
          }
          else {
              var code = elem.text();
              s.text = code;
          }
          document.head.appendChild(s);
          elem.remove();
        }
      }
    };
  });
}(angular));
