/* Our Fallback.js loader */

fallback.load({

  "moment": 
    ['https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.2/moment-with-locales.min.js',
     '/js/moment/moment-with-locales.min.js'],
  "moment.tz": 
    ['https://cdnjs.cloudflare.com/ajax/libs/moment-timezone/0.3.1/moment-timezone.min.js',
     '/js/moment/moment-timezone-with-data.min.js'],

  jQuery: 
    ['https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js',
     '/js/flatui/jquery.min.js'],

  "angular": 
    ['https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.2.28/angular.min.js',
     '/js/angular/angular.min.js'],
  "angular.module('ngCookies')": 
    ['https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.2.28/angular-cookies.min.js',
     '/js/angular/angular-cookies.min.js'],
  "angular.module('ngResource')":
    ['https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.2.28/angular-resource.min.js',
     '/js/angular/angular-resource.min.js'],
  "angular.module('ngRoute')":
    ['https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.2.28/angular-route.min.js',
     '/js/angular/angular-route.min.js'],

  "angular.module('ui.gravatar')":
    ['/js/angular/angular-gravatar.min.js'],
  // TODO: maybe have to fix this
  "angular.module('ui.bootstrap')":
    ['https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.13.0/ui-bootstrap.min.js',
     '/js/angular/ui-bootstrap.min.js'],
  "angular.module('angulartics')":
    ['//cdnjs.cloudflare.com/ajax/libs/angulartics/0.17.2/angulartics.min.js'],
  "angular.module('angulartics.google.analytics')":
    ['https://cdnjs.cloudflare.com/ajax/libs/angulartics/0.17.2/angulartics-ga.min.js'],

  "angular.module('darg')":
    ['/js/darg/app.js'],

}, {
  shim: {
    'moment.tz': ['moment'],
    "angular": ['jQuery'],
    "angular.module('angulartics')": ['angular'],
    "angular.module('angulartics.google.analytics')": 
      ['angular', "angular.module('angulartics')"],
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
