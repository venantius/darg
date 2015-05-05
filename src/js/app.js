var darg = angular.module('darg', 
                          ['ngCookies', 
                           'ngRoute', 
                           'ngLoadScript',
                           'ui.bootstrap',
                           'ui.gravatar'
                          ]);

darg.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        // Outer
        .when('/', {
            templateUrl: '/templates/home.html'
        })
        .when('/about', {
            templateUrl: '/templates/about.html'
        })
        .when('/api', {
            templateUrl: '/templates/api.html'
        })
        .when('/faq', {
            templateUrl: '/templates/faq.html'
        })
        .when('/integrations', {
            templateUrl: '/templates/integrations.html'
        })

        // Password reset flow
        .when('/password_reset', {
            templateUrl: '/templates/password_reset.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
        })
        .when('/new_password', {
            templateUrl: '/templates/new_password.html',
            controller: 'DargPasswordResetCtrl',
            controllerAs: 'PasswordReset'
        })

        // Signup and Login
        .when('/signup', {
            templateUrl: '/templates/signup.html',
            controller: 'DargSignupCtrl',
            controllerAs: 'Signup'
        })
        .when('/login', {
            templateUrl: '/templates/login.html'
        })

        // Inner
        .when('/team', {
            templateUrl: '/templates/team.html',
            controller: 'DargTeamCtrl',
            controllerAs: 'Team'
        })
        .when('/team/:teamId', {
            templateUrl: '/templates/team/settings.html',
            controller: 'DargTeamCtrl',
            controllerAs: 'Team'
        })
        .when('/team/:teamId/timeline', {
            templateUrl: '/templates/team/timeline.html',
            controller: 'DargTimelineCtrl',
            controllerAs: 'Timeline'
        })
        .when('/team/:teamId/timeline/:date', {
            templateUrl: '/templates/team/timeline.html',
            controller: 'DargTimelineCtrl',
            controllerAs: 'Timeline'
        })
        .when('/settings/profile', {
            templateUrl: '/templates/settings/profile.html',
            controller: 'DargSettingsCtrl',
            controllerAs: 'Settings'
        })

        .otherwise({
            redirectTo: '/'
        });

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
    $locationProvider.hashPrefix('!');
   }
]);

darg.config(function($provide) {
  $provide.decorator('datepickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/datepicker.html';
    return $delegate;
  });
  $provide.decorator('yearpickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/year.html';
    return $delegate;
  });
  $provide.decorator('monthpickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/month.html';
    return $delegate;
  });
  $provide.decorator('daypickerDirective', function($delegate) {
    $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/day.html';
    return $delegate;
  });
  $provide.decorator('datepickerPopupWrapDirective', function($delegate) {
      $delegate[0].templateUrl = '/templates/angular_bootstrap/datepicker/popup.html';
      return $delegate;
  });
});
