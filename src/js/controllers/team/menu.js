darg.controller('DargTeamSettingsMenuCtrl',
    [
      '$routeParams',
      '$scope',
      'team',
      function(
        $routeParams,
        $scope,
        team) {

    var self = this;

    this.isActive = function(ctrl, page) {
      if (page === "settings" && ctrl === "settings") {
        return "active"
      } else if (page === "members" && ctrl === "members") {
        return "active"
      } else if (page === "services" && ctrl === "services") {
        return "active"
      }
    };

    /*
     * watchers
     */

    $scope.$watch(function() {
      return team.currentTeam
    }, function(newValue, oldValue) {
      if (Object.keys(newValue).length > 0) {
        self.currentTeam = newValue;
      }
    });

    $scope.$watch(function() {
      return $routeParams.teamId
    }, function(newValue, oldValue) {
      if (newValue != null) {
        team.refreshTeamData(newValue);
      }
    });

}]);
