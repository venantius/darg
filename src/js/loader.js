/* Our Fallback.js loader */

fallback.load({

  "moment": ['https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.2/moment-with-locales.min.js',
             '/js/moment/moment-with-locales.min.js'],
  "moment.tz": ['https://cdnjs.cloudflare.com/ajax/libs/moment-timezone/0.3.1/moment-timezone.min.js',
                '/js/moment/moment-timezone-with-data.min.js'],

                jQuery: ['https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js',
                         '/js/flatui/jquery.min.js'],

  "angular": ['/js/angular/angular.min.js'],
  "angular.module('ngCookies')": 
    ['/js/angular/angular-cookies.min.js'],
  "angular.module('ngResource')":
    ['/js/angular/angular-resource.min.js'],
  "angular.module('ngRoute')":
    ['/js/angular/angular-route.min.js'],

  "angular.module('ui.gravatar')":
    ['/js/angular/angular-gravatar.min.js'],
  // TODO: maybe have to fix this
  "angular.module('ui.bootstrap')":
    ['/js/angular/ui-bootstrap.min.js'],

  "angular.module('darg')":
    ['/js/darg/app.js'],

}, {
  shim: {
    'moment.tz': ['moment'],
    "angular": ['jQuery'],
    "angular.module('ngCookies')": ['angular'],
    "angular.module('ngResource')": ['angular'],
    "angular.module('ngRoute')": ['angular'],
    "angular.module('ui.gravatar')": ['angular'],
    "angular.module('ui.bootstrap')": ['angular'],
    "angular.module('darg')": 
      [
        "angular",
        "angular.module('ngCookies')",
        "angular.module('ngResource')",
        "angular.module('ngRoute')",
        "angular.module('ui.gravatar')",
        "angular.module('ui.bootstrap')",
      ]
  }, 

  callback: function(success, failed) {
    flatui = document.createElement('script');
    flatui.src = "/js/flatui/flat-ui-pro.min.js";
    flatuiapp = document.createElement('script');
    flatuiapp.src = "/js/flatui/application.js";
    document.getElementsByTagName('head')[0].appendChild(flatui);
    document.getElementsByTagName('head')[0].appendChild(flatuiapp);
  }
});
