/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;
/******/
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	var Utils  = __webpack_require__(1);
	var Furniture  = __webpack_require__(2);
	var welcome = __webpack_require__(3);
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

	    welcome.init();                 // SET UP HOME PAGE
	    this.logout();                  // SET UP LOGOUT FUNCTIONALITY
	    this.checkUserAuthentication(); // SET AUTH LISTENER
	    this.renderFurniture();         // RENDER FURNITURE
	  },

	  createFurniture: function(snapshot) {
	    snapshot.forEach(function(childSnapshot) {
	      new Furniture(childSnapshot);
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

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	/*
	* Helper
	*
	*/

	var root = 'https://mover-app-5000-demo.firebaseio.com/';

	var utils = {
	  urls: {
	    root: root,
	    furniture: root + 'furniture/',
	    background: root + 'background/'
	  }
	};

	module.exports = utils;

/***/ },
/* 2 */
/***/ function(module, exports, __webpack_require__) {

	var utils  = __webpack_require__(1);
	var furnitureRef = new Firebase(utils.urls.furniture);

	/*
	* FURNITURE MODULES
	*
	* This is a furniture class and must be instaniated like
	* a normal class with the "new" keyword.
	*/

	var Furniture = function(snapshot, options) {
	  options = options || {};
	  var self = this;
	  var data = snapshot.val();

	  /*
	  * Register Furniture Values
	  *
	  */

	  this.officeSpace = $('#office-space');
	  this.element = $("<div class='furniture'></div>");
	  this.id = snapshot.name();
	  this.ref = snapshot.ref();
	  this.type = data.type;
	  this.locked = data.locked;
	  this.rotation = data.rotation;
	  this.top = data.top;
	  this.left = data.left;
	  this.name = data.name;


	  /*
	  * Create Firebase Reference
	  *
	  */

	  this.ref  = new Firebase(utils.urls.furniture + this.id);


	  /*
	  * Create Furniture Method
	  *
	  */

	  this.createElement = function() {

	    //SET DRAG OPTIONS
	    this.element.draggable({
	      containment: self.officeSpace,
	      start: function(event, ui){
	        self.element.addClass("is-active");
	        self.ref.child("locked").set(true);
	      },

	      drag: function(event, ui){
	        self.ref.child("left").set(ui.position.left);
	        self.ref.child("top").set(ui.position.top);
	      },

	      stop: function(event, ui){
	        self.element.removeClass("is-active");
	        self.ref.child("locked").set(false);
	      }
	    });

	    // SET CURRENT LOCATION
	    this.element
	    .addClass(this.type)
	    .css({
	      "top": parseInt(this.top, 10),
	      "left": parseInt(this.left, 10)
	    });

	    // ADD TO DOM
	    this.officeSpace.append(this.element);
	  };


	  /*
	  * Create Furniture Element
	  *
	  */

	  this.createElement();
	};

	module.exports = Furniture;

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	var utils  = __webpack_require__(1);
	var rootRef = new Firebase(utils.urls.root);

	/*
	* Welcome module
	*
	* This is the module that sets up the welcome page and Google login
	*/


	var welcome = {

	  $alert: null,
	  $signInButtons: null,

	  init: function(){
	    var self = this;

	    this.$alert = $(".alert");
	    this.$signInButtons = $(".welcome-hero-signin");

	    // SETUP LOGIN BUTTON
	    this.$signInButtons.on("click", function(e){
	      var provider = $(this).data("provider");

	      rootRef.authWithOAuthPopup(provider, function(error, authData){
	        if (error){
	          console.log(error);
	          self.$alert.removeClass("is-hidden");
	        }
	        else {
	          self.$alert.addClass("is-hidden");
	        }
	      });
	    });

	  }
	};

	module.exports = welcome;

/***/ }
/******/ ])