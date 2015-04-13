var darg = angular.module('darg', 
                          ['ngCookies', 
                           'ngRoute', 
                           'ngLoadScript',
                           'ui.bootstrap',
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
            templateUrl: '/templates/password_reset.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
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

darg.config(function($provide) {
  $provide.decorator('datepickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/datepicker.html';
    return $delegate;
  });
  $provide.decorator('yearpickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/year.html';
    return $delegate;
  });
  $provide.decorator('monthpickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/month.html';
    return $delegate;
  });
  $provide.decorator('daypickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/day.html';
    return $delegate;
  });
  $provide.decorator('datepickerPopupWrapDirective', function($delegate) {
      $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/popup.html';
      return $delegate;
  });
});

darg.controller('DargPasswordResetCtrl',
    ['$scope',
     'auth',
     function(
         $scope,
         auth) {
}]);

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
    'role',
    'team',
    'user',
    function(
        $cookies,
        $cookieStore,
        $http,
        $location,
        $routeParams,
        $scope,
        role,
        team,
        user) {
    var self = this;

    /* 
     * Forms
     */
    this.creationForm = {
        name: "",
    };
    this.newRole = {
        email: "",
    };

    /*
     * Controller model
     */
    this.team = {};
    this.roles = {};
    this.currentRole = {field: ""};

    /* 
     * Alerts
     */
    this.invitationSuccessAlerts = [];
    this.invitationFailureAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content};
    };

    /* 
     * Adding members to team
     */
    this.createRole = role.createRole;
    this.createTeam = team.createTeam;


    /*
     * Utility functions
     */

    /* Can we delete this user? */
    this.canDelete = function(target_user_id) {
        if (this.currentRole.admin == true || $cookieStore.get('id') == target_user_id) {
            return true;
        } else {
            return false
        }
    };


    this._refreshTeamData = function(team_id) {
        team.getTeam(team_id)
        .then(function(data) {
            self.team = data;
        }, function(data) {
            console.log(data);
        });
    };

    /* 
     * Since this gets called by $scope.$watch
     */
    this._refreshTeamAndRoleData = function(team_id) {
        self._refreshTeamData(team_id);

        role.getTeamRoles(team_id)
        .then(function(data) {
            self.roles = data;
        }, function(data) {
            console.log(data);
        });

        role.getRole(team_id, $cookieStore.get('id'))
        .then(function(data) {
            self.currentRole = data;
        }, function(data) {
            console.log(data);
        });
    }

    /* Delete a role, and update the model */
    this.deleteRole = function(team_id, user_id) {
        role.deleteRole(team_id, user_id)
        .then(function(data) {
            self._refreshTeamAndRoleData(team_id);
        }, function(data) {
             console.log(data);
        });
    };

    /* Create a role, and update the model */
    this.createRole = function(team_id, params) {
        role.createRole(team_id, params)
        .then(function(data) {
            self._refreshTeamAndRoleData(team_id);
            message = "Invitation sent to " + params.email + "!";
            self.setAlert(self.invitationSuccessAlerts, message);
        }, function(data) {
            console.log(data);
            self.setAlert(self.invitationFailureAlerts, data.message);
        });
    };

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return $routeParams.teamId
    }, function(oldValue, newValue) {
        if (newValue != null) {
            console.log("refreshing...");
            self._refreshTeamAndRoleData(newValue);
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

    /*
     * This is the date for the Datepicker. It has to be at $scope,
     * because of how the Angular UI folks wrote the datepicker. 
     * I loathe this.
     */
    $scope.date = new Date();

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
        if (user.current_team != null) {
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


    $scope.auth = auth;

    $scope.loggedIn = function() {
        if ($cookieStore.get('logged-in') == true) {
            return true;
        } else {
            return false;
        }
    };

    $scope.currentUser = {};
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
        url = "/api/v1/user/" + $cookieStore.get('id');
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            user.info = data;
            $scope.currentUser = data;
            $scope.UserProfile.name = data.name;
            $scope.UserProfile.email = data.email;
            $scope.UserProfile.timezone = data.timezone;
            $scope.UserProfile.email_hour = data.email_hour;
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

    $scope.resetPassword = user.resetPassword;

    /* watchers */
    $scope.$watch(function() {
        return $scope.loggedIn()
    }, function(oldValue, newValue) {
        if ($scope.loggedIn() == true) {
            $scope.getCurrentUser();
        }
    });

    $scope.$watch(function() {
        return getDefaultTeam()
    }, function(oldValue, newValue) {
        user.current_team = getDefaultTeam();
    });
}]);


/*
 * Service for authentication (login/logout)
 */
darg.service('auth', function($cookieStore, $http, $location) {
    this.login = function(params) {
        $http({
            method: "post",
            url: '/api/v1/login', 
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
    };

    this.logout = function() {
        $http({
            method: "get",
            url: "/api/v1/logout"
        })
        .success(function(data) {
            console.log('logged out.');
            $cookieStore.remove('logged-in');
            $location.path('/');
        })
    };

});

darg.service('role', function($http, $q) {
    /*
     * API
     */
    this.getTeamRoles = function(id) {
        url = "/api/v1/team/" + id + "/user";
        var deferred = $q.defer();
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data);
        })
        return deferred.promise;
    };

    this.getRole = function(team_id, user_id) {
        console.log("fetching role...");
        url = "/api/v1/team/" + team_id + "/user/" + user_id;
        var deferred = $q.defer();
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data);
        })
        return deferred.promise;
    };

    this.deleteRole = function(team_id, user_id) {
        console.log("deleting role...");
        url = "/api/v1/team/" + team_id + "/user/" + user_id;
        var deferred = $q.defer();
        $http({
            method: "delete",
            url: url
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data);
        })
        return deferred.promise;
    };

    this.createRole = function(team_id, params) {
        console.log("creating role...");
        url = "/api/v1/team/" + team_id + "/user"
        var deferred = $q.defer();
        $http({
            method: "post",
            url: url,
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data);
        })
        return deferred.promise;
    };

    /* 
     * Utility Functions
     */
    this.isAdmin = function(role) {
        if (role.admin == true) {
            return true;
        } else {
            return false;
        }
    }
});

darg.service('team', function($http, $location, $q) {

    this.createTeam = function(params) {
        $http({
            method: "post",
            url: "/api/v1/team",
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            url = "/team/" + data.id + "/timeline"
            $location.path(url);
        })
    };

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

darg.service('user', function($cookieStore, $http) {
    this.info = null;
    this.current_team = null;

    this.updateProfile = function(params) {
        url = "/api/v1/user/" + $cookieStore.get('id');
        $http({
            method: "post",
            url: url,
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
        })
    };

    this.resetPassword = function(params) {
        $http({
            method: "post",
            url: "/api/v1/password_reset",
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            // TODO: Provide a tooltip or something on success?
            // No, replace the entire speech bubble
        })
        .error(function(data) {
            console.log("Failed to reset password.");
        });
    };

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
