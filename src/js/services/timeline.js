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
