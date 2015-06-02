darg.controller('DargTeamMembersCtrl',
    [
    '$cookieStore',
    '$routeParams',
    '$scope',
    'role',
    'team',
    'user',
    function(
        $cookieStore,
        $routeParams,
        $scope,
        role,
        team,
        user) {
    var self = this;

    $scope.teamPage = "members";

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
    this.currentTeam = {};
    this.roles = {};
    this.currentRole = {field: ""};

    /* 
     * Alerts
     */
    this.invitationSuccessAlerts = [];
    this.invitationFailureAlerts = [];
    this.settingsUpdatedAlerts = [];
    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content};
    };

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


    this._refreshTeamData = function(team_id) {
        team.getTeam(team_id)
        .then(function(data) {
            self.currentTeam = data;
        }, function(data) {
            console.log(data);
        });
    };

    this._refreshRoleData = function(team_id) {
        role.getTeamRoles(team_id)
        .then(function(data) {
            self.roles = data;
        }, function(data) {
            console.log(data);
        });

        role.getRole(team_id, $cookieStore.get('id'))
        .then(function(data) {
            self.currentRole = data;
        }, function(data) {
            console.log(data);
        });
    };

    /* Delete a role, and update the model */
    this.deleteRole = function(team_id, user_id) {
        role.deleteRole(team_id, user_id)
        .then(function(data) {
            self._refreshRoleData(team_id);
        }, function(data) {
             console.log(data);
        });
    };

    this.updateRole = function(team_id, user_id, params) {
        console.log("Updating...");
        role.updateRole(team_id, user_id, params)
        .then(function(data) {
            console.log("Successfully updated!");
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

    /* Create a role, and update the model */
    this.createRole = function(team_id, params) {
        role.createRole(team_id, params)
        .then(function(data) {
            self._refreshRoleData(team_id);
            message = "Invitation sent to " + params.email + "!";
            self.setAlert(self.invitationSuccessAlerts, message);
        }, function(data) {
            console.log(data);
            self.setAlert(self.invitationFailureAlerts, data.message);
        });
    };

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
      return team.currentTeam
    }, function(newValue, oldValue) {
      if (Object.keys(newValue).length > 0) {
        self.currentTeam = newValue;
        self._refreshRoleData(newValue.id);
      }
    });
}]);
