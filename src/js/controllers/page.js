darg.controller('DargPageCtrl', ['$scope', '$http', '$location', 
               function($scope, $http, $location) {
    $scope.header = "templates/header.html";
    $scope.footer = "templates/footer.html";

    $scope.inner = "templates/timeline.html";
    $scope.outer = "templates/outer.html";
   }
]);
