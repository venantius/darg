darg.controller('DargTeamCtrl',
    [
    '$cookies',
    '$cookieStore',
    '$http',
    '$location',
    '$routeParams',
    '$scope',
    'team',
    'user',
    function(
        $cookies,
        $cookieStore,
        $http,
        $location,
        $routeParams,
        $scope,
        team,
        user) {

    this.creationForm = {
        name: "",
    };
    this.team = {}

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

    /* $watch section*/
    var self = this;

    /* Watch what team we should be looking at */
    $scope.$watch(function() {
        return $routeParams.teamId
    }, function(oldValue, newValue) {
        if (newValue != null) {
            team.getTeam(newValue)
            .then(function(data) {
                self.team = data
            }, function(data) {
                console.log("Failed to update team.");
            });
        }
    });
}]);
