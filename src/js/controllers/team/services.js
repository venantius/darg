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
      github.createIntegration(team)
      .then(function(data) {
        self.goToGitHubSettingsPage(team)
      }, function(data) {
        console.log(data) 
      }
    )};

    /*
     * Utility functions
     */

    self.github = github;

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
