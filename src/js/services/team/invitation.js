darg.service('teamInvitation', function($http, $q) {

    this.getToken = function(token) {
        url = "/api/v1/team/invitation/" + token
        $http({
            method: "get",
            url: url
        })
        .success(function(data) {
            console.log(data)
        })
        .error(function(data) {
            console.log(data) 
        })
    };
            
});
