angular.module('hello', [])
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
    });