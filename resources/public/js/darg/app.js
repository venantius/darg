var darg = angular.module('darg', ['ngCookies', 'ngRoute', 'ngLoadScript']);

darg.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        .when('/', {
            templateUrl: '/templates/home.html'
        })
        .when('/timeline/:teamId', {
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
        .when('/settings', {
            templateUrl: '/templates/settings.html'
        })
        .when('/settings/:settingPage', {
            templateUrl: '/templates/settings.html'
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
            url: '/api/v1/signup', 
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
            $scope.GetTimeline();
        })
    }

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

    $scope.GetTimeline = function() {
        if (user.team != null) {
            url = "/api/v1/darg/" + user.team
            $http({
                method: "get",
                url: url
            })
            .success(function(data) {
                $scope.Timeline = data;
            })
            .error(function(data) {
                console.log("Failed to get timeline");
            });
        } else {
            console.log("Something is fucked");
        }
    };

    $scope.loadNewTeamTimeline = function(id) {
        url = "/timeline/" + id;
        $location.path(url);
    }

    $scope.$watch(function() {
        return user.team
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true && user.team != null) {
            $scope.GetTimeline();
        }
    });

}]);


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
        "email": ""
    };

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
            user.team = getDefaultTeam();
        })
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


darg.factory('user', function($cookieStore) {
    var service = {};
    var info = null;
    var team = null;

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
