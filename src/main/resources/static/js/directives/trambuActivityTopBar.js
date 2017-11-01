angular.module('activityOverview')
    .directive('trambuActivityTopBar', function () {
        return {
            scope: true,
            templateUrl: "templates/trambu-activity-overview-top-bar.html"
        };
    });