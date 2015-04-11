darg.service('role', function($cookieStore, $http, $route, $q) {
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
