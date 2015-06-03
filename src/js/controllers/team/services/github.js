darg.controller('DargTeamGitHubCtrl',
    [
    '$cookieStore',
    '$location',
    '$routeParams',
    '$scope',
    'github',
    'team',
    function(
        $cookieStore,
        $location,
        $routeParams,
        $scope,
        github,
        team) {

    var self = this;
    $scope.teamPage = "services";

    /*
     * Controller model
     */
    self.currentTeam = {};

    self.userRepos = {};

    github.getUsersRepoList()
    .then(function(data) {
      self.userRepos = data;
    }, function(data) {
      console.log(data)
    });

    self.oauthWithGitHub = github.oauthWithGitHub;

    /*
     * Data functions
     */

    /* 
     * Utility functions
     */

    self.isAuthenticated = function() {
      return ($cookieStore.get('github') == true)
    }

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return team.currentTeam
    }, function(newValue, oldValue) {
        if (newValue != {}) {
          self.currentTeam = team.currentTeam;
        }
    });
}]);

