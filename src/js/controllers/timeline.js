darg.controller('DargTimelineCtrl', 
    ['$scope', 
     '$http', 
     '$cookies', 
     '$cookieStore', 
     'user',
     function(
         $scope, 
         $http, 
         $cookies, 
         $cookieStore, 
         user) {

    $scope.formatDateString = function(date) {
        return Date.parse(date);
    }

    $scope.GetTimeline = function() {
        $http({
            method: "get",
            url: "/api/v1/darg/1"
        })
        .success(function(data) {
            $scope.Timeline = data;
            console.log("Succeeded");
        })
        .error(function(data) {
            console.log("Failed to get timeline");
        });
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
    }

    $scope.$watch(function() {
        return (user.team != null && user.loggedIn())
    }, function(oldValue, newValue) {
        if (user.loggedIn() == true && user.team != null) {
            $scope.GetTimeline();
        }
    });

}]);

