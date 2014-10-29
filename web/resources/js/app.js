var utils  = require('./helpers/utils');
var editor = require('./editor');


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