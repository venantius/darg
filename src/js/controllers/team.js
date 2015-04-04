darg.controller('DargTeamCtrl',
    [
    '$cookies',
    '$cookieStore',
    '$http',
    '$location',
    '$scope',
    'user',
    function(
        $cookies,
        $cookieStore,
        $http,
        $location,
        $scope,
        user) {

    this.creationForm = {
        name: "",
    };

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

}]);


