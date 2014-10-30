var utils  = require('../helpers/utils');
var rootRef = new Firebase(utils.urls.root);

/*
* Welcome module
*
* This is the module that sets up the welcome page and Google login
*/


var welcome = {

  $error: null,
  $signInButtons: null,

  init: function(){
    var self = this;
    
    this.$error = $(".error");
    this.$signInButtons = $(".welcome-hero-signin");

    // SETUP LOGIN BUTTON
    this.$signInButtons.on("click", function(e){
      var provider = $(this).data("provider");

      rootRef.authWithOAuthPopup(provider, function(error, authData){
        if (error){
          self.$error.removeClass("error-hide");
        }
        else {
          self.$error.addClass("error-hide");
        }
      });
    });

  }
};

module.exports = welcome;