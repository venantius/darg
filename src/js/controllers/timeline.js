darg.controller('DargTimelineCtrl', 
    ['$cookies',
     '$cookieStore',
     '$http', 
     '$location',
     '$routeParams',
     '$scope', 
     'user',
     function(
         $cookies, 
         $cookieStore, 
         $http,
         $location,
         $routeParams,
         $scope, 
         user) {

    $scope.formatDateString = function(date) {
        return Date.parse(date);
    }

    $scope.GetTimeline = function() {
        if (user.team != null) {
            url = "/api/v1/darg/" + user.team
            $http({
                method: "get",
                url: url
            })
            .success(function(data) {
                $scope.Timeline = data;
            })
            .error(function(data) {
                console.log("Failed to get timeline");
            });
        } else {
            console.log("Something is fucked");
        }
    };

    $scope.loadNewTeamTimeline = function(id) {
        url = "/timeline/" + id;
        $location.path(url);
    }

    $scope.$watch(function() {
        return user.team
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true && user.team != null) {
            $scope.GetTimeline();
        }
    });

}]);

