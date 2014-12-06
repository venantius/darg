darg.controller('DargUserCtrl', ['$scope', '$http', '$routeParams',
               function($scope, $http, $routeParams) {
    
    $scope.CurrentUser = {};
    $scope.CurrentTeam = null;

    getDefaultTeam = function(user) {
        if (user.teams.length == 0) {
            return null; 
        } else {
            return user.teams[0].id;
        }
    };

    $scope.getCurrentUser = function() {
        $http({
            method: "get",
            url: "/api/v1/user"
        })
        .success(function(data) {
            $scope.CurrentUser = data;
            $scope.CurrentTeam = getDefaultTeam($scope.CurrentUser);
        })
        .error(function(data) {
            console.log(data);
        });
    };

    $scope.getCurrentUser();
}]);

