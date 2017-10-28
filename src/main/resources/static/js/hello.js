angular.module('hello', ['ui.bootstrap'])
    .controller('home', function ($scope, $http) {
        $http.get('/getActivities').then(function (response) {
                console.log(response);
                $scope.activities = response.data;
            },
            function (errResponse) {
                console.error('Error while fetching Users');
                deferred.reject(errResponse);
                $scope.error = 'error getting'
            });

        $scope.getDone = function(isDone) {
            if(isDone == true) {
                return '[DONE]';
            } else {
                return '[TODO]';
            }
        };
    });