angular.module('hello', ['ui.bootstrap'])
    .controller('home', function ($scope, $http) {
        $scope.activities = [];

        $http.get('/getActivities').then(function (response) {
                console.log(response);
                $scope.activities = response.data;
            },
            function (errResponse) {
                console.error('Error while fetching Users');
                deferred.reject(errResponse);
                $scope.error = 'error getting'
            });

        $scope.getDone = function (isDone) {
            if (isDone == true) {
                return 'fa fa-calendar-check-o';
            } else {
                return 'fa fa-calendar-times-o';
            }
        };

        $scope.getTodoToggleText = function (isDone) {
            if (isDone == true) {
                return "Didn't do it";
            } else {
                return "I did it!";
            }
        }

        $scope.getHeaderTemplate = function (isDone) {
            if (isDone == true) {
                return "color: #4cae4c;";
            } else {
                return "";
            }
        }


        $scope.save = function (name) {
            for (i in $scope.activities) {
                if ($scope.activities[i].name == name) {
                    let activity = $scope.activities[i];
                    if (activity.deadline == undefined) {
                        delete activity.deadline;
                    }
                    if (activity.completionDate == undefined) {
                        delete activity.completionDate;
                    }
                    var result = $http.post('/save', activity);
                }
            }
        };

        $scope.delete = function (name) {
            for (i in $scope.activities) {
                if ($scope.activities[i].name == name) {
                    let activity = $scope.activities[i];
                    if (activity.deadline == undefined) {
                        delete activity.deadline;
                    }
                    if (activity.completionDate == undefined) {
                        delete activity.completionDate;
                    }
                    $http.post('/delete', activity).then(function (response) {
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

                }
            }
        };

        $scope.getPanelStyle = function (name) {
            if ($scope.isUrgent(name)) {
                return "panel-danger";
            } else {
                return "panel-default";
            }
        }

        $scope.isUrgent = function (name) {
            var today = new Date();
            for (i in $scope.activities) {
                if ($scope.activities[i].name == name) {
                    let activity = $scope.activities[i];
                    if (activity.deadline != undefined) {
                        var deadlineDate = new Date(activity.deadline);
                        var warningDate = new Date(deadlineDate.getFullYear(), deadlineDate.getMonth(), deadlineDate.getDay() - 1);
                        if (today >= warningDate && !activity.completed) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
            return false;
        }

        $scope.addActivity = function () {
            var tagList = $scope.tags.split(",");
            var projectList = $scope.projects.split(",");
            var activity = {
                name: $scope.name,
                priority: "B",
                completed: false,
                tags: tagList,
                projects: projectList,
                deadline: $scope.deadline
            };
            $scope.activities.push(activity);
            $scope.save(activity.name);
        }

        $scope.togglediv = function (id) {
            var div = document.getElementById(id);
            div.style.display = div.style.display == "none" ? "block" : "none";
        }

        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.clear = function () {
            $scope.dt = null;
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };

        $scope.dateOptions = {
            dateDisabled: disabled,
            formatYear: 'yy',
            maxDate: new Date(2020, 5, 22),
            minDate: new Date(),
            startingDay: 1
        };

        // Disable weekend selection
        function disabled(data) {
            var date = data.date,
                mode = data.mode;
            //return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
            return false;
        }

        $scope.toggleMin = function () {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };

        $scope.toggleMin();

        $scope.open1 = function () {
            $scope.popup1.opened = true;
        };

        $scope.open2 = function () {
            $scope.popup2.opened = true;
        };

        $scope.setDate = function (year, month, day) {
            $scope.dt = new Date(year, month, day);
        };

        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.popup1 = {
            opened: false
        };

        $scope.popup2 = {
            opened: false
        };

        var tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        var afterTomorrow = new Date();
        afterTomorrow.setDate(tomorrow.getDate() + 1);
        $scope.events = [
            {
                date: tomorrow,
                status: 'full'
            },
            {
                date: afterTomorrow,
                status: 'partially'
            }
        ];

        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0, 0, 0, 0);

                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0, 0, 0, 0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }

            return '';
        }
    });