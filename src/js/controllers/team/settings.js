darg.controller('DargTeamSettingsCtrl',
    [
    '$routeParams',
    '$scope',
    'team',
    function(
        $routeParams,
        $scope,
        team) {

    var self = this;
    $scope.teamPage = "settings";

    /*
     * Controller model
     */
    this.currentTeam = {};
    this.currentRole = {field: ""};

    /* 
     * Alerts
     */
    this.settingsUpdatedAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content};
    };

    /*
     * Utility functions
     */

    this.updateTeam = function(params) {
        team.updateTeam($routeParams.teamId, params).
            then(function(data) {
                console.log("success!");
                $scope.getCurrentUser();
                message = "Successfully updated!";
                self.setAlert(self.settingsUpdatedAlerts, message);
            }, function(data) {
                console.log(data);
            });
    };

    /*
     * watchers
     */

    $scope.$watch(function() {
        return team.currentTeam;
    }, function(newValue, oldValue) {
        if (newValue != {}) {
          self.currentTeam = newValue;
        }
    });
}]);
