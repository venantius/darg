darg.controller('DargTeamServicesCtrl',
    [
    '$location',
    '$routeParams',
    '$scope',
    'github',
    'team',
    function(
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

    /* 
     * Service functions
     */

    self.addGitHubIntegration = function(team) {
      github.addGitHubIntegration(team)
      .then(function(data) {
        self.goToGitHubSettingsPage(team)
      }, function(data) {
        console.log(data) 
      }
    )};

    /*
     * Utility functions
     */

    self.hasGitHubIntegration = function(team) {
      return (team.github_team_settings != null &&
              Object.keys(team.github_team_settings).length > 0);
    };

    self.goToGitHubSettingsPage = function(team) {
      url = '/team/' + team.id + '/services/github'
      $location.path(url)
    };

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return team.currentTeam
    }, function(newValue, oldValue) {
        if (newValue != {}) {
          self.currentTeam = team.currentTeam;
        }
    });
}]);
