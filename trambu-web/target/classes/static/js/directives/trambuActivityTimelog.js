angular.module('activityOverview')
    .directive('trambuActivityTimelog', function () {
        return {
            scope: true,
            templateUrl: "templates/trambu-activity-timelog.html"
        };
    });