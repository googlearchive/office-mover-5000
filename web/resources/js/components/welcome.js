var utils  = require('../helpers/utils');
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

    // REGISTER ELEMENTS
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


// EXPORT MODULE
module.exports = welcome;