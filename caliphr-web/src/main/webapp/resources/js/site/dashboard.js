(function () {
	
	// send the csrf_token along with all ajax requests
	var csrf_token = $("meta[name='_csrf']").attr("content");
    $.ajaxSetup({
        headers: {
        	'X-CSRF-TOKEN': csrf_token
        }
    });

    //  Fixed side nav scroller (mmelusky)

    $(window).scroll(function(){
        if ($(this).scrollTop() > 230) {
            $('#back-top').fadeIn();
        } else {
            $('#back-top').fadeOut();
        }

    });

    var APP = Ember.Application.create({
        rootElement: "#hookForEmber"
    });


    APP.Router.map(function () {
        this.resource('cqm', {path: '/cqm/:id'}, function () {

        });
        this.resource('measureInformation', {path: '/:cmsId'}, function () {
            this.resource('dummyTemplate', {path: '/:submeasureTitle'});
            this.resource('initialPopulation', function () {
                this.resource('initialPopulationCriteria', {path: '/initialPopulationCriteria'});
                this.resource('initialPopulationPatients', {path: '/initialPopulationPatients'});
            });
            this.resource('numeratorInfo', function () {
                this.resource('numeratorInfoCriteria', {path: '/numeratorCriteria'});
                this.resource('numeratorInfoPatients', {path: '/numeratorPatients'});
            });//:submeasureInfo
            this.resource('denominatorInfo', function () {
                this.resource('denominatorInfoCriteria', {path: '/denominatorInfoCriteria'});
                this.resource('denominatorInfoPatients', {path: '/denominatorInfoPatients'});
            });
            this.resource('exclusionsInfo', function () {
                this.resource('exclusionsInfoCriteria', {path: '/exclusionsInfoCriteria'});
                this.resource('exclusionsInfoPatients', {path: '/exclusionsInfoPatients'});
            });
            this.resource('exceptionInfo',function(){
                this.resource('exceptionInfoCriteria', {path:'/exceptionInfoCriteria'});
                this.resource('exceptionInfoPatients', {path:'/exceptionInfoPatients'});
            })
        });


    });

    APP.ApplicationRoute = Ember.Route.extend({
        model: function () {
            return Ember.RSVP.hash({
                organization:Em.$.ajax({
                    "type":"POST",
                    'url': '/api/provider/organizations/all',
                    'async': true,
                    'global': false,
                    'success': function (data) {
                        return data;
                    }
                }),// Ember.$.getJSON('/api/provider/organizations/all'),
                practice: Em.$.ajax({
                    "type":"POST",
                    'url': '/api/provider/practices/all',
                    'async': true,
                    'global': false,
                    'success': function (data) {
                        return data;
                    }
                }),// Ember.$.getJSON('/api/provider/practices/all')
                provider: Em.$.ajax({
                    "type":"POST",
                    'url': '/api/provider/providers/all',
                    'async': true,
                    'global': false,
                    'success': function (data) {
                        return data;
                    }
                })//Ember.$.getJSON('/api/provider/providers/all'),
            });
        },
        afterModel:function(model,transition){
            var org = model.organization;
            var prac = model.practice;
            var prov = model.provider;
             console.log('------------------------------'+model.organization);
            console.log('-------------------------------'+model.practice);
            console.log('-------------------------------'+model.provider);
            console.log('------------------------------'+org.length);
            if(org.length == 1) {
                console.log('Yes there is only 1 organization');
                this.controllerFor('application').set('organizationSelected.id', org[0].id);
                this.controllerFor('application').set('controlPracDisable', false);
            }
            if(prac.length == 1 && (prac[0].organizationId == org[0].id)){
                this.controllerFor('application').set('practiceSelected.id',prac[0].id);
                this.controllerFor('application').set('controlProvDisable',false);
            }
            if(prov.length == 1 && (prov[0].groupId == prac[0].id)){
                this.controllerFor('application').set('providerSelected.id',prov[0].id);
            }

        },
    });

    APP.IndexRoute = Ember.Route.extend({
        beforeModel:function() {
            return this.transitionTo('index');
        }
    });

    APP.CqmRoute = Ember.Route.extend({
        beforeModel:function(){
            if((this.controllerFor('application').get('providerSelected.id')) == null){
                this.transitionTo('index');
            }
        },
        model: function (params) {
            //this.controllerFor('application').set('providerSelected.id',params.id);
            if((this.controllerFor('application').get('providerSelected.id') != null) || (params >0)) {
                console.log("Entered if condition");
                return Em.$.ajax({
                    "type": "POST",
                    'url': '/api/all_active_measures/' + params.id,
                    'async': false,
                    'global': false,
                    'success': function (data) {
                        return data;
                    }
                });
            } else {
                console.log("Entered else condition");
                return Em.$.ajax({
                    "type": "POST",
                    'url': '/api/all_active_measures/' + 0,
                    'async': false,
                    'global': false,
                    'success': function (data) {
                        return data;
                    }
                });
            }
        },
    });



    APP.MeasureInformationRoute = Ember.Route.extend({
        beforeModel:function(){
            if((this.controllerFor('application').get('providerSelected.id')) == null){
                this.transitionTo('index');
            }
        },
        model: function () {

        }
    });

    APP.MeasureInformationIndexRoute = Ember.Route.extend({
        model: function () {
            return this.transitionTo('initialPopulationCriteria');
        }
    });

    APP.NumeratorInfoRoute = Ember.Route.extend({
        model: function () {

        }
    });
    APP.NumeratorInfoIndexRoute = Ember.Route.extend({
        beforeModel: function () {
            return this.transitionTo('numeratorInfoCriteria');
        }
    });
    APP.InitialPopulationIndexRoute = Ember.Route.extend({
        beforeModel: function () {
            return this.transitionTo('initialPopulationCriteria');
        }
    });
    APP.DenominatorInfoIndexRoute = Ember.Route.extend({
        beforeModel: function () {
            return this.transitionTo('denominatorInfoCriteria');
        }
    });
    APP.ExclusionsInfoIndexRoute = Ember.Route.extend({
        beforeModel: function () {
            return this.transitionTo('exclusionsInfoCriteria');
        }
    });
    APP.ExceptionInfoIndexRoute = Ember.Route.extend({
        beforeModel:function(){
            return this.transitionTo('exceptionInfoCriteria');
        }
    });
    APP.InitialPopulationCriteriaRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/measure_attributes/' + this.controllerFor('measureInformation').get('hqmfDocumentId'));
            var reuseModel = this.controllerFor('measureInformation').get('modelForInfo');
            return reuseModel;
        }
    });
    APP.NumeratorInfoCriteriaRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/measure_attributes/' + this.controllerFor('measureInformation').get('hqmfDocumentId'));
            var reuseModel = this.controllerFor('measureInformation').get('modelForInfo');
            return reuseModel;
        }
    });
    APP.DenominatorInfoCriteriaRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/measure_attributes/' + this.controllerFor('measureInformation').get('hqmfDocumentId'));
            var reuseModel = this.controllerFor('measureInformation').get('modelForInfo');
            return reuseModel;
        }
    });
    APP.ExclusionsInfoCriteriaRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/measure_attributes/' + this.controllerFor('measureInformation').get('hqmfDocumentId'));
            var reuseModel = this.controllerFor('measureInformation').get('modelForInfo');
            return reuseModel;
        }
    });
    APP.ExceptionInfoCriteriaRoute = Ember.Route.extend({
        model:function(){
            //return Ember.$.getJSON('/api/measure_attributes/' + this.controllerFor('measureInformation').get('hqmfDocumentId'));
            var reuseModel = this.controllerFor('measureInformation').get('modelForInfo');
            return reuseModel;
        }
    });
    APP.NumeratorInfoPatientsRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/patient_results/' + this.controllerFor('dummyTemplate').get('numeratorId'));
           return Em.$.ajax({
                "type":"POST",
                'url': '/api/patient_results/' + this.controllerFor('dummyTemplate').get('numeratorId'),
                'async': true,
                'global': false,
                'success': function (data) {
                    return data;
                }
            })
        }
    });
    APP.InitialPopulationPatientsRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/patient_results/' + this.controllerFor('dummyTemplate').get('ippId'));
            return Em.$.ajax({
                "type":"POST",
                'url': '/api/patient_results/' + this.controllerFor('dummyTemplate').get('ippId'),
                'async': true,
                'global': false,
                'success': function (data) {
                    return data;
                }
            })
        }
    });
    APP.DenominatorInfoPatientsRoute = Ember.Route.extend({
        model: function () {
            //return Ember.$.getJSON('/api/patient_results/' + this.controllerFor('dummyTemplate').get('denominatorId'));
            return Em.$.ajax({
                "type":"POST",
                'url': '/api/patient_results/' + this.controllerFor('dummyTemplate').get('denominatorId'),
                'async': true,
                'global': false,
                'success': function (data) {
                    return data;
                }
            })
        }
    });
    APP.ExclusionsInfoPatientsRoute = Ember.Route.extend({
        model: function () {
            return Em.$.ajax({
                "type":"POST",
                'url': '/api/patient_results/' + this.controllerFor('dummyTemplate').get('exclusionId'),
                'async': true,
                'global': false,
                'success': function (data) {
                    return data;
                }
            });
        }
    });
    APP.ExceptionInfoPatientsRoute = Ember.Route.extend({
        model:function() {
            //return Ember.$.getJSON('/api/patient_results/' + this.controllerFor('dummyTemplate').get('exceptionId'));
            return Em.$.ajax({
                "type":"POST",
                'url': '/api/patient_results/' + this.controllerFor('dummyTemplate').get('exceptionId'),
                'async': true,
                'global': false,
                'success': function (data) {
                    return data;
                }
            })
        }
    });

    APP.PercentageLoaderComponent = Ember.Component.extend({
        classNames: ['percentageLoader'],
        percent:function(){
            //var percent = (this.get('num')>0)?((this.get('num') / (this.get('den')-this.get('excl')-this.get('excep'))) *100) : 0;
            if(this.get('num') == 0){
                return 0;
            } else {
                return (this.get('num') / (this.get('den')-this.get('excl')-this.get('excep'))) *100
            }
           // return percent;
        }.property(),
        _initializeClassyLoader: function () {
            this.$().ClassyLoader({
                displayOnLoad: true,
                percentage: this.get('percent'),
                speed: 1,
                roundedLine: false,
                showRemaining: true,
                fontFamily: 'Helvetica',
                fontSize: '22px',
                showText: true,
                diameter: 60,
                fontColor: '#3c763d',
                lineColor: '#3c763d',
                remainingLineColor: '#f0f0f0',
                lineWidth: 20
            });
        }.on('didInsertElement')
    });

    APP.DaterangePickerComponent = Ember.Component.extend({
        classNames: ['dateRangePicker'],
        _initializeDateRangePicker: function () {
            this.$().daterangepicker({
                locale: {
                    format: 'MMM D, YYYY'
                },
                showDropdowns: true,
                autoApply: true,
                startDate: moment().subtract(29, 'days'),
                endDate: moment(),
            });
        }.on('didInsertElement')
    });

    APP.IndexController = Ember.ObjectController.extend({

    });


    APP.ApplicationController = Ember.ObjectController.extend({
        selectedOrganization:null,
        selectedPractice:null,
        selectedProvider:null,
        showRefreshButton: false,
        currentPathChanged: function () {
            if(this.get('currentPath') == 'measureInformation.dummyTemplate') {
                console.log('the current path is -------------------->' + this.get('currentPath'));
                window.scrollTo(0, 0);
            }
        }.observes('currentPath'),
        controlPracDisable:true,
        controlProvDisable:true,
        dropDownOrg:true,
        dropDownPrac:true,
        dropDownProv:true,

        organizationSelected: {
            id: null,
        },
        practiceSelected: {
            id: null,
        },
        providerSelected: {
            id: null,
        },

        watchProviderDropDown: function () {
            console.log("Entered Watch provider");
            sessionStorage.setItem("ProvId",this.get('providerSelected.id'));
            //console.log('RESET THE TIMER');
            //ResetTimeOutTimer();
            var Id1 = parseInt(this.get('organizationSelected.id'));
            var Id2 = parseInt(this.get('practiceSelected.id'));
            var Id3 = parseInt(this.get('providerSelected.id'));
            this.set('selectedProvider',this.get('providerSelected.id'));
            //idForProvider = Id;
            if ((Id1>0) && (Id2>0) && (Id3>0)) {

                console.log("Entered if-if condition :"+Id3);
                // localStorage.setItem('providerId',Id3);
                this.controllerFor('cqm').set('showExportAndCheck', true);
                this.set('showRefreshButton',true);
                this.set("showCalculationButton", true);
               // this.set('providerSelected.id',Id3);
                this.transitionTo('cqm', Id3);

                // Reset the check all button for provider changes
                this.controllerFor('cqm').set("toggleCheckAll", false);
                this.controllerFor('cqm').send("checkAllClick");

                //console.log(this.get('providerSelected.id'));
            } else {
                console.log("Entered else-else condition");
                this.transitionTo('index');
                this.controllerFor('cqm').set('showExportAndCheck', false);
                this.set('showRefreshButton',false);
                this.set("showCalculationButton", false);
            }
        }.observes('providerSelected.id'),

        //
        // Property to determine whether to show/hide thc calculate button
        showCalculationButton: false,
        groupedPractice:null,
        groupedProvider:null,
        watchOrganizationDropDown:function(){
            console.log("Entered Watch Organization");
            sessionStorage.setItem("OrgId",this.get('organizationSelected.id'));
            this.set('practiceSelected.id',null);
            this.set('providerSelected.id',null);
            var orgId = this.get('organizationSelected.id');
            this.set('selectedOrganization',orgId);
            var matchedResults = Ember.A();
            var practiceModel = null;
            console.log("-------------------------------------------"+this.get('model.practice'));
            if(this.get('model.practice') == null) {
                console.log('-------------------------------------PRACTICE MODEL IS EMPTY');
                $.ajax({
                    "type":"POST",
                    'async': false,
                    'global': false,
                    'url': '/api/provider/practices/all',
                    'dataType': "json",
                    'success': function (data) {
                        practiceModel = data;
                    }
                });
                practiceModel.forEach(function (item) {
                    if (orgId == item.organizationId) {
                        matchedResults.pushObject(item);
                    }
                });
                if(this.get('selectedPractice')){
                    this.set('practiceSelected.id',this.get('selectedPractice'));
                }

            } else {
                practiceModel = this.get('model.practice');
                practiceModel.forEach(function (item) {
                    if (orgId == item.organizationId) {
                        matchedResults.pushObject(item);
                    }
                });
            }

            this.set('groupedPractice',matchedResults);
            if(sessionStorage.getItem('PracId')){
                console.log("_HAKUNA MATATAT");
                this.set('practiceSelected.id',sessionStorage.getItem("PracId"));
                console.log("HAKUN ABXAJBXJXNKAX________________________ :" + sessionStorage.getItem("PracId"));
            }
            if(this.get('organizationSelected.id')) {
                this.set('controlPracDisable', false);
                if(this.get('practiceSelected.id') && this.get('providerSelected.id')){
                    this.controllerFor('cqm').set('showExportAndCheck', true);
                    this.set('showCalculationButton',true);
                    this.set('showRefreshButton',true);
                } else {
                   // this.transitionTo('cqm',0);
                    this.controllerFor('cqm').set('showExportAndCheck', false);
                }

                // Reset the check all button for provider changes
                this.controllerFor('cqm').set("toggleCheckAll", false);
                this.controllerFor('cqm').send("checkAllClick");
            } else {
                //this.transitionTo('cqm',0);
                this.controllerFor('cqm').set('showExportAndCheck', false);
                this.set('controlPracDisable',true);
                this.set('showCalculationButton',false);
                this.set('showRefreshButton',false);
            }

        }.observes('organizationSelected.id'),
        watchPracticeDropDown:function(){
            console.log("Entered Watch Practice");
            var pracId = this.get('practiceSelected.id');

            //$('#practiceList option[value="'+pracId+'"]').attr("selected",'selected');
            sessionStorage.setItem("PracId",this.get('practiceSelected.id'));
            this.set('providerSelected.id',null);

            this.set('selectedPractice',pracId);
            //var providerModel = this.get('model.provider');
            var matchedResults = Ember.A();
            var providerModel = null;
            if (this.get('model.provider') == null) {
                console.log('PROVIDER MODEL IS EMPTY');
                $.ajax({
                    "type":"POST",
                    'async': false,
                    'global': false,
                    'url': '/api/provider/providers/all',
                    'dataType': "json",
                    'success': function (data) {
                        providerModel = data;
                       // console.log("THE DATA RECEIVED FROM AJAX: " + providerModel);
                    }
                });
                providerModel.forEach(function (item) {
                    if (pracId == item.groupId) {
                        matchedResults.pushObject(item);
                    }
                });
                /*if(this.get('selectedProvider')){
                    this.set('providerSelected.id',this.get('selectedProvider'));
                }*/

            } else {
                providerModel = this.get('model.provider');
                providerModel.forEach(function (item) {
                    if (pracId == item.groupId) {
                        matchedResults.pushObject(item);
                    }
                });
            }
            this.set('groupedProvider',matchedResults);
            if(this.get('practiceSelected.id')) {
                this.set('controlProvDisable', false);
                if(this.get('organizationSelected.id') && this.get('providerSelected.id')){
                    this.controllerFor('cqm').set('showExportAndCheck', true);
                    this.set('showCalculationButton',true);
                    this.set('showRefreshButton',true);
                } else {
                    //this.transitionTo('cqm',0);
                    this.controllerFor('cqm').set('showExportAndCheck', false);
                }

                // Reset the check all button for provider changes
                this.controllerFor('cqm').set("toggleCheckAll", false);
                this.controllerFor('cqm').send("checkAllClick");
            } else {
               // this.transitionTo('cqm',0);
                this.controllerFor('cqm').set('showExportAndCheck', false);
                this.set('controlProvDisable',true);
                this.set('showCalculationButton',false);
                this.set('showRefreshButton',false);
            }
        }.observes('practiceSelected.id'),
        calculationTrigger: true,
        actions: {
            calculate: function () {
                console.log('RESET THE TIMER');
                // ResetTimeOutTimer();
                var Id1 = parseInt(this.get('organizationSelected.id'));
                var Id2 = parseInt(this.get('practiceSelected.id'));
                var Id3 = parseInt(this.get('providerSelected.id'));

                if (this.get('calculationTrigger') && (Id1>0) && (Id2>0) && (Id3>0)) {
                    this.set("showCalculationButton",false);
                    var dat = $('#daterange').data('daterangepicker');
                    var prov = this.get('providerSelected.id');
                    $("#calculationButton").attr('data-target', '#calculationTriggerModal');
                    var startDateYear = moment(dat.startDate, 'YYY-MM-DD').year();
                    var endDateYear = moment(dat.endDate, 'YYY-MM-DD').year();
                    var startDateDay = moment(dat.startDate, 'YYY-MM-DD').date();
                    var endDateDay = moment(dat.endDate, 'YYY-MM-DD').date();
                    var startDateMonth = ((moment(dat.startDate, 'YYY-MM-DD').month() + 1) < 10 ? '0' : '') + (moment(dat.startDate, 'YYY-MM-DD').month() + 1);
                    var endDateMonth = ((moment(dat.endDate, 'YYY-MM-DD').month() + 1) < 10 ? '0' : '') + (moment(dat.endDate, 'YYY-MM-DD').month() + 1);
                    var startDateSelected = startDateYear + '' + startDateMonth + '' + startDateDay;
                    var endDateSelected = endDateYear + '' + endDateMonth + '' + endDateDay;
                    console.log(startDateSelected + '--------------->' + endDateSelected + '----------> provider Id is ' + prov);

                    $.post('/api/measures/calculate?', {
                        providerId: prov,
                        startDate: startDateSelected,
                        endDate: endDateSelected
                    }, function (data) {
                        console.log('Status returned: ' + data.status + '\n ' + 'Message : ' + data.message);
                    }).fail(function () {
                        console.log(error);
                    });

                    // Reset the check all flag for new calculations
                    this.controllerFor('cqm').set("toggleCheckAll", false);
                    this.controllerFor('cqm').send("checkAllClick");
                }

            },
            refreshCqm:function(){
                console.log('RESET THE TIMER');
                // ResetTimeOutTimer();
                var Id3 = parseInt(this.get('providerSelected.id'));

                this.transitionToRoute('cqm',Id3);

                this.controllerFor('cqm').send('doRefresh');
            }

        }
    });

    Ember.Handlebars.helper('format-date', function (data) {
        return moment(new Date(data)).format('ll');
    });
    Ember.Handlebars.helper('show-newlines', function (data) {
        if(data) {
            return data.replace(/\\n/g, "<br/>");
        }
    });

    APP.InitialPopulationCriteriaController = Ember.ObjectController.extend({
        initialPopulationValue: function () {
            if(this.get('model.Initial Patient Population')) {
                var parsedJson = JSON.parse(this.get('model.Initial Patient Population'));
                return parsedJson["value"];
            }
        }.property('model'),
        stratification: function () {
            if(this.get('model.Stratification')) {
                var parsedJson = JSON.parse(this.get('model.Stratification'));
                return parsedJson["value"];
            }
        }.property('model')
    });

    APP.NumeratorInfoCriteriaController = Ember.ObjectController.extend({
        numeratorValue: function () {
            if(this.get('model.Numerator')) {
                var parsedJson = JSON.parse(this.get('model.Numerator'));
                return parsedJson["value"];
            }
        }.property('model'),
        guidanceValue: function () {
            if(this.get('model.Guidance')) {
                var parsedJson = JSON.parse(this.get('model.Guidance'));
                return parsedJson["value"];
            }
        }.property('model'),
    });
    APP.DenominatorInfoCriteriaController = Ember.ObjectController.extend({
        denominatorValue: function () {
            if(this.get('model.Denominator')) {
                var parsedJson = JSON.parse(this.get('model.Denominator'));
                return parsedJson["value"];
            }
        }.property('model'),
    });
    APP.ExclusionsInfoCriteriaController = Ember.ObjectController.extend({
        numeratorExclusionsValue: function () {
            if(this.get('model.Numerator Exclusions')) {
                var parsedJson = JSON.parse(this.get('model.Numerator Exclusions'));
                return parsedJson["value"];
            }
        }.property('model'),
        denominatorExclusionsValue: function () {
            if(this.get('model.Denominator Exclusions')) {
                var parsedJson = JSON.parse(this.get('model.Denominator Exclusions'));
                return parsedJson["value"];
            }
        }.property('model'),
    });

    APP.ExceptionInfoCriteriaController = Ember.ObjectController.extend({
        exceptionValue:function(){
            if(this.get('model.Denominator Exceptions')) {
                var parsedJson = JSON.parse(this.get('model.Denominator Exceptions'));
                if (parsedJson["value"]) {
                    return parsedJson["value"];
                } else {
                    return 'No Exceptions for this measure';
                }
            }
        }.property('model'),
    });
    APP.CqmController = Ember.ArrayController.extend({
        isActive:true,
        isActiveOne:false,
        showExportAndCheck:false,
        cqmModel:null,
        scrollOffset:null,
        toggle: true,
        cmsIDForDummy:null,
        selectSingleProvider:false,
        selectMultipleProviders:false,
        anyMeasures:function(){
            if(this.get('model').length > 1){
                return true;
            } else {
                return false;
            }
        }.property('model'),
        watchSelectedSingleProvider:function(){
            if((this.get('selectSingleProvider')==true)){
                this.set('selectMultipleProviders',false);
            }
        }.observes('selectSingleProvider'),
        watchSelectMultipleProviders:function(){
           //this.toggleProperty('selectMultipleProviders');
            if((this.get('selectMultipleProviders')==true)){
                this.set('selectSingleProvider',false);
            }
        }.observes('selectMultipleProviders'),
        multipleProviders:function(){
            var provider = this.controllerFor('application').get('groupedProvider');
            if(this.controllerFor('application').get('groupedProvider')) {
                if (this.controllerFor('application').get('groupedProvider').length > 1) {
                    return true;
                } else {
                    return false;
                }
            }
        }.property('model'),
        providerModel:function(){
            if(this.controllerFor('application').get('groupedProvider')) {
                return this.controllerFor('application').get('groupedProvider');
            }
        }.property('model'),
        cmsId:function(){
            return this.controllerFor('measureInformation').get('cmsId');
        }.property(),
        dateCreated:function(){
            if(this.get('firstObject')) {
                var firstObject = this.get('firstObject');
                return moment(new Date(firstObject.dateCreated)).format('ll');
            } else {
                return 'None';
            }
        }.property('model'),

        // Select/Unselect All
        toggleCheckAll : true,

        // Last calculated date (mmelusky - 2/15/26)
        lastCalculated:function() {
            if(this.get('firstObject')) {
                var firstModel = this.get('firstObject');
                return moment(firstModel.dateCreated).format('MMM D,YYYY');
            } else {
                return 'None';
            }
        }.property('model'),

        startDate:function(){
            if(this.get('firstObject')) {
                var firstModel = this.get('firstObject');
                //var date = firstModel.reportingPeriodStart;
                var dateYear = firstModel.reportingPeriodStart[0] + firstModel.reportingPeriodStart[1] + firstModel.reportingPeriodStart[2] + firstModel.reportingPeriodStart[3];
                var dateMonth = firstModel.reportingPeriodStart[4] + firstModel.reportingPeriodStart[5];
                var dateDate = firstModel.reportingPeriodStart[6] + firstModel.reportingPeriodStart[7];
                var dat = dateYear + dateMonth + dateDate;
                return moment(dat, 'YYYY-MM-DD').format('MMM D,YYYY');
            } else {
                return 'None';
            }
        }.property('model'),

        endDate:function(){
            if(this.get('firstObject')) {
                var firstModel = this.get('firstObject');
                var dateYear = firstModel.reportingPeriodEnd[0] + firstModel.reportingPeriodEnd[1] + firstModel.reportingPeriodEnd[2] + firstModel.reportingPeriodEnd[3];
                var dateMonth = firstModel.reportingPeriodEnd[4] + firstModel.reportingPeriodEnd[5];
                var dateDate = firstModel.reportingPeriodEnd[6] + firstModel.reportingPeriodEnd[7];
                var dat = dateYear + dateMonth + dateDate;
                return moment(dat, 'YYYY-MM-DD').format('MMM D,YYYY');
            } else {
                return 'None';
            }
        }.property('model'),
        groupedResults: function () {
            if(this.get('model')) {
                this.set('cqmModel',this.get('model'));
                var result = Ember.A();
                var id = 1;
                this.get('model').forEach(function (item) {
                    //console.log(item.domainName);
                    var hasType = result.findBy('domainName', item.domainName);
                    if (!hasType) {
                        result.pushObject(Ember.Object.create({
                            id: id++,
                            domainName: item.domainName,
                            contents: []
                        }));
                    }
                    result.findBy('domainName', item.domainName).get('contents').pushObject(item);
                });
                return result;

            } else {
                return "There are no previous calculation to display";
            }
        }.property('model'),

        proxiedModel: Ember.computed.map('model', function (model) {
            return Ember.ObjectProxy.create({
                content: model,
                checked: false
            });

        }),
        proxiedCheckedItems: Ember.computed.filterBy('proxiedModel', 'isSelected', true),
        checkedItems: Ember.computed.mapBy('proxiedCheckedItems', 'content'),
        selectedCount: Ember.computed.alias('checkedItems.length'),
        numberOfSelectedCqm:function(){
            return this.get('selectedCount');
        }.property('selectedCount'),
        actions: {

            // Check all action
            checkAllClick : function() {
                if (this.get('groupedResults')) {
                    var toggleCheckAll = this.get("toggleCheckAll");
                    this.get('groupedResults').forEach(function (result) {
                        if (result.get("contents")) {
                            result.get("contents").forEach(function(item) {
                                Ember.set(item, "isSelected", toggleCheckAll);
                            });
                        }
                    });
                }
                this.toggleProperty("toggleCheckAll");
            },

            removeHide:function(item){
                console.log(item);
                var menuId = '#'+item;
                $(menuId).show();
            },

            scrollToCqmOne:function(){
                this.controllerFor('application').set('showCalculationButton',true);
                this.controllerFor('application').set('showRefreshButton',true);
                $('#organizationList').attr('disabled',false);
                $('#practiceList').attr('disabled',false);
                $('#providerList').attr('disabled',false);
                $('#daterange').attr('disabled',false);
                var id1 = this.controllerFor('application').get('providerSelected.id');
                var id = this.get('cmsId');
                var offset = this.get('scrollOffset');
                this.transitionToRoute('cqm',id1).then(function() {
                    console.log('Success');

                    if (id) {
                        var param = '#' + id;
                        console.log(param);

                        console.log($(param).val() + '------->' + $(param).html());

                        var $scrollTo = $('body,html').animate({scrollTop:offset},500);
                        console.log(param,$scrollTo,offset,offset+10);
                        this.transitionToRoute('cqm',id1).then($scrollTo);
                    }
                });

                // Reset check all button
                this.toggleProperty("toggleCheckAll");
                
            },
            goToLink: function (item,item2, cmsId) {
                console.log('RESET THE TIMER');
                ResetTimeOutTimer();
                //event.preventDefault();
                // var param = $(event.target).attr('target');
                var param = "#" + cmsId;
                var hideSideMenu = '#'+item2;
                var $scrollTo = $('body,html').animate({scrollTop: Ember.$(param).offset().top-240}, 500);
                var scrollOffset = Ember.$(param).offset().top;
                this.set('scrollOffset',scrollOffset);
                console.log(param,$scrollTo);
                $(hideSideMenu).hide();
                this.transitionToRoute(item.route).then( $scrollTo);
            },
            toggleMenu: function () {

                if (this.get('toggle')) {
                    $(".page-container").addClass("sidebar-collapsed").removeClass("sidebar-collapsed-back");
                    $("#menu span").css({"position": "absolute"});
                    $('.cqmContainer').animate({'margin-left':'17.5%'},300);

                }
                else {
                    $(".page-container").removeClass("sidebar-collapsed").addClass("sidebar-collapsed-back");
                    $('.cqmContainer').animate({'margin-left':'350px'},200);
                    setTimeout(function () {
                        $("#menu span").css({"position": "relative"});

                    }, 300);

                }
                this.toggleProperty('toggle');


            },

            scrollTop:function(){
                $('body,html').animate({
                    scrollTop: 0
                }, 800);
                return false;
            },
            doRefresh: function () {
                var provModel= null;
                var provId = this.controllerFor('application').get('providerSelected.id');
                if(provId >0){
                    $.ajax({
                        'type':"POST",
                        'async': false,
                        'global': false,
                        'url': '/api/all_active_measures/'+provId,
                        'dataType': "json",
                        'success': function (data) {
                            provModel = data;
                            console.log(provModel);
                        }
                    });
                    console.log('REFRESHIN TO CQM :'+provId);
                    // this.transitionTo('cqm',provId);
                    this.controllerFor('application').set('showCalculationButton',true);
                    this.set('model',provModel);//get('target.router').refresh();

                    // Reset this flag
                    this.set("toggleCheckAll", false);
                    this.send("checkAllClick");
                }
                /* this.controllerFor('application').set('showCalculationButton',true);
                 this.get('target.router').refresh();*/
            },
            scrollToOffset:function(item){
                console.log('RESET THE TIMER');
                ResetTimeOutTimer();
                var id = '#'+item;
                this.set('cmsIDForDummy',item);
                var newOffset = Ember.$(id).offset().top-240;
                this.set('scrollOffset',newOffset);
                this.controllerFor('application').set('showCalculationButton',false);
                $('#organizationList').attr('disabled',true);
                $('#practiceList').attr('disabled',true);
                $('#providerList').attr('disabled',true);
                $('#daterange').attr('disabled',true);
                this.controllerFor('application').set('showRefreshButton',false);
            },
            exportZipCQM: function () {
                this.set('selectSingleProvider',false);
                this.set('selectMultipleProviders',false);
                console.log('RESET THE TIMER');
                ResetTimeOutTimer();
                // Create a "form" object and submit a post to download the xml file
                var form = $('<form></form>');

                form.attr("method", "post");
                form.attr("action", '/extract/qrda_cat1/export?_csrf=' + csrf_token);

                var selected = this.get('checkedItems');
                console.log(selected);
                selected.forEach(function (item) {
                    var field = $('<input></input>');
                    field.attr("type", "hidden");
                    field.attr("name", "hqmfId");
                    field.attr("value", item.hqmfDocumentId);
                    form.append(field);
                });

                // The form needs to be a part of the document in
                // order for us to be able to submit it.
                $(document.body).append(form);
                form.submit();
            },
            exportXmlCQM: function () {
                console.log('RESET THE TIMER');
                ResetTimeOutTimer();
                // Create a "form" object and submit a post to download the xml file
                var form = $('<form></form>');

                form.attr("method", "post");
                form.attr("action", '/extract/qrda_cat3/export?_csrf=' + csrf_token);

                var selected = this.get('checkedItems');
                console.log(selected);
                selected.forEach(function (item) {
                    var field = $('<input></input>');
                    field.attr("type", "hidden");
                    field.attr("name", "hqmfId");
                    field.attr("value", item.hqmfDocumentId);
                    form.append(field);
                });

                // The form needs to be a part of the document in
                // order for us to be able to submit it.
                $(document.body).append(form);
                form.submit();
            },
            exportPracticeLevel:function(){

                ResetTimeOutTimer();
                var form = $('<form></form>');
                form.attr("method","post");
                form.attr("action",'/extract/excel_format/exportPracticeLevel?_csrf='+csrf_token);
                var selected = this.get('checkedItems');
                var providerSelected = this.controllerFor('application').get('providerSelected.id');
                var providerList = this.controllerFor('application').get('groupedProvider');
                var practiceName = $('#practiceList option:selected').text();
                var organizationName = $('#organizationList option:selected').text();
                console.log('**********************************PROVIDER list :'+providerList);
                var field1 = $('<input></input>');
                field1.attr('type','hidden');
                field1.attr('name','practiceName');
                field1.attr('value',practiceName);
                form.append(field1);

                providerList.forEach(function(item){
                   var field2 = $('<input></input>');
                    field2.attr('type','hidden');
                    field2.attr('name','providerList');
                    field2.attr('value',item.id);
                    form.append(field2);
                });
                providerList.forEach(function(item){
                    var field3 = $('<input></input>');
                    field3.attr('type','hidden');
                    field3.attr('name','providerName');
                    field3.attr('value',item.fullName);
                    form.append(field3);
                });
                providerList.forEach(function(item){
                    var field4 = $('<input></input>');
                    field4.attr('type','hidden');
                    field4.attr('name','providerNPI');
                    field4.attr('value',item.npi);
                    form.append(field4);
                });
                var field5 = $('<input></input>');
                field5.attr('type','hidden');
                field5.attr('name','organizationName');
                field5.attr('value',organizationName);
                form.append(field5);

                /*selected.forEach(function(item){
                    var field = $('<input></input>');
                    field.attr("type","hidden");
                    field.attr("name","hqmfId");
                    field.attr("value",item.hqmfDocumentId);
                    form.append(field);
                });*/

                $(document.body).append(form);
                form.submit();

            },

            exportProviderLevel:function(){

                ResetTimeOutTimer();
                var form = $('<form></form>');
                form.attr("method","post");
                form.attr("action",'/extract/excel_format/exportProviderLevel?_csrf='+csrf_token);
                var selected = this.get('checkedItems');
                var providerSelected = this.controllerFor('application').get('providerSelected.id');
                var providerList = this.controllerFor('application').get('groupedProvider');
                var practiceName = $('#practiceList option:selected').text();
                var organizationName = $('#organizationList option:selected').text();
                var selectedProviderName = $('#providerList option:selected').text();
                var measuresSelected = this.get('checkedItems');
                console.log('**********************************PROVIDER list :'+providerList);
                var field1 = $('<input></input>');
                field1.attr('type','hidden');
                field1.attr('name','practiceName');
                field1.attr('value',practiceName);
                form.append(field1);

                providerList.forEach(function(item){
                    if(item.id == providerSelected){
                        var field2 = $('<input></input>');
                        field2.attr('type','hidden');
                        field2.attr('name','selectedProviderid');
                        field2.attr('value',item.id);
                        form.append(field2);
                    }
                });
                providerList.forEach(function(item){
                    if(item.fullName == selectedProviderName){
                        var field3 = $('<input></input>');
                        field3.attr('type','hidden');
                        field3.attr('name','selectedProviderName');
                        field3.attr('value',item.fullName);
                        form.append(field3);

                        var field4 = $('<input></input>');
                        field4.attr('type','hidden');
                        field4.attr('name','selectedProviderNPI');
                        field4.attr('value',item.npi);
                        form.append(field4);
                    }
                });

                var field5 = $('<input></input>');
                field5.attr('type','hidden');
                field5.attr('name','organizationName');
                field5.attr('value',organizationName);
                form.append(field5);

                measuresSelected.forEach(function(item){
                    var field6 =$('<input></input>');
                    field6.attr('type','hidden');
                    field6.attr('name','measuresSelected');
                    field6.attr('value',item.cmsId);
                    form.append(field6);
                });

                $(document.body).append(form);
                form.submit();

            },
            clickOnXmlTab:function(){
                if(this.get('isActiveOne')) {
                    this.toggleProperty("isActiveOne");
                    this.toggleProperty("isActive");
                }
                this.set('selectSingleProvider',false);
                this.set('selectMultipleProviders',false);
            },
            clickOnExcelTab:function(){
                if(this.get('isActive')) {
                    this.toggleProperty("isActive");
                    this.toggleProperty("isActiveOne");
                }
            },
            clearOptions:function(){
                this.toggleProperty("isActiveOne");

                this.set('selectSingleProvider',false);
                this.set('selectMultipleProviders',false);

                this.toggleProperty("isActive");

                $('#excelExportTab').removeClass('active');

            }

        }
    });

    APP.DummyTemplateController = Ember.Controller.extend({

        submeasureTitle:function(){
            return this.get('model.submeasureTitle');
        }.property('model'),
        numeratorId: function(){
            return this.get('model.numeratorResultId');
        }.property('model'),
        ippId:function(){
            if(this.get('model.ippResultId')) {
                return this.get('model.ippResultId');
            } else {
                return 0;
            }
        }.property('model'),
        ippCount:function(){
            return this.get('model.ippCount');
        }.property('model'),
        numeratorCount:function(){
            return this.get('model.numeratorCount');
        }.property('model'),
        denominatorCount:function(){
            return this.get('model.denominatorCount');
        }.property('model'),
        denexCount:function(){
            return this.get('model.denexCount');
        }.property('model'),
        exceptionCount:function(){
            if(this.get('model.denexcepCount')) {
                return this.get('model.denexcepCount');
            } else {
                return 0;
            }
        }.property('model'),
        denominatorId:function(){
            return this.get('model.denominatorResultId')
        }.property('model'),
        exclusionId:function(){
            if(this.get('model.denexResultId')) {
                return this.get('model.denexResultId');
            } else {
                return 0;
            }
        }.property('model'),
        exceptionId:function(){
            if(this.get('model.denexcepResultId')) {
                return this.get('model.denexcepResultId');
            } else {
                return 0;
            }
        }.property('model'),
    });

    APP.MeasureInformationController = Ember.Controller.extend({
        /* currentPathChanged: function () {
         window.scrollTo(0, 0);
         }.observes('currentPath'),*/
        cmsId:function(){
          this.get('model.cmsId');
        },
        measureAttributes:null,
        needs:['application'],
        href1:Ember.computed.alias('controllers.application.providerSelected.id'),
        href:function() {
            return '/#/cqm/' + this.get('href1');
        }.property('href1'),
        ippCount:function(){


            if(this.controllerFor('dummyTemplate').get('ippCount')){
                return this.controllerFor('dummyTemplate').get('ippCount')
            } else {
                return 0;
            }
        }.property('model'),
        numeratorCount:function(){
            if(this.controllerFor('dummyTemplate').get('numeratorCount')){
                return this.controllerFor('dummyTemplate').get('numeratorCount');
            } else {
                return 0;
            }
        }.property('model'),
        denominatorCount:function(){
            if(this.controllerFor('dummyTemplate').get('denominatorCount')){
                return this.controllerFor('dummyTemplate').get('denominatorCount');
            } else {
                return 0;
            }
        }.property('model'),
        denexCount:function(){
            if(this.controllerFor('dummyTemplate').get('denexCount')){
                return this.controllerFor('dummyTemplate').get('denexCount');
            } else {
                return 0;
            }
        }.property('model'),
        exceptionCount:function(){
            return this.controllerFor('dummyTemplate').get('exceptionCount');
        }.property('model'),
        domainName:function(){
            return this.get('model.domainName')
        }.property('model'),
        hqmfDocumentId:function(){
            return this.get('model.hqmfDocumentId');
        }.property('model'),

        measureGuidance:function(){
            var json = null;
            var hqmf = this.get('hqmfDocumentId');
           /* return Em.$.ajax({
                "type":"POST",
                'url': '/api/patient_results/' + this.controllerFor('dummyTemplate').get('numeratorId'),
                'async': true,
                'global': false,
                'success': function (data) {
                    return data;
                }
            })*/
            if(hqmf) {
                $.ajax({
                    "type": "POST",
                    'async': false,
                    'global': false,
                    'url': '/api/measure_attributes/' + hqmf,
                    'dataType': "json",
                    'success': function (data) {
                        json = data;
                    }
                });
                this.set('measureAttributes', json);
                var parsedJson = JSON.parse(json.Guidance);
                return parsedJson["value"];
            }

        }.property('model'),
        modelForInfo:function(){
            var hqmfDocumentId = this.get('hqmfDocumentId');
            var result = null;
            if(this.get('measureAttributes')){
                result = this.get('measureAttributes');
            }
            /* $.ajax({
                 'async': false,
                 'global': false,
                 'url': '/api/measure_attributes/'+hqmfDocumentId,
                 'dataType': "json",
                 'success': function (data) {
                     result = data;
                 }
             });*/
            return result;
        }.property('model'),
        showExclusionTab:function(){
            if(this.get('measureAttributes.Denominator Exclusions')) {
                var parsedJson = JSON.parse(this.get('measureAttributes.Denominator Exclusions'));
                if (parsedJson["value"] == 'None' || parsedJson["value"] == 'Not Applicable') {
                    return false
                } else {
                    return true
                }
            }
        }.property('model'),
        showExceptionTab:function(){
            if(this.get('measureAttributes.Denominator Exceptions')) {
                var parsedJson = JSON.parse(this.get('measureAttributes.Denominator Exceptions'));
                if (parsedJson["value"] == 'None' || parsedJson["value"] == 'Not Applicable') {
                    return false
                } else {
                    return true
                }
            }
        }.property('model'),
        measureStratifications:function(){
            var json = null;
           // var hqmf = this.get('hqmfDocumentId');
            if(this.get('measureAttributes')){
                json = this.get('measureAttributes');
            }
           /* $.ajax({
                'async': false,
                'global': false,
                'url': '/api/measure_attributes/'+hqmf,
                'dataType': "json",
                'success': function (data) {
                    json = data;
                }
            });*/
            if(json) {
                var parsedJson = JSON.parse(json.Stratification);
                return parsedJson["value"];
            }
        }.property('model'),
        title : function(){
            return this.get('model.title');
        }.property('model.title'),
        sbTitle:function(){
            var dat = this.get('model');
            return dat.populationSetResults;
        }.property('model'),
        submeasuretitle:function(){
            var subTitle =this.controllerFor('dummyTemplate').get('submeasureTitle');
            if(subTitle){
                return subTitle;
            } else {
                return 'None';
            }
        }.property('model'),
        description : function(){
            if(this.get('model.description')) {
                return this.get('model.description');
            }
        }.property('model.id'),
        cmsId:function(){
            return this.get('model.cmsId');
        }.property('model.cmsId'),
        populationSetResults:function(){
           return this.get('model.populationSetResults');

        }.property('model.populationSetResults'),
        //guidance:function(){
        //  return this.controllerFor('numeratorInfoCriteria').get('guidanceValue');
        //}.property(),

        column: function () {
            return "margin-bottom:0px;"
        }.property(),
        measureInfo: function () {
            return "padding:10px; height:auto;  border-radius:5px;margin-top:20px;margin-top:50px;"
        }.property(),
        measureInfoBackground: function () {
            return "padding:10px; height:auto; border-radius:5px;background-color:white;"
        }.property(),
        eachMeasureTitle: function () {
            return "padding:10px; font-size:17px;text-align:right;"
        }.property(),

        actions:{
            scrollToCqm:function(item){
                console.log('RESET THE TIMER');
                ResetTimeOutTimer();
                var id = this.controllerFor('application').get('providerSelected.id');
                this.transitionToRoute(item,id);

                this.controllerFor('cqm').send('scrollToCqmOne');

            }
        }
    });

})();