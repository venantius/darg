/*
 * Shared data and functions for the current user
 */
darg.service('user', function($cookieStore, $http, $q) {
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
            deferred.resolve(data);
        })
        .error(function(data) {
            deferred.reject(data) 
        })
        return deferred.promise;
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
