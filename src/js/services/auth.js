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

});
