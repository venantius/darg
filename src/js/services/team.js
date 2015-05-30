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

    this.isActive = function(ctrl, page) {
      console.log(page)
      if (page === "settings" && ctrl === "settings") {
        return "active"
      } else if (page === "members" && ctrl === "members") {
        return "active"
      } else if (page === "services" && ctrl === "services") {
        return "active"
      }
    };

});
