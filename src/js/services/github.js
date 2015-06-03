darg.service('github', function($http, $q, $window) {

  var self = this;

  self.oauthWithGitHub = function(team_id) {
    url = "/oauth/github/login/" + team_id
    console.log(url);
    $window.location.href = url;
  };

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
