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
    this.currentTeam = {};
    this.roles = {};
    this.currentRole = {field: ""};

    /* 
     * Alerts
     */
    this.invitationSuccessAlerts = [];
    this.invitationFailureAlerts = [];
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

    this.createTeam = function(params) {
        team.createTeam(params).
            then(function(data) {
                user.getCurrentUser()
                .then(function(data) {
                    user.info = data;
                }, function(data) {
                    console.log(data)
                });
            }, function(data) {
                console.log(data)
            });
    }

    this.updateTeam = function(params) {
        team.updateTeam($routeParams.teamId, params).
            then(function(data) {
                console.log("success!");
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
        return $routeParams.teamId
    }, function(newValue, oldValue) {
        if (newValue != null) {
            self._refreshTeamData(newValue);
            self._refreshRoleData(newValue);
        }
    });
}]);
