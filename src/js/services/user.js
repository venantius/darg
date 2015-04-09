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
