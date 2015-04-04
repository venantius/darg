var darg = angular.module('darg', 
                          ['ngCookies', 
                           'ngRoute', 
                           'ngLoadScript']);

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
        .when('/timeline/:teamId', {
            templateUrl: '/templates/home.html'
        })
        .when('/settings', {
            templateUrl: '/templates/settings.html'
        })
        .when('/settings/:settingPage', {
            templateUrl: '/templates/settings.html'
        })
        .when('/team', {
            templateUrl: '/templates/team.html'
        })

        .otherwise({
            redirectTo: '/'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
   }
]);
