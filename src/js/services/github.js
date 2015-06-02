darg.service('github', function($http, $q, $window) {

  var self = this;

  self.oauthWithGitHub = function() {
    $window.location.href = "/oauth/github/login";
  };
});
