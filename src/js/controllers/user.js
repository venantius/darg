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

    $scope.ResetForm = {
        "email": ""
    }

    $scope.LoadPasswordResetPage = function() {
        $location.path('/password_reset');
        $location.search('failed_login', null);
    };

    $scope.resetPassword = user.resetPassword;

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

