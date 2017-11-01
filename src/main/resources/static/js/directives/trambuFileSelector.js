angular.module('activityOverview')
    .directive('trambuFileSelector', function () {
        return {
            templateUrl: "templates/trambu-file-selector.html",
            link: function(scope, elem, attrs) {
                scope.fileLocation = fileLocation;
            }
        };
    });