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

    /* 
     * Display this user if they have content, or if they're the currently
     * logged-in user 
     */
    $scope.showUser = function(user) {
        if ($scope.userHasTasks(user) || $scope.isCurrentUser(user.id)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Does this user have any tasks listed for today?
     */
    $scope.userHasTasks = function(user) {
        if (user.task.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    $scope.GetTimeline = function() {
        if (user.current_team != null) {
            url = "/api/v1/darg/team/" + user.current_team
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
        return user.current_team
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true && user.current_team != null) {
            console.log("CURRENT TEAM");
            console.log(user.current_team);
            $scope.loadNewTeamTimeline(user.current_team);
            $scope.GetTimeline();
        }
    });
}]);
