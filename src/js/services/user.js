darg.factory('user', function($cookieStore) {
    var service = {};
    var info = null;
    var team = null;

    service.loggedIn = function() {
        if ($cookieStore.get('logged-in') == true) {
            return true;
        } else {
            return false;
        }
    };

    return service;
});
