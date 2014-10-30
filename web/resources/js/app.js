var Utils  = require('./helpers/utils');
var data  = require('./helpers/data');
var Dropdown = require('./components/dropdown');
var Furniture  = require('./components/furniture');
var rootRef = new Firebase(Utils.urls.root);
var furnitureRef = new Firebase(Utils.urls.furniture);


/*
* Application Module
*
* This is the main module that initializes the entire application.
*/

var app = {

  /*
  * Initalize the application
  *
  * Get intials dump of Firebase furniture data.
  */

  init: function() {
    var self = this;

    furnitureRef.once("value", function(snapshot){
       self.createFurniture(snapshot);
    });

    this.createDropdowns();
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
    this.backgroundDropdown = new Dropdown($addBackground, data.backgrounds, 'backgrounds');
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