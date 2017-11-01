angular.module('activityOverview')
    .controller('home', function ($scope, $http) {
            $scope.activitiesWithHeader = [];
            $scope.activeFilter = "";
            $scope.errorMessage;
            $scope.hideCompleted = false;
            $scope.selectedPriority = "C";
            $scope.fileLocation = "default/todo.txt";
            $scope.warningHours = 24;
            $scope.warningMinutes = 0;
            $scope.warningSeconds = 0;
            $scope.editMode = "uneditable";
            $scope.editableActivites = {};
            $scope.lastPrintedDate = undefined;

            $scope.priorities = {
                prioOne: "A",
                prioTwo: "B",
                prioThree: "C",
                prioFour: "D",
                prioFive: "E",
                prioSix: "F"
            }

            $http.get('/initialize').then(function (response) {
                    $scope.loadActivities();
                },
                function (errorResponse) {
                    $scope.errorMessage = 'error initializing application';
                });

            $scope.makeEditable = function (activityName) {
                $scope.editableActivites[activityName] = "editable";
            }

            $scope.getEditMode = function (activityName) {
                let editable = $scope.editableActivites[activityName];
                if (editable) {
                    return editable;
                } else {
                    $scope.editableActivites[activityName] = "uneditable";
                    return $scope.editableActivites[activityName];
                }
            }

            $scope.getErrorMessage = function () {
                return errorMessage;
            }

            $scope.resetDeadline = function () {
                $scope.deadline = undefined;
            }

            $scope.toggleHideCompleted = function () {
                $scope.hideCompleted = !$scope.hideCompleted;
            }

            $scope.getHideCompleted = function () {
                return $scope.hideCompleted;
            }

            $scope.loadActivities = function () {
                $scope.activitiesWithHeader.length = 0;
                $http.get('/getActivitiesWithDateHeader').then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                    },
                    function (errResponse) {
                        $scope.errorMessage = 'error getting activitiesWithHeader';
                    });

                $scope.editableActivites = new Object();

                for (var i in $scope.activitiesWithHeader) {
                    var activityList = $scope.activitiesWithHeader[i];
                    for (var j in activityList) {
                        var activity = activityList[j];
                        $scope.initializeProperties(activity);
                    }
                }
            };

            $scope.initializeProperties = function (activity) {
                $scope.editableActivites[activity.name] = "uneditable";
                $scope.openHeaders[activity.name] = false;

                for (var i in activity.subActivities) {
                    var subActivity = activity.subActivities[i];
                    $scope.initializeProperties(subActivity);
                }

            };

            $scope.loadActivtiesByTag = function (tag) {
                $scope.activeFilter = tag;
                $scope.activitiesWithHeader.length = 0;
                $scope.toggleFilterVisibility();
                $http.post('/getActivitiesByTag', tag).then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                    },
                    function (errResponse) {
                        $scope.errorMessage = 'error getting activitiesWithHeader by tag';
                    });
            };

            $scope.updateFile = function () {
                var fileLoc = $scope.fileLocation;
                $http.post('/updateFileLocation', fileLoc).then(function (response) {
                        $scope.loadActivities();
                    },
                    function (errResponse) {
                        $scope.errorMessage = 'error setting file: ' + errResponse;
                    });
            };


            $scope.loadActivtiesByProject = function (project) {
                $scope.activeFilter = project;
                $scope.activitiesWithHeader = [];
                $scope.toggleFilterVisibility();
                $http.post('/getActivitiesByProject', project).then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                    },
                    function (errResponse) {
                        $scope.errorMessage = 'error getting activitiesWithHeader by project';
                    });
            }

            $scope.clearFilter = function () {
                $scope.activeFilter = "";
                $scope.toggleFilterVisibility();
                $scope.activitiesWithHeader = [];
                $scope.loadActivities();
            }

            $scope.toggleFilterVisibility = function () {
                var div = document.getElementById("filterDisplay");
                div.style.display = div.style.display == "none" ? "block" : "none";
            }

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

            $scope.removeProject = function (activityName, displayArray, index) {
                for (var i in $scope.activitiesWithHeader) {
                    var activityList = $scope.activitiesWithHeader[i];
                    for (var j in activityList) {
                        var activity = activityList[j]
                        if (activity.name == activityName) {
                            $scope.activitiesWithHeader[i][j].projects.splice(index, 1);
                            displayArray.splice(index, 1);
                        }
                    }
                }
            }

            $scope.removeTag = function (activityName, displayArray, index) {
                for (var i in $scope.activitiesWithHeader) {
                    var activityList = $scope.activitiesWithHeader[i];
                    for (var j in activityList) {
                        var activity = activityList[j]
                        if (activity.name == activityName) {
                            $scope.activitiesWithHeader[i][j].tags.splice(index, 1);
                            displayArray.splice(index, 1);
                        }
                    }
                }
            }

            $scope.save = function (name) {
                $scope.editableActivites[name] = false;
                let activityInMemory = $scope.findActivity(name);
                if (activityInMemory == undefined) {
                    $scope.errorMessage = "Activity not found!";
                    return;
                }
                let parentActivity = activityInMemory.parentActivity;
                if (parentActivity != null && parentActivity.length > 0) {
                    $scope.save(parentActivity);
                } else {
                    $scope.saveActivityObjectFromMemory(activityInMemory);
                    return;
                }
            };

            $scope.saveActivityObjectFromMemory = function saveActivityObjectFromMemory(activityToSave) {
                if (activityToSave.deadline == undefined) {
                    delete activityToSave.deadline;
                }
                if (activityToSave.completionDate == undefined) {
                    delete activityToSave.completionDate;
                }
                if (activityToSave.projects == undefined || activityToSave.projects.length < 1) {
                    delete activityToSave.projects;
                }
                if (activityToSave.tags == undefined || activityToSave.tags.length < 1) {
                    delete activityToSave.tags;
                }

                $http.post('/save', activityToSave).then(function (response) {
                        $scope.editableActivites[name] = "uneditable";
                        return true;
                    },
                    function (response) {
                        $scope.errorMessage = "Error while saving activity";
                    });
            }

            $scope.findActivity = function (name) {
                for (j in $scope.activitiesWithHeader) {
                    var activityList = $scope.activitiesWithHeader[j];
                    for (var i in activityList) {
                        let activityInMemory = activityList[i];
                        if (activityInMemory.name == name) {
                            return activityInMemory;
                        } else {
                            let foundSub = $scope.findSubActivity(activityInMemory, name);
                            if (foundSub != undefined) {
                                return foundSub;
                            }
                        }
                    }
                }
                return undefined;
            };

            $scope.findSubActivity = function (activityWithSubs, name) {
                for (var sub in activityWithSubs.subActivities) {
                    let subActivity = activityWithSubs.subActivities[sub];
                    if (subActivity.name == name) {
                        return subActivity;
                    } else {
                        let foundSub = $scope.findSubActivity(subActivity);
                        if (foundSub != undefined) {
                            return foundSub;
                        }
                    }
                }
                return undefined;
            }

            $scope.delete = function (name) {
                $http.get('/getActivitiesWithDateHeader').then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                        let foundActivity = $scope.findActivity(name);
                        $http.post('/delete', foundActivity).then(function (response) {
                                $scope.loadActivities();
                            },
                            function (errResponse) {
                                $scope.errorMessage = 'error deleting activitiesWithHeader';
                            });
                        return;
                    },
                    function (errResponse) {
                        $scope.errorMessage = 'error deleting activitiesWithHeader';
                    });
            };

            $scope.getPanelStyle = function (name) {
                if ($scope.isUrgent(name)) {
                    return 'panel-danger';
                } else {
                    return 'panel-default';
                }
            }

            $scope.isUrgent = function (name) {
                var today = new Date();
                for (i in $scope.activitiesWithHeader) {
                    for (j in $scope.activitiesWithHeader[i]) {
                        if ($scope.activitiesWithHeader[i][j].name == name) {
                            let activity = $scope.activitiesWithHeader[i][j];
                            if (activity.deadline != undefined) {
                                var parsedDeadline = new Date(activity.deadline);
                                var diff = Math.abs(parsedDeadline - new Date()) / 1000;
                                if (diff <= activity.warningTimeFrame && !activity.completed) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                    }
                }
                return false;
            };

            $scope.shouldDateBeDisplayed = function (date) {
                let headerDate = Date.parse(date);
                var diff = Math.abs(headerDate - new Date()) / 1000;
                if (diff >= (31557600)) {
                    return "nodisplay";
                } else {
                    return "display";
                }
            }

            $scope.convertActivityDeadlineToDate = function (acivitydeadline) {
                return new Date(acivitydeadline[0], acivitydeadline[1], acivitydeadline[2], acivitydeadline[3], acivitydeadline[4]);
            };

            $scope.getWarningTimeFrameInSeconds = function getWarningTimeFrameInSeconds() {
                return $scope.warningSeconds + ($scope.warningMinutes * 60) + ($scope.warningHours * 60 * 60);
            }

            $scope.addActivity = function () {

                var activity = {
                    name: $scope.name,
                    completed: false,
                    deadline: $scope.deadline,
                    priority: $scope.selectedPriority,
                    tags: [],
                    projects: [],
                    warningTimeFrame: $scope.getWarningTimeFrameInSeconds()
                };
                if ($scope.tags) {
                    let tagList = $scope.tags.split(",");
                    if (tagList.length > 0) {
                        activity.tags = tagList;
                    }
                }

                if ($scope.projects) {
                    let projectList = $scope.projects.split(",");
                    if (projectList.length > 0) {
                        activity.projects = projectList;
                    }
                }


                var added = false;
                let deadlineToCheck = activity.deadline;
                if (activity.deadline == undefined) {
                    deadlineToCheck = new Date();
                }

                for (var j in $scope.activitiesWithHeader) {
                    if (j == deadlineToCheck && !added) {
                        var activityList = $scope.activitiesWithHeader[j];
                        activityList.push(activity);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    var tempActivityList = [];
                    tempActivityList.push(activity);
                    $scope.activitiesWithHeader[deadlineToCheck] = tempActivityList;
                }

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

            $scope.open3 = function () {
                $scope.popup3.opened = true;
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

            $scope.popup3 = {
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

            $scope.displaySubActivities = function (subArrayLength) {
                if (subArrayLength > 0) {
                    return "showSubActivities";
                } else {
                    return "noShowSubActivities";
                }
            }

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
        }
    )
;