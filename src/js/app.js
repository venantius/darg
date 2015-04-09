var darg = angular.module('darg', 
                          ['ngCookies', 
                           'ngRoute', 
                           'ngLoadScript',
                           'ui.date',
                           'ui.gravatar']);

darg.config(['$routeProvider', '$locationProvider', 
           function AppConfig($routeProvider, $locationProvider) {

    $routeProvider
        // outer
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
        .when('/password_reset', {
            templateUrl: '/templates/password_reset.html'
        })

        // inner
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
        .when('/settings/profile', {
            templateUrl: '/templates/settings/profile.html',
            controller: 'DargSettingsCtrl',
            controllerAs: 'Settings'
        })

        .otherwise({
            redirectTo: '/'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
   }
]);
