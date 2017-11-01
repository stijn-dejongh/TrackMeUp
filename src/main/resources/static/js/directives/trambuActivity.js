angular.module('activityOverview')
    .directive('trambuActivity', function () {
        return {
            templateUrl: "templates/trambu-activity.html",
            link: function(scope, elem, attrs) {
                scope.activity = activity;
            }
        };
    });