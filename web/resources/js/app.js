var Utils  = require('./helpers/utils');
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
  $error: null,
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
    this.$error = $(".error");
    this.$signOutButton = $(".toolbar-sign-out");

    welcome.init();         // SET UP HOME PAGE
    this.logout();          // SET UP LOGOUT FUNCTIONALITY
    this.setAuthListener(); // SET AUTH LISTENER
    this.renderFurniture(); // RENDER FURNITURE
  },

  createFurniture: function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      new Furniture(childSnapshot);
    });
  },

  setAuthListener: function(){
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
    $signOutButton.on("click", function(e){
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