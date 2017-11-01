angular.module('activityOverview')
    .directive('trambuAddActivity', function () {
        return {
            templateUrl: "templates/trambu-add-activity.html",
            link: function(scope, elem, attrs) {
                scope.name = name;
                scope.selectedPriority = selectedPriority;
                scope.deadline = deadline;
                scope.warningHours = warningHours;
                scope.warningMinutes = warningMinutes;
                scope.warningSeconds = warningSeconds;
            }
        };
    });