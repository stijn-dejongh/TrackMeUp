angular.module('activityOverview')
    .directive('trambuDateHeader', function () {
        return {
            scope: true,
            templateUrl: "templates/trambu-date-header.html"
        };
    });