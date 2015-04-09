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
