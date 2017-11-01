angular.module('activityOverview')
    .directive('trambuAddActivity', function () {
        return {
            scope: true,
            templateUrl: "templates/trambu-add-activity.html"
        };
    });