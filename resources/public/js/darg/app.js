var darg = angular.module('darg', 
                          ['ngCookies', 
                           'ngRoute', 
                           'ngLoadScript',
                           'ui.bootstrap',
                           'ui.gravatar'
                          ]);

darg.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        // Outer
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

        // Password reset flow
        .when('/password_reset', {
            templateUrl: '/templates/password_reset.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
        })
        .when('/new_password', {
            tempalteUrl: '/templates/new_password.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
        })

        // Signup
        .when('/signup', {
            templateUrl: '/templates/signup.html',
            controller: 'DargSignupCtrl',
            controllerAs: 'Signup'
        })

        // Inner
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
        .when('/team/:teamId/timeline/:date', {
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

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
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

darg.controller('DargSignupCtrl', 
    ['$scope', 
     '$http',
     '$location',
     function($scope, $http, $location) {

    this.SignupForm = {
        name: "",
        email: "",
        password: "",
        token: $location.search().token
    };

    this.Signup = function() {
        $http({
            method: "post",
            url: '/api/v1/user', 
            data: $.param(this.SignupForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            $location.search('token', null);
            $location.path('/');
        })
        .error(function(data) {
            console.log(data)
        });
    };
}]);

darg.controller('DargTaskCtrl', 
    [function() {
    
    this.task = null;

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
    this.currentTeam = {};
    this.roles = {};
    this.currentRole = {field: ""};

    /* 
     * Alerts
     */
    this.invitationSuccessAlerts = [];
    this.invitationFailureAlerts = [];
    this.settingsUpdatedAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content};
    };

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
            self.currentTeam = data;
        }, function(data) {
            console.log(data);
        });
    };

    this._refreshRoleData = function(team_id) {
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
    };

    /* Delete a role, and update the model */
    this.deleteRole = function(team_id, user_id) {
        role.deleteRole(team_id, user_id)
        .then(function(data) {
            self._refreshRoleData(team_id);
        }, function(data) {
             console.log(data);
        });
    };

    this.updateRole = function(team_id, user_id, params) {
        console.log("Updating...");
        role.updateRole(team_id, user_id, params)
        .then(function(data) {
            console.log("Successfully updated!");
        }, function(data) {
            console.log(data);
        });
    };

    this.createTeam = function(params) {
        team.createTeam(params).
            then(function(data) {
                user.getCurrentUser()
                .then(function(data) {
                    user.info = data;
                }, function(data) {
                    console.log(data)
                });
            }, function(data) {
                console.log(data)
            });
    }

    this.updateTeam = function(params) {
        team.updateTeam($routeParams.teamId, params).
            then(function(data) {
                console.log("success!");
                $scope.getCurrentUser();
                message = "Successfully updated!";
                self.setAlert(self.settingsUpdatedAlerts, message);
            }, function(data) {
                console.log(data);
            });
    };

    /* Create a role, and update the model */
    this.createRole = function(team_id, params) {
        role.createRole(team_id, params)
        .then(function(data) {
            self._refreshRoleData(team_id);
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
    }, function(newValue, oldValue) {
        if (newValue != null) {
            self._refreshTeamData(newValue);
            self._refreshRoleData(newValue);
        }
    });
}]);

darg.controller('DargTimelineCtrl', 
    ['$http',
     '$location',
     '$routeParams',
     '$scope',
     'datepicker',
     'task',
     'timeline',
     'user',
     function(
         $http,
         $location,
         $routeParams,
         $scope,
         datepicker,
         task,
         timeline,
         user) {
    
    var self = this;

    $scope.formatDateString = function(date) {
        return moment(date)._d;
    }

    /*
     * This is for the Datepicker. It has to be at $scope,
     * because of how the Angular UI folks wrote the datepicker. 
     */
    this.show = datepicker.show;

    this.setDate = function() {
        if ($routeParams.date != null) {
            $scope.date = moment($routeParams.date)._d;
        } else {
            $scope.date = new Date();
        }
    }
    this.setDate();

    $scope.open = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened = true;
    };
    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1,
        maxMode: 'day'
    };

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

    this._refreshTimeline = function () {
        timeline.getTimeline($routeParams.teamId, $routeParams.date)
        .then(function(data) {
            self.events = data;
        }, function(data) {
            console.log(data) 
        });
    }

    this.postTask = function(date, taskString) {
        var params = {
            "date": date,
            "team_id": $routeParams.teamId,
            "task": taskString
        }
        task.createTask(params)
        .then(function(data) {
            self._refreshTimeline();
        }, function(data) {
            console.log(data)
        });
    }

    $scope.$watch(function() {
        return $routeParams.teamId;
    }, function(newValue, oldValue) {
        self._refreshTimeline();
    });

    $scope.$watch(function() {
        return $scope.date;
    }, function(newValue, oldValue) {
        if (newValue != null) {
            date = moment(newValue).format('YYYY-MM-DD');
            url = "/team/" + $routeParams.teamId
                + "/timeline/" + date;
            $location.url(url);
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
        $scope.currentUser = newValue;
    });

    $scope.$watch(function() {
        return getDefaultTeam()
    }, function(newValue, oldValue) {
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
        .success(function(data) {
            $location.path('/');
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

darg.service('datepicker', function() {
    this.show = true;
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

    this.updateRole = function(team_id, user_id, params) {
        console.log("updating role...");
        console.log(params);
        url = "/api/v1/team/" + team_id + "/user/" + user_id;
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

darg.service('task', function($http, $q) {

    this.createTask = function(params) {
        var deferred = $q.defer();
        $http({
            method: "post",
            url: "/api/v1/task",
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data);
        });
        return deferred.promise;
    };

});

darg.service('team', function($http, $location, $q) {

    this.createTeam = function(params) {
        var deferred = $q.defer();
        $http({
            method: "post",
            url: "/api/v1/team",
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            url = "/team/" + data.id + "/timeline"
            $location.path(url);
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data);
        })
        return deferred.promise;
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
        .error(function(data) {
            deferred.reject(data);
        })
        return deferred.promise;
    };

    this.updateTeam = function(id, params) {
        url = "/api/v1/team/" + id;
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
});

darg.service('teamInvitation', function($http, $q) {

    this.getToken = function(token) {
        url = "/api/v1/team/invitation/" + token
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            console.log(data)
        })
        .error(function(data) {
            console.log(data) 
        })
    };
            
});

darg.service('timeline', function($http, $q) {
    this.getTimeline = function(team_id, date) {
        url = "/api/v1/darg/team/" + team_id
        if (date != null) {
            url += "/" + date 
        };
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
        });
        return deferred.promise;
    };
});

darg.service('user', function($cookieStore, $http, $q) {
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
        var deferred = $q.defer();
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

    this.getCurrentUser = function() {
        var deferred = $q.defer();
        url = "/api/v1/user/" + $cookieStore.get('id');
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
    }
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
