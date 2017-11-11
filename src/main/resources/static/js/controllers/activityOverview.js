angular.module('activityOverview')
    .controller('home', function ($scope, $http) {
            $scope.activitiesWithHeader = [];
            $scope.activeFilter = "";
            $scope.statusMessage = "";
            $scope.hideCompleted = false;

            $scope.editMode = "uneditable";
            $scope.editableActivites = {};
            $scope.lastPrintedDate = undefined;
            $scope.openHeaders = [];

            $scope.fileLocation = "default/todo.txt";
            $scope.trackingFileLocation = "default/logs.txt";

            $scope.name = "";
            $scope.selectedPriority = "C";
            $scope.warningHours = 24;
            $scope.warningMinutes = 0;
            $scope.warningSeconds = 0;
            $scope.selectedParent = undefined;
            $scope.tags = "";
            $scope.projects = "";
            $scope.deadline = undefined;

            $scope.activiveTimelogging = "";


            $scope.priorities = {
                prioOne: "A",
                prioTwo: "B",
                prioThree: "C",
                prioFour: "D",
                prioFive: "E",
                prioSix: "F"
            };

            $scope.inlineOptions = {
                minDate: new Date(),
                showWeeks: true
            };

            $scope.dateOptions = {
                formatYear: 'yy',
                maxDate: new Date(2020, 5, 22),
                minDate: new Date(),
                startingDay: 1
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

            $http.get('/initialize').then(function () {
                    $scope.loadActivities();
                },
                function () {
                    $scope.statusMessage = 'error initializing application';
                });

            $scope.makeEditable = function (activityName) {
                $scope.editableActivites[activityName] = "editable";
            };

            $scope.getEditMode = function (activityName) {
                let editable = $scope.editableActivites[activityName];
                if (editable) {
                    return editable;
                } else {
                    $scope.editableActivites[activityName] = "uneditable";
                    return $scope.editableActivites[activityName];
                }
            };

            $scope.getstatusMessage = function () {
                return statusMessage;
            };

            $scope.resetDeadline = function () {
                $scope.deadline = undefined;
            };

            $scope.toggleHideCompleted = function () {
                $scope.hideCompleted = !$scope.hideCompleted;
            };

            $scope.getHideCompleted = function () {
                return $scope.hideCompleted;
            };

            $scope.loadActivities = function () {
                $scope.activitiesWithHeader.length = 0;
                $http.get('/getActivitiesWithDateHeader').then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                    },
                    function () {
                        $scope.statusMessage = 'error getting activitiesWithHeader';
                    });

                $scope.editableActivites = {};

                for (let i in $scope.activitiesWithHeader) {
                    let activityList = $scope.activitiesWithHeader[i];
                    for (let j in activityList) {
                        let activity = activityList[j];
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
                    function () {
                        $scope.statusMessage = 'error getting activitiesWithHeader by tag';
                    });
            };

            $scope.updateFile = function () {
                let fileLoc = $scope.fileLocation;
                $http.post('/updateFileLocation', fileLoc).then(function () {
                        $scope.loadActivities();
                    },
                    function (errResponse) {
                        $scope.statusMessage = 'error setting todo file: ' + errResponse;
                    });

                let logFileLoc = $scope.trackingFileLocation;
                $http.post('/updateLogFileLocation', logFileLoc).then(function () {

                    },
                    function (errResponse) {
                        $scope.statusMessage = 'error setting log file: ' + errResponse;
                    });
            };


            $scope.loadActivtiesByProject = function (project) {
                $scope.activeFilter = project;
                $scope.activitiesWithHeader = [];
                $scope.toggleFilterVisibility();
                $http.post('/getActivitiesByProject', project).then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                    },
                    function () {
                        $scope.statusMessage = 'error getting activitiesWithHeader by project';
                    });
            };

            $scope.clearFilter = function () {
                $scope.activeFilter = "";
                $scope.toggleFilterVisibility();
                $scope.activitiesWithHeader = [];
                $scope.loadActivities();
            };

            $scope.toggleFilterVisibility = function () {
                let div = document.getElementById("filterDisplay");
                div.style.display = div.style.display === "none" ? "block" : "none";
            };

            $scope.getDone = function (isDone) {
                if (isDone === true) {
                    return 'fa fa-calendar-check-o';
                } else {
                    return 'fa fa-calendar-times-o';
                }
            };

            $scope.getTodoToggleText = function (isDone) {
                if (isDone === true) {
                    return "Didn't do it";
                } else {
                    return "I did it!";
                }
            }

            $scope.getHeaderTemplate = function (isDone) {
                if (isDone === true) {
                    return "color: #4cae4c;";
                } else {
                    return "";
                }
            }

            $scope.removeProject = function (activityName, projectToRemove) {
                let activity = $scope.findActivity(activityName);
                let index = activity.projects.indexOf(projectToRemove);
                activity.projects.splice(index, 1);
            };

            $scope.removeTag = function (activityName, tagToRemove) {
                let activity = $scope.findActivity(activityName);
                $scope.statusMessage = "ActivityName: " + activityName + " Tag: [" + tagToRemove + "]";
                let index = activity.tags.indexOf(tagToRemove);
                activity.tags.splice(index, 1);
            };

            $scope.save = function (name) {
                $scope.editableActivites[name] = false;
                let activityInMemory = $scope.findActivity(name);
                if (activityInMemory === undefined) {
                    $scope.statusMessage = "Activity not found!";
                    return;
                }
                let parentActivity = activityInMemory.parentActivity;
                if (parentActivity !== null && parentActivity !== undefined && parentActivity.length > 0) {
                    $scope.save(parentActivity);
                } else {
                    $scope.saveActivityObjectFromMemory(activityInMemory);
                }
            };

            $scope.saveActivityObjectFromMemory = function saveActivityObjectFromMemory(activityToSave) {
                if (activityToSave.deadline === undefined) {
                    delete activityToSave.deadline;
                }
                if (activityToSave.completionDate === undefined) {
                    delete activityToSave.completionDate;
                }
                if (activityToSave.projects === undefined || activityToSave.projects.length < 1) {
                    delete activityToSave.projects;
                }
                if (activityToSave.tags === undefined || activityToSave.tags.length < 1) {
                    delete activityToSave.tags;
                }

                $http.post('/save', activityToSave).then(function () {
                        $scope.editableActivites[name] = "uneditable";
                    },
                    function () {
                        $scope.statusMessage = "Error while saving activity";
                    });
            };

            $scope.findActivity = function (name) {
                for (let j in $scope.activitiesWithHeader) {
                    let activityList = $scope.activitiesWithHeader[j];
                    for (let i in activityList) {
                        let activityInMemory = activityList[i];
                        if (activityInMemory.name === name) {
                            return activityInMemory;
                        } else {
                            let foundSub = $scope.findSubActivity(activityInMemory, name);
                            if (foundSub !== undefined) {
                                return foundSub;
                            }
                        }
                    }
                }
                return undefined;
            };

            $scope.findSubActivity = function (activityWithSubs, name) {
                for (let sub in activityWithSubs.subActivities) {
                    let subActivity = activityWithSubs.subActivities[sub];
                    if (subActivity.name === name) {
                        return subActivity;
                    } else {
                        let foundSub = $scope.findSubActivity(subActivity);
                        if (foundSub !== undefined) {
                            return foundSub;
                        }
                    }
                }
                return undefined;
            };

            $scope.delete = function (name) {
                $http.get('/getActivitiesWithDateHeader').then(function (response) {
                        $scope.activitiesWithHeader = response.data;
                        let foundActivity = $scope.findActivity(name);
                        $http.post('/delete', foundActivity).then(function () {
                                $scope.loadActivities();
                            },
                            function () {
                                $scope.statusMessage = 'error deleting activitiesWithHeader';
                            });
                    },
                    function () {
                        $scope.statusMessage = 'error deleting activitiesWithHeader';
                    });
            };

            $scope.getPanelStyle = function (name) {
                if ($scope.isUrgent(name)) {
                    return 'panel-danger';
                } else {
                    return 'panel-default';
                }
            };

            $scope.isUrgent = function (name) {
                let activity = $scope.findActivity(name);
                if (activity.deadline !== undefined) {
                    let parsedDeadline = new Date(activity.deadline);
                    let diff = Math.abs(parsedDeadline - new Date()) / 1000;
                    return diff <= activity.warningTimeFrame && !activity.completed;
                }

                return false;
            };

            $scope.shouldDateBeDisplayed = function (date) {
                let headerDate = Date.parse(date);
                let diff = Math.abs(headerDate - new Date()) / 1000;
                if (diff >= (31557600)) {
                    return "nodisplay";
                } else {
                    return "display";
                }
            };

            $scope.convertActivityDeadlineToDate = function (acivitydeadline) {
                return new Date(acivitydeadline[0], acivitydeadline[1], acivitydeadline[2], acivitydeadline[3], acivitydeadline[4]);
            };

            $scope.getWarningTimeFrameInSeconds = function getWarningTimeFrameInSeconds() {
                return $scope.warningSeconds + ($scope.warningMinutes * 60) + ($scope.warningHours * 60 * 60);
            };

            $scope.addActivity = function () {
                $scope.statusMessage = "Saving activity";
                let activity = {
                    name: $scope.name,
                    completed: false,
                    deadline: $scope.deadline,
                    priority: $scope.selectedPriority,
                    tags: [],
                    projects: [],
                    warningTimeFrame: $scope.getWarningTimeFrameInSeconds(),
                    parentActivity: undefined
                };
                if ($scope.tags && $scope.tags.length > 1) {
                    let tagList = $scope.tags.split(",");
                    if (tagList.length > 0) {
                        activity.tags = tagList;
                    }
                }

                if ($scope.projects && $scope.projects.length > 1) {
                    let projectList = $scope.projects.split(",");
                    if (projectList.length > 0) {
                        activity.projects = projectList;
                    }
                }

                if ($scope.selectedParent && $scope.selectedParent !== undefined) {
                    activity.parentActivity = $scope.selectedParent;
                }


                let added = false;
                if (activity.parentActivity !== undefined && activity.parentActivity.length > 0) {
                    let parent = $scope.findActivity(activity.parentActivity);
                    if (parent !== undefined) {
                        parent.subActivities.push(activity);
                        added = true;
                    } else {
                        activity.parentActivity = null;
                    }
                }


                let deadlineToCheck = activity.deadline;
                if (activity.deadline === undefined) {
                    deadlineToCheck = new Date();
                }

                for (let j in $scope.activitiesWithHeader) {
                    if (j === deadlineToCheck && !added) {
                        let activityList = $scope.activitiesWithHeader[j];
                        activityList.push(activity);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    let tempActivityList = [];
                    tempActivityList.push(activity);
                    $scope.activitiesWithHeader[deadlineToCheck] = tempActivityList;
                }


                $scope.save(activity.name);
                $scope.resetInputFields();
            };

            $scope.resetInputFields = function () {
                $scope.name = "";
                $scope.selectedPriority = "C";
                $scope.warningHours = 24;
                $scope.warningMinutes = 0;
                $scope.warningSeconds = 0;
                $scope.selectedParent = undefined;
                $scope.tags = "";
                $scope.projects = "";
                $scope.deadline = undefined;

                $scope.togglediv('addActivity');
            };

            $scope.startTimeLog = function (activityName) {
                let activity = $scope.findActivity(activityName);
                $http.post('/startTimeLog', activity.id).then(function (response) {
                        $scope.statusMessage = 'Timelogging started for activity [' + activity.name + ']';
                        $scope.activiveTimelogging = activity.name;
                    },
                    function () {
                        $scope.statusMessage = 'error starting timelogging for activity [' + activity.name + ']';
                    });
            }

            $scope.stopTimeLog = function (activityName) {
                let activity = $scope.findActivity(activityName);
                $http.post('/stopTimeLog', activity.id).then(function (response) {
                        $scope.statusMessage = 'Timelogging stopped for activity [' + activity.name + ']';
                        $scope.activiveTimelogging = "";
                    },
                    function () {
                        $scope.statusMessage = 'error stopping timelogging for activity [' + activity.name + ']';
                    });
            }

            $scope.isTimelogActive = function (activityName) {
                if ($scope.activiveTimelogging === activityName) {
                    return 'active';
                } else {
                    return 'notActive';
                }
            }

            $scope.togglediv = function (id) {
                let div = document.getElementById(id);
                div.style.display = div.style.display === "none" ? "block" : "none";
            };

            $scope.today = function () {
                $scope.dt = new Date();
            };
            $scope.today();

            $scope.clear = function () {
                $scope.dt = null;
            };

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

            $scope.displaySubActivities = function (subArrayLength) {
                if (subArrayLength > 0) {
                    return "showSubActivities";
                } else {
                    return "noShowSubActivities";
                }
            };
        }
    )
;