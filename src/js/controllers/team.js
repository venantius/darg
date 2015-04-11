darg.controller('DargTeamCtrl',
    [
    '$cookies',
    '$cookieStore',
    '$http',
    '$location',
    '$routeParams',
    '$scope',
    'role',
    'team',
    'user',
    function(
        $cookies,
        $cookieStore,
        $http,
        $location,
        $routeParams,
        $scope,
        role,
        team,
        user) {
    var self = this;

    /* 
     * Forms
     */
    this.creationForm = {
        name: "",
    };
    this.newRole = {
        email: "",
    };

    /*
     * Controller model
     */
    this.team = {};
    this.roles = {};
    this.currentRole = {field: ""};

  $scope.today = function() {
    $scope.dt = new Date();
  };
  $scope.today();

  $scope.clear = function () {
    $scope.dt = null;
  };

  // Disable weekend selection
  $scope.disabled = function(date, mode) {
    return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
  };

  $scope.toggleMin = function() {
    $scope.minDate = $scope.minDate ? null : new Date();
  };
  $scope.toggleMin();

  $scope.open = function($event) {
    $event.preventDefault();
    $event.stopPropagation();

    $scope.opened = true;
  };

  $scope.dateOptions = {
    formatYear: 'yy',
    startingDay: 1
  };

  $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
  $scope.format = $scope.formats[0];

    /* 
     * Alerts
     */
    this.invitationSuccessAlerts = [];
    this.invitationFailureAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content};
    };

    /* 
     * Adding members to team
     */
    this.createRole = role.createRole;
    this.createTeam = team.createTeam;


    /*
     * Utility functions
     */

    /* Can we delete this user? */
    this.canDelete = function(target_user_id) {
        if (this.currentRole.admin == true || $cookieStore.get('id') == target_user_id) {
            return true;
        } else {
            return false
        }
    };


    /* 
     * Since this gets called by $scope.$watch
     */
    this._refreshTeamAndRoleData = function(team_id) {
        team.getTeam(team_id)
        .then(function(data) {
            self.team = data;
        }, function(data) {
            console.log("Failed to update team.");
        });

        role.getTeamRoles(team_id)
        .then(function(data) {
            self.roles = data;
        }, function(data) {
            console.log("Failed to update roles.");
        });

        role.getRole(team_id, $cookieStore.get('id'))
        .then(function(data) {
            self.currentRole = data;
        }, function(data) {
            console.log(data);
        });
    }

    /* Delete a role, and update the model */
    this.deleteRole = function(team_id, user_id) {
        role.deleteRole(team_id, user_id)
        .then(function(data) {
            self._refreshTeamAndRoleData(team_id);
        }, function(data) {
             console.log(data);
        });
    };

    /* Create a role, and update the model */
    this.createRole = function(team_id, params) {
        role.createRole(team_id, params)
        .then(function(data) {
            self._refreshTeamAndRoleData(team_id);
            message = "Invitation sent to " + params.email + "!";
            self.setAlert(self.invitationSuccessAlerts, message);
        }, function(data) {
            console.log(data);
            self.setAlert(self.invitationFailureAlerts, data.message);
        });
    };

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return $routeParams.teamId
    }, function(oldValue, newValue) {
        if (newValue != null) {
            console.log("refreshing...");
            self._refreshTeamAndRoleData(newValue);
        }
    });



}]);
