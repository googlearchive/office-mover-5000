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

	var utils  = __webpack_require__(2);
	var editor = __webpack_require__(1);


	/*
	* Application Module
	*
	* This is the main module that initializes the entire application.
	*/

	var app = {
	  init: function() {
	    editor.init();
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

	var utils = __webpack_require__(2);
	var dragOptions = __webpack_require__(3);

	var rootRef = new Firebase(utils.urls.root);
	var furnitureRef = new Firebase(utils.urls.furniture);

	var furnitureTemplates = {
	  desk: "<div class='editor-furniture editor-desk'></div>",
	  plant: "<div class='editor-furniture editor-plant'></div>"
	};

	var $loggedInElements =  $(".mover-header > .logo," +
	                           ".mover-header > .title," +
	                           ".mover-header > .mover-sign-out," +
	                           ".editor");

	var $loggedOutElements = $(".buzzwords," +
	                           ".error," +
	                           ".welcome-hero");

	var updateUIForLogout = function(){
	  $loggedOutElements.removeClass("hide");
	  $loggedInElements.addClass("hide");
	};

	var updateUIForLogin = function(){
	  $loggedOutElements.addClass("hide");
	  $loggedInElements.removeClass("hide");
	};

	var editor = {

	  init: function(){
	    // SETUP LOGIN BUTTON
	    $(".google-signin").on("click", function(e){
	      rootRef.authWithOAuthPopup("google", function(error, authData){
	        if (error){
	          $(".error").removeClass("error-hide");
	        }
	        else {
	          updateUIForLogin();
	        }
	      });
	    });

	    $(".mover-sign-out").on("click", function(e){
	      rootRef.unauth();
	    });

	    rootRef.onAuth(function(authData){
	      if (authData){
	        updateUIForLogin();  // USER IS LOGGED IN
	      }
	      else {
	        updateUIForLogout(); // USER IS LOGGED OUT
	      }
	    });

	    // GET FURNITURE POSITIONS
	    furnitureRef.once("value", function(snapshot){
	      var state = snapshot.val();
	      this.render(state);
	    }.bind(this));

	    // SET LISTENERS ON NEW FURNITURE BUTTONS
	    $(".editor-new").on("click", function(e){

	      // MAKE JQUERY OBJECT FOR PIECE OF FURNITURE
	      var itemName = $(this).data("name");          // DESK, PLANT, etc.
	      var $item = $(furnitureTemplates[itemName]);  // jQUERY OBJECT
	      var newItemRef = furnitureRef.push({          // PUSH TO FIREBASE
	        type: itemName,
	        top: 0,
	        left: 0,
	        locked: false,
	        name: "",
	        rotation: 0
	      });
	      var itemID = newItemRef.toString();

	      // MAKE DRAGGABLE WITH dragOptions AND APPEND TO DOM
	      $item.data('id', itemID);
	      $item.draggable(dragOptions);
	      $(".editor").append($item);
	    });
	  },

	  render: function(state){
	    var $furnitures = _.map(state, function(furniture, index){
	      var $furniture = $(furnitureTemplates[furniture.type]);
	      var url = utils.urls.furniture + index;

	      $furniture.data("id", url);
	      $furniture.draggable(dragOptions);
	      $furniture.css({
	        "top": parseInt(furniture.top, 10),
	        "left": parseInt(furniture.left, 10)
	      });


	      return $furniture;
	    });

	    $(".editor").empty().append($furnitures);
	  }
	};

	module.exports = editor;


/***/ },
/* 2 */
/***/ function(module, exports, __webpack_require__) {

	/*
	* Helper
	*
	*/

	var root = 'https://office-mover.firebaseio.com/';

	var utils = {
	  urls: {
	    root: root,
	    furniture: root + 'furniture/',
	    background: root + 'background/'
	  }
	};

	module.exports = utils;

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	/*
	* Drag and Drop Settings
	*
	*/
	var state = {};

	var dragOptions = {
	  start: function(event, ui){
	    var $eventTarget = $(event.target);
	    var location = $eventTarget.data('id');
	    var itemRef;

	    state[location] = {
	      ref: new Firebase(location)
	    };

	    $eventTarget.addClass("is-editor-furniture-active");
	    itemRef = state[location].ref;
	    itemRef.child("locked").set(true);
	  },

	  drag: function(event, ui){
	    var $eventTarget = $(event.target);
	    var location = $eventTarget.data('id');
	    var itemRef = state[location].ref;

	    itemRef.child("left").set(ui.position.left);
	    itemRef.child("top").set(ui.position.top);
	  },

	  stop: function(event, ui){
	    var $eventTarget = $(event.target);
	    var location = $eventTarget.data('id');
	    var itemRef = state[location].ref;

	    $eventTarget.removeClass("is-editor-furniture-active");
	    itemRef.child("locked").set(false);
	  }
	};

	module.exports = dragOptions;

/***/ }
/******/ ])