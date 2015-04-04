darg.controller('DargTaskCreationCtrl', 
    ['$scope', 
     '$http', 
     '$routeParams',
     '$cookies', 
     '$cookieStore', 
     'user',
     function(
         $scope, 
         $http, 
         $routeParams,
         $cookies, 
         $cookieStore, 
         user) {

    $scope.TaskForm = {
        "date": "",
        "task": "",
        "team_id": ""
    };

    $scope.PostTask = function(darg) {
        $scope.TaskForm.date = darg.date;
        $scope.TaskForm.team_id = $routeParams.teamId;
        $http({
            method: "post",
            url: "/api/v1/task",
            data: $.param($scope.TaskForm),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })
        .success(function(data) {
            console.log("Posted new task!");
            $scope.GetTimeline(user.current_team);
        })
    }

}]);
