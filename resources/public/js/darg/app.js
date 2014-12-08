var darg = angular.module('darg', ['ngCookies', 'ngRoute', 'ngLoadScript']);

darg.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        .when('/', {
            templateUrl: 'templates/home.html'
        })
        .when('/timeline/:teamId', {
            templateUrl: 'templates/home.html'
        })
        .when('/about', {
            templateUrl: 'templates/about.html'
        })
        .when('/api', {
            templateUrl: 'templates/api.html'
        })
        .when('/faq', {
            templateUrl: 'templates/faq.html'
        })
        .when('/integrations', {
            templateUrl: 'templates/integrations.html'
        })
        .when('/password_reset', {
            templateUrl: 'templates/password_reset.html'
        })
        .when('/settings', {
            templateUrl: 'templates/settings.html'
        })
        .otherwise({
            redirectTo: '/'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
   }
]);

darg.controller('DargPageCtrl', ['$scope', '$http', '$location', 
               function($scope, $http, $location) {
    $scope.header = "templates/header.html";
    $scope.footer = "templates/footer.html";

    $scope.inner = "templates/timeline.html";
    $scope.outer = "templates/outer.html";
   }
]);

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

darg.controller('DargTimelineCtrl', 
    ['$scope', 
     '$http', 
     '$cookies', 
     '$cookieStore', 
     'user',
     function(
         $scope, 
         $http, 
         $cookies, 
         $cookieStore, 
         user) {

    $scope.formatDateString = function(date) {
        return Date.parse(date);
    }

    $scope.GetTimeline = function() {
        $http({
            method: "get",
            url: "/api/v1/darg/1"
        })
        .success(function(data) {
            $scope.Timeline = data;
            console.log("Succeeded");
        })
        .error(function(data) {
            console.log("Failed to get timeline");
        });
    };

    $scope.TaskForm = {
        "task": ""
    };

    $scope.PostTask = function() {
        $http({
            method: "post",
            url: "/api/v1/task",
            data: $.param($scope.TaskForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            console.log("Success!");
        })
    }

    $scope.$watch(function() {
        return (user.team != null && user.loggedIn())
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

    getDefaultTeam = function(current_user) {
        if (current_user.teams.length == 0) {
            return null; 
        } else {
            return current_user.teams[0].id;
        }
    };

    $scope.getCurrentUser = function() {
        $http({
            method: "get",
            url: "/api/v1/user"
        })
        .success(function(data) {
            user.info = data;
            $scope.CurrentUser = data;
            user.team = getDefaultTeam(user.info);
        })
        .error(function(data) {
            console.log("Failed to get current user!");
            console.log(data);
        });
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

    /* Watchers */
    $scope.$watch(function() {
        return user.loggedIn()
    }, function(oldValue, newValue) {
        if ($scope.loggedIn() == true) {
            $scope.getCurrentUser();
            $scope.loadGravatar("navbar", 40);
            $scope.loadGravatar("timeline", 100);
        }
    });
}]);


darg.factory('user', function($cookieStore) {
    var service = {};

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
