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
