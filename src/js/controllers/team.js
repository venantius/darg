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

    this.creationForm = {
        name: "",
    };
    this.team = {};
    this.roles = {};
    this.currentRole = {field: ""};

    this.newRole = {
        email: "",
    };

    this.deleteRole = role.deleteRole;
    this.createRole = role.createRole;


    this.createTeam = function() {
        $http({
            method: "post",
            url: "/api/v1/team",
            data: $.param(this.creationForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            url = "/timeline/" + data.id
            $location.path(url);
        })
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

    /*
     * $watch section
     */
    var self = this;

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return $routeParams.teamId
    }, function(oldValue, newValue) {
        if (newValue != null) {
            team.getTeam(newValue)
            .then(function(data) {
                self.team = data;
            }, function(data) {
                console.log("Failed to update team.");
            });

            role.getTeamRoles(newValue)
            .then(function(data) {
                self.roles = data;
            }, function(data) {
                console.log("Failed to update roles.");
            });

            role.getRole(newValue, $cookieStore.get('id'))
            .then(function(data) {
                self.currentRole = data;
            }, function(data) {
                console.log(data);
            });
        }
    });

}]);
