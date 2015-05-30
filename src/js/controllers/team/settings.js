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

    this.isActive = team.isActive;
    this.page = "settings";

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

    this._refreshTeamData = function(team_id) {
        team.getTeam(team_id)
        .then(function(data) {
            self.currentTeam = data;
        }, function(data) {
            console.log(data);
        });
    };

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

    this.x = "active"

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return $routeParams.teamId
    }, function(newValue, oldValue) {
        if (newValue != null) {
            self._refreshTeamData(newValue);
        }
    });
}]);
