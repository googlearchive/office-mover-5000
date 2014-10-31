var Utils  = require('./helpers/utils');
var data  = require('./helpers/data');
var userProfile = require('./components/user-profile');
var Dropdown = require('./components/dropdown');
var Furniture  = require('./components/furniture');
var welcome = require('./components/welcome');
var rootRef = new Firebase(Utils.urls.root);
var furnitureRef = new Firebase(Utils.urls.furniture);
var backgroundRef = new Firebase(Utils.urls.background);

/*
* Application Module
*
* This is the main module that initializes the entire application.
*/

var app = {
  $welcome: null,
  $app: null,
  $signInButtons: null,
  $alert: null,
  $signOutButton: null,


  /*
  * Initalize the application
  *
  * Get intials dump of Firebase furniture data.
  */

  init: function() {

    // REGISTER ELEMENTS
    this.$welcome = $("#welcome");
    this.$app = $("#app");
    this.$officeSpace = $("#office-space");
    this.$signInButtons = $(".welcome-hero-signin");
    this.$alert = $(".alert");
    this.$signOutButton = $(".toolbar-sign-out");

    //INITIALIZE APP
    welcome.init();
    this.checkUserAuthentication();
    this.createDropdowns();
    this.setOfficeBackground();
    this.logout();
  },

  checkUserAuthentication: function(){
    var self = this;

    rootRef.onAuth(function(authData){
      if (authData) {
        self.hideWelcomeScreen();
        self.renderFurniture();
        userProfile.init(authData);
      }
      else {
        self.showWelcomeScreen();
      }
    });
  },

  createDropdowns: function() {
    var self = this;
    var $addFurniture = $('#add-furniture');
    var $addBackground = $('#select-background');

    this.furnitureDropdown = new Dropdown($addFurniture, data.furniture, 'furniture');
    this.backgroundDropdown = new Dropdown($addBackground, data.backgrounds, 'background');

    $('.dropdown').on('click', '.dropdown-button', function(e) {
      e.preventDefault();
      var button = $(e.currentTarget);
      var type = button.data('type');
      var name = button.data('name');

      switch(type) {
        case 'furniture': self.addFurniture(name); break;
        case 'background': self.changeBackground(name); break;
      }
    });
  },

  changeBackground: function(name) {
    backgroundRef.set(name);
  },

  setOfficeBackground: function() {
    var self = this;

    backgroundRef.on('value', function(snapshot) {
      var value = snapshot.val();
      var pattern = value ? 'background-' + value : '';

      self.$officeSpace.removeClass().addClass('editor ' +  pattern);
    });
  },

  addFurniture: function(type) {
    furnitureRef.push({
      top: 400,
      left: 300,
      type: type,
      rotation: 0,
      locked: false,
      name: ""
    });
  },

  createFurniture: function(snapshot) {
    new Furniture(snapshot);
  },

  renderFurniture: function(){
    var self = this;

    furnitureRef.once("value", function(snapshot){
      snapshot.forEach(function(childSnapshot) {
        new Furniture(childSnapshot);
      });
    });

    furnitureRef.on("child_added", function(snapshot){
      self.createFurniture(snapshot);
    });
  },

  logout: function(){
    this.$signOutButton.on("click", function(e){
      rootRef.unauth();
    });
  },

  showWelcomeScreen: function(){
    this.$welcome.removeClass("is-hidden");
    this.$app.addClass("is-hidden");
  },

  hideWelcomeScreen: function(){
    this.$welcome.addClass("is-hidden");
    this.$app.removeClass("is-hidden");
  }
};


/*
* Initialize App
*
*/

$(document).ready(function() {
  app.init();
});


/*
* Export App
*
*/

module.exports = app;