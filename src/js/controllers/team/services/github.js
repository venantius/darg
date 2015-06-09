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

    self.github = github;

    /*
     * Data functions
     */

    /* 
     * Utility functions
     */


    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return team.currentTeam
    }, function(newValue, oldValue) {
        if (newValue.id != null) {
          self.currentTeam = newValue;

          github.fetchIntegration(newValue)
          .then(function(data) {
            self.github.settings = data;
          });
        }
    });
}]);

