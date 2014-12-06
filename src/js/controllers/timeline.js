darg.controller('DargTimelineCtrl', ['$scope', '$http', '$cookies', '$cookieStore', 'user',
               function($scope, $http, $cookies, $cookieStore, user) {

    /*
     * This is totally an anti-pattern but I'm just learning how services work.
     */
    $scope.loggedIn = user.loggedIn;
    $scope.formatDateString = function(date) {
        return Date.parse(date);
    }

    $scope.GetTimeline = function() {
        if ($scope.CurrentTeam == null) {
            console.log("Null!");
        } else if ($scope.CurrentTeam != null) {
            $http({
                method: "get",
                url: "/api/v1/darg/1"
            })
            .success(function(data) {
                $scope.Timeline = data;
            })
            .error(function(data) {
                $scope.Timeline = "Error: failed to retrieve timeline";
                console.log("Failed to retrieve timeline");
                console.log(data);
            });
        };
    };

    $scope.TaskForm = {
        "task": ""
    };

    $scope.PostTask = function() {
        $http({
            method: "post",
            url: "/api/v1/task",
            data: $.param($scope.TaskForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            console.log("Success!");
        })
        .error(function(data) {
            console.log("Failure!");
        });
    }


    // I am ashamed to say that this took me way longer to figure out than
    // it should have :(
    $scope.$watch('loggedIn()', function(oldValue, newValue) {
        console.log($scope.CurrentTeam);
        if ($scope.loggedIn() == true && $scope.CurrentTeam != null) {
            $scope.GetTimeline();
        }
    });

}]);

