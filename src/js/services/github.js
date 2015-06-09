darg.service('github', function($http, $q, $window) {

  var self = this;

  self.settings = {};

  self.hasIntegration = function(team) {
    return (team.services != null && 
            team.services.github == true)
  };

  self.hasAuth = function(gh_settings) {
    return (gh_settings.login != null)
  };

  self.oauth = function(team) {
    url = "/oauth/github/login/" + team.id
    console.log(url);
    $window.location.href = url;
  };

  self.createIntegration = function(team) {
    var deferred = $q.defer();
    url = "/api/v1/team/" + team.id + "/services"
    params = {"type": "github"}
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
    });
    return deferred.promise;
  };

  self.fetchIntegration = function(team) {
    var deferred = $q.defer();
    url = "/api/v1/team/" + team.id + "/services/github"
    $http({
      method: "get",
      url: url
    })
    .success(function(data) {
      deferred.resolve(data);
    })
    .error(function(data) {
      console.log(data);
      deferred.reject(data);
    });
    return deferred.promise;
  };

  self.updateIntegration = function(team, params) {
    var deferred = $q.defer();
    url = "/api/v1/team/" + team.id + "/services/github"
    $http({
      method: "patch",
      url: url,
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

  self.removeAuth = function(team) {
    params = {"access_token_id": null}
    self.updateIntegration(team, params)
    self.settings.login = null;
    self.settings.access_token_id = null;
  }

  self.getUsersRepoList = function() {
    var deferred = $q.defer();
    url = "/api/v1/user/services/github/repos"
    $http({
      method: "get",
      url: url
    })
    .success(function(data) {
      deferred.resolve(data)
    })
    .error(function(data) {
      deferred.reject(data)
    })
    return deferred.promise;
  };

});
