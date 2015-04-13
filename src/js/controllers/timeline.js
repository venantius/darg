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

    /*
     * This is the date for the Datepicker. It has to be at $scope,
     * because of how the Angular UI folks wrote the datepicker. 
     * I loathe this.
     */
    $scope.date = new Date();

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

    $scope.GetTimeline = function(id) {
        if (id != null) {
            url = "/api/v1/darg/team/" + id
            $http({
                method: "get",
                url: url
            })
            .success(function(data) {
                url = "/team/" + id + "/timeline";
                $location.path(url);
                $scope.Timeline = data;
            })
            .error(function(data) {
                console.log("Failed to get timeline");
            });
        } else {
            console.log("Something is fucked");
        }
    };

    $scope.$watch(function() {
        return user.current_team
    }, function(oldValue, newValue) {
        if (user.current_team != null) {
            $scope.GetTimeline(user.current_team);
        }
    });
}]);
