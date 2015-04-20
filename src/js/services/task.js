darg.service('task', function($http, $q) {

    this.createTask = function(params) {
        var deferred = $q.defer();
        $http({
            method: "post",
            url: "/api/v1/task",
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

});
