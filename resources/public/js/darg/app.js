/* 
* This little bit of code is needed in order to load Javascript within "partials"
* - the html templates we use that get loaded by Angular's ng-include directive.
*
* At some point I'll refactor it out into its own file (which should generally
* happen with all of the Angular code), but I'm not yet certain how to do that.
*/

(function (ng) {
  'use strict';

  var app = ng.module('ngLoadScript', []);

  app.directive('script', function() {
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

/* Darg application code begins here */
var app = angular.module('darg', ['ngCookies', 'ngRoute', 'ngLoadScript']);

/*
 * AngularJS routing code goes here
 */

app.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        .when('/', {
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

/*
 * AngularJS services go here
 */

app.factory('user', function($cookieStore) {
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
 * AngularJS controllers go here
 */

app.controller('DargPageCtrl', ['$scope', '$http', '$location', 
               function($scope, $http, $location) {
    $scope.header = "templates/header.html";
    $scope.footer = "templates/footer.html";

    $scope.inner = "templates/timeline.html";
    $scope.outer = "templates/outer.html";
   }
]);

app.controller('DargLoginCtrl',
       ['$scope', '$http', '$cookies', '$cookieStore', '$location', 'user',
       function($scope, $http, $cookies, $cookieStore, $location, user) {

    $scope.loggedIn = user.loggedIn;
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
            $scope.Gravatar("40");
        });
    };

    $scope.Logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
        })
        .error(function(data) {
            console.log("Error logging out.");
            console.log(data);
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

    $scope.gravatars = {
        "navbar": "",
        "timeline": ""
    }
    $scope.loadGravatar = function(target, size) {
        console.log(size);
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
    $scope.loadGravatar("navbar", 40);
    $scope.loadGravatar("timeline", 100);

}]);

app.controller('DargSignupCtrl', ['$scope', '$http', '$cookies', '$cookieStore',
               function($scope, $http, $cookies, $cookieStore) {

    $scope.SignupForm = {
        givenName: "",
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
            $scope.Gravatar("40")
        })
        .error(function(data) {
            console.log("Error signing up");
            console.log(data);
        });
    };
}]);

app.controller('DargTimelineCtrl', ['$scope', '$http', '$cookies', '$cookieStore', 'user',
               function($scope, $http, $cookies, $cookieStore, user) {

    /*
     * This is totally an anti-pattern but I'm just learning how services work.
     */
    $scope.loggedIn = user.loggedIn;
    $scope.formatDateString = function(date) {
        return Date.parse(date);
    }

    $scope.GetTimeline = function() {
        $http({
            method: "get",
            url: "/api/v1/darg"
        })
        .success(function(data) {
            $scope.Timeline = data;
        })
        .error(function(data) {
            $scope.Timeline = "Error: failed to retrieve timeline";
            console.log("Failed to retrieve timeline");
            console.log(data);
        });
    };

    // I am ashamed to say that this took me way longer to figure out than
    // it should have :(
    $scope.$watch('loggedIn()', function(oldValue, newValue) {
        if ($scope.loggedIn() == true) {
            $scope.GetTimeline();
        }
    });

}]);
