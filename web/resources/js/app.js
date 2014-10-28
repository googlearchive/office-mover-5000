var utils  = require('./helpers/utils');


/*
* Application Module
*
* This is the main module that initializes the entire application.
*/

var app = {
  init: function() {
    console.log(utils.urls.root);
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