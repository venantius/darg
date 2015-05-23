darg.controller('DargTimelineCtrl', 
    ['$http',
     '$location',
     '$routeParams',
     '$scope',
     'datepicker',
     'task',
     'team',
     'timeline',
     'user',
     function(
         $http,
         $location,
         $routeParams,
         $scope,
         datepicker,
         task,
         team,
         timeline,
         user) {
    
    var self = this;

    $scope.formatDateString = function(date) {
        return moment(date)._d;
    }

    /*
     * This is for the Datepicker. It has to be at $scope,
     * because of how the Angular UI folks wrote the datepicker. 
     */
    this.show = datepicker.show;

    this.setDate = function() {
        if ($routeParams.date != null) {
            $scope.date = moment($routeParams.date)._d;
        } else {
            $scope.date = new Date();
        }
    }
    this.setDate();

    $scope.open = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        
        if ($scope.opened != true ) {
          $scope.opened = true;
        } else {
          $scope.opened = false;
        }
    };
    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1,
        maxMode: 'day'
    };

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
    this.teamId = $routeParams.teamId;

    this.team = team.currentTeam;

    this._refreshTimeline = function () {
        timeline.getTimeline($routeParams.teamId, $routeParams.date)
        .then(function(data) {
            self.events = data;
        }, function(data) {
            console.log(data) 
        });
    }

    this.postTask = function(date, taskString) {
        var params = {
            "date": date,
            "timestamp": new Date().toISOString(),
            "team_id": $routeParams.teamId,
            "task": taskString
        }
        task.createTask(params)
        .then(function(data) {
            self._refreshTimeline();
        }, function(data) {
            console.log(data)
        });
    }

    this.getIcon = function(task) {
      if (task.type == "email") {
        return "fa fa-envelope-o"
      } else if (task.type == "task") {
        return "fa fa-check"
      }
    };

    /*
     * watchers
     */

    $scope.$watch(function() {
        return $routeParams.teamId;
    }, function(newValue, oldValue) {
      if ($routeParams.date != null) {
        self._refreshTimeline();
      }
      team.getTeam(self.teamId)
      .then(function(data) {
        self.currentTeam = data;
      }, function(data) {
        console.log(data) 
      });
    });

    $scope.$watch(function() {
        return $scope.date;
    }, function(newValue, oldValue) {
        if (newValue != null) {
            date = moment(newValue).format('YYYY-MM-DD');
            url = "/team/" + $routeParams.teamId
                + "/timeline/" + date;
            $location.url(url);
        }
    });
}]);
