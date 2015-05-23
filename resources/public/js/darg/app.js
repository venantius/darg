var darg = angular.module('darg', 
                          ['angulartics',
                           'angulartics.google.analytics',
                           'ngCookies', 
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
        .when('/pricing', {
            templateUrl: '/templates/pricing.html'
        })

        // Password reset flow
        .when('/password_reset', {
            templateUrl: '/templates/password_reset.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
        })
        .when('/new_password', {
            templateUrl: '/templates/new_password.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
        })

        // Signup and Login
        .when('/signup', {
            templateUrl: '/templates/signup.html',
            controller: 'DargSignupCtrl',
            controllerAs: 'Signup'
        })
        .when('/login', {
            templateUrl: '/templates/login.html'
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

darg.controller('DargAlertCtrl',
    ['$location',
     '$scope',
     'alert',
     'user',
     function(
         $location,
         $scope,
         alert,
         user) {

    this.alerts = alert;

    /*
     * watchers
     */
    $scope.$watch(function() {
        return $location.search().failed_login;
    }, function(newValue, oldValue) {
        if (newValue != null) {
            alert.setAlert(alert.failedLoginAlerts,
                           alert.failedLoginMessage,
                          "dialog-danger");
        } else {
            alert.failedLoginAlerts = [];
        }
    });

    $scope.$watch(function() {
      return $location.path();
    }, function(newValue, oldValue) {
      if (oldValue == "/login" && 
          newValue != "/login") {
        alert.failedLoginAlerts = [];
        $location.search('failed_login', null);
      }
      if (oldValue == "/password_reset" && 
          newValue != "/password_reset") {
        alert.passwordResetAlerts = [];
      }
    });

    $scope.$watch(function() {
        return user.info.confirmed_email
    }, function(newValue, oldValue) {
        if (newValue == false) {
            alert.setAlert(alert.emailConfirmationAlerts,
                           alert.emailConfirmationMessage);
        } else {
            alert.emailConfirmationAlerts = []
        }
    });

 }]);

darg.controller('DargPasswordResetCtrl',
    ['$location',
     '$scope',
     'alert',
     'auth',
     function(
       $location,
       $scope,
       alert,
       auth) {
    var self = this;

    this.auth = auth;

    $scope.ResetForm = {
        "email": ""
    }

    $scope.resetPassword = function(params) {
      auth.resetPassword(params)
      .then(function(data) {
        console.log("success!")
        alert.setAlert(alert.passwordResetAlerts,
                       alert.passwordResetSuccessMessage,
                      "dialog-success");
      }, function(data) {
        alert.setAlert(alert.passwordResetAlerts,
                       data.message,
                       "dialog-danger");
        console.log(data)
      })
    };

    this.setNewPassword = function(params) {
      auth.setNewPassword(params)
      .then(function(data) {
        $location.path('/');
        $location.search('token', null);
      }, function(data) {
        console.log(data);
      })
    };

    this.passwordResetForm = {
      "password": "",
      "confirm_password": ""
    };

    $scope.$watch(function() {
      return $location.search().token
    }, function(newValue, oldValue) {
      if (newValue != null) {
        self.passwordResetForm.token = newValue
      }
    });
}]);

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

    this.goToLoginPage = function() {
      $location.path('/login');
    }
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
     'team',
     'timeline',
     'user',
     function(
         $http,
         $location,
         $routeParams,
         $scope,
         datepicker,
         task,
         team,
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
        
        if ($scope.opened != true ) {
          $scope.opened = true;
        } else {
          $scope.opened = false;
        }
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
    this.teamId = $routeParams.teamId;

    this.team = team.currentTeam;

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
            "timestamp": new Date().toISOString(),
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

    this.getIcon = function(task) {
      if (task.type == "email") {
        return "fa fa-envelope-o"
      } else if (task.type == "task") {
        return "fa fa-check"
      }
    };

    /*
     * watchers
     */

    $scope.$watch(function() {
        return $routeParams.teamId;
    }, function(newValue, oldValue) {
      if ($routeParams.date != null) {
        self._refreshTimeline();
      }
      team.getTeam(self.teamId)
      .then(function(data) {
        self.currentTeam = data;
      }, function(data) {
        console.log(data) 
      });
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
     'alert',
     'auth',
     'intercom',
     'user',
     function(
         $cookieStore,
         $location,
         $scope, 
         $http, 
         $routeParams,
         alert,
         auth,
         intercom,
         user) {

    var self = this;
    this.user = user;

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

    $scope.LoadPasswordResetPage = function() {
        $location.path('/password_reset');
        $location.search('failed_login', null);
    };

    $scope.getCurrentUser = function() {
        user.getCurrentUser()
        .then(function(data) {
            user.info = data;
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

    $scope.goToSignupPage = function() {
        $location.path('/signup');
    };

    this.sendEmailConfirmation = user.sendEmailConfirmation;

    /* Watch for the changes we need to redirect someone from the homepage */
    this.homeRedirector = function() {
      if ($location.path() == "/") {
        if ($scope.loggedIn() == true ) {
          if (user.info.name != null) {
            return 3;
          } else {
            return 2;
          }
        } else {
          return 1;
        }
      } else {
        return 0;
      }
    };

    /* watchers */
    $scope.$watch(function() {
        return $scope.loggedIn()
    }, function(newValue, oldValue) {
        if (newValue == true) {
            $scope.getCurrentUser();
        }
    });

    $scope.$watch(function() {
        return user.info
    }, function(newValue, oldValue) {
        if (newValue != null) {
            $scope.currentUser = newValue;
        }
    });

    $scope.$watch(function() {
      return self.homeRedirector();
    }, function(newValue, oldValue) {
      if (newValue == 3) {
        intercom.notify(user.info)
        if ($location.path() == "/") {
          if (user.info.team.length > 0) {
            url = '/team/' + user.info.team[0].id + '/timeline';
          } else {
            url = '/team'
          }
          $location.path(url);
        }
      }
    });

    $scope.$watch(function() {
      return $location.path()
    }, function(newValue, oldValue) {
      intercom.update()
    });
}]);


/*
 * Alerts
 */
darg.service('alert', function() {

    this.setAlert = function(alert_list, alert_content, alert_class) {
        alert_list[0] = {
            msg: alert_content,
            class: alert_class
        }
    };

    this.emailConfirmationAlerts = [];
    this.emailConfirmationMessage = "We've e-mailed you with a link to confirm your e-mail address. Didn't get it?";

    this.failedLoginAlerts = [];
    this.failedLoginMessage = "Incorrect e-mail or password.";

    this.passwordResetAlerts = [];
    this.passwordResetSuccessMessage = "Password reset e-mail sent!";

});

/*
 * Service for authentication (login/logout)
 */
darg.service('auth', function($cookieStore, $http, $location, $q) {
    this.login = function(params) {
        $http({
            method: "post",
            url: '/api/v1/login', 
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
          if ($location.search().redirect != null) {
            $location.path($location.search().redirect);
            $location.search('redirect', null);
          } else {
            $location.path('/');
          }
        })
        .error(function(data) {
            $location.path('/login');
            $location.search('failed_login', 'true');
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

    this.resetPassword = function(params) {
        var deferred = $q.defer();
        $http({
            method: "post",
            url: "/api/v1/password_reset",
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

    this.setNewPassword = function(params) {
      var deferred = $q.defer();
      $http({
        method: "post",
        url: "/api/v1/new_password",
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

darg.service('datepicker', function() {
    this.show = true;
});

/*
 * Service for intercom (login/logout)
 */
darg.service('intercom', function($cookieStore, $http, $location) {
  this.notify = function(user) {
    window.Intercom('boot', {
      app_id: "pt2u9jve",
      name: user.name,
      user_id: user.id,
      email: user.email,
      created_at: Date.parse(user.created_at) / 1000
    });
  };

  this.update = function() {
    window.Intercom('update');
  }

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
    params.type = "task";
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

/*
 * Shared data and functions for the current user
 */
darg.service('user', function($cookieStore, $http, $q) {
  var self = this;
    this.info = {
        "confirmed_email": null,
        "team": {}
    };
    this.current_team = null;

    this.updateProfile = function(params) {
        var deferred = $q.defer();
        console.log("updating user profile...");
        console.log(params);
        url = "/api/v1/user/" + $cookieStore.get('id');
        $http({
            method: "post",
            url: url,
            data: $.param(params),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
          self.info.confirmed_email = data.confirmed_email;
          deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data) 
        })
        return deferred.promise;
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

    this.confirmEmail = function(token) {
        var deferred = $q.defer();
        url = "/api/v1/user/" + $cookieStore.get('id') + "/email/" + token
        $http({
            method: "post",
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

    this.sendEmailConfirmation = function() {
        var deferred = $q.defer();
        url = "/api/v1/user/" + $cookieStore.get('id') + "/email"
        $http({
            method: "post",
            url: url
        })
        .success(function(data) {
            console.log(data);
            deferred.resolve(data);
        })
        .error(function(data) {
            console.log(data);
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
