darg.controller('DargTeamGitHubCtrl',
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
    self.repos = {};

    self.oauthWithGitHub = github.oauthWithGitHub;

    /* 
     * Utility functions
     */

    self.isAuthenticated = function() {
    }; // TODO

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return team.currentTeam
    }, function(newValue, oldValue) {
        if (newValue != {}) {
          self.currentTeam = team.currentTeam;
        }
    });
}]);

