var Utils  = require('./helpers/utils');
var Furniture  = require('./components/furniture');
var rootRef = new Firebase(Utils.urls.root);
var furnitureRef = new Firebase(Utils.urls.furniture);
var editor = require('./editor');


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
       self.createFurniture(snapshot, {

       });
    });
  },

  createFurniture: function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      new Furniture(childSnapshot);
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