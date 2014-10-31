var Utils  = require('./helpers/utils');
var data  = require('./helpers/data');
var Dropdown = require('./components/dropdown');
var Furniture  = require('./components/furniture');
var welcome = require('./components/welcome');
var rootRef = new Firebase(Utils.urls.root);
var furnitureRef = new Firebase(Utils.urls.furniture);

/*
* Application Module
*
* This is the main module that initializes the entire application.
*/

var app = {

  // REGISTER ELEMENTS
  $welcome: null,
  $app: null,
  $signInButtons: null,
  $alert: null,
  $signOutButton: null,

  // HIDE / SHOW WELCOME SCREEN
  showWelcomeScreen: function(){
    this.$welcome.removeClass("is-hidden");
    this.$app.addClass("is-hidden");
  },

  hideWelcomeScreen: function(){
    this.$welcome.addClass("is-hidden");
    this.$app.removeClass("is-hidden");
  },

  /*
  * Initalize the application
  *
  * Get intials dump of Firebase furniture data.
  */

  init: function() {
    var self = this;
    // REGISTER ELEMENTS
    this.$welcome = $("#welcome");
    this.$app = $("#app");
    this.$signInButtons = $(".welcome-hero-signin");
    this.$alert = $(".alert");
    this.$signOutButton = $(".toolbar-sign-out");



    this.createDropdowns();
    welcome.init();                 // SET UP HOME PAGE
    this.logout();                  // SET UP LOGOUT FUNCTIONALITY
    this.checkUserAuthentication(); // SET AUTH LISTENER
    this.renderFurniture();         // RENDER FURNITURE
  },

  changeBackground: function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      new Furniture(childSnapshot);
    });
  },

  createFurniture: function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      new Furniture(childSnapshot);
    });
  },



  createDropdowns: function() {
    var $addFurniture = $('#add-furniture');
    var $addBackground = $('#select-background');

    this.furnitureDropdown = new Dropdown($addFurniture, data.furniture, 'furniture');
    this.backgroundDropdown = new Dropdown($addBackground, data.backgrounds, 'background');

    $('.dropdown').on('click', '.dropdown-button', function(e) {
      e.preventDefault();
      var button = $(e.currentTarget);
      var type = button.data('type');

      switch(type) {
        case 'furniture': self.createFurniture(); break;
        case 'background': self.changeBackground(); break;
      }
    });
  },

  checkUserAuthentication: function(){
    var self = this;

    rootRef.onAuth(function(authData){
      if (authData) {
        self.hideWelcomeScreen();
      }
      else {
        self.showWelcomeScreen();
      }
    });
  },

  renderFurniture: function(){
    var self = this;

    furnitureRef.once("value", function(snapshot){
       self.createFurniture(snapshot, {});
    });
  },

  logout: function(){
    // SETUP LOGOUT BUTTON
    this.$signOutButton.on("click", function(e){
      rootRef.unauth();
    });
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