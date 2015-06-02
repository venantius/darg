darg.service('team', function($http, $location, $q) {

  var self = this;

  self.currentTeam = {};

  self.createTeam = function(params) {
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

  self.getTeam = function(id) {
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

  self.updateTeam = function(id, params) {
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

  self.refreshTeamData = function(team_id) {
    self.getTeam(team_id)
    .then(function(data) {
      self.currentTeam = data;
    }, function(data) {
      console.log(data);
    });
  };

});
