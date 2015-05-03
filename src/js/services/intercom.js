/*
 * Service for intercom (login/logout)
 */
darg.service('intercom', function($cookieStore, $http, $location) {
  this.notify = function(user) {
    window.Intercom('boot', {
      app_id: "pt2u9jve",
      name: user.name,
      email: user.email,
      // TODO: The current logged in user's sign-up date as a Unix timestamp.
      created_at: Date.parse(user.created_at) / 1000
    });
  };

  this.update = function() {
    window.Intercom('update');
  }

});
