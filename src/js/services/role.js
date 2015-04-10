darg.service('role', function($http, $q) {
    this.getTeamRoles = function(id) {
        url = "/api/v1/team/" + id + "/role";
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

    this.deleteTeamRole = function(team_id, role_id) {
        console.log("deleting role...");
        url = "/api/v1/team/" + team_id + "/role/" + role_id;
        var deferred = $q.defer();
        $http({
            method: "delete",
            url: url
        })
        .success(function(data) {
            deferred.resolve(data);
        })
        .error(function(data) {
            console.log(data);
        })
        return deferred.promise;
    };
});
