var utils  = require('../helpers/utils');
var rootRef = new Firebase(utils.urls.root);

/*
* Welcome module
*
* This is the module that sets up the welcome page and Google login
*/

var $welcome = $("#welcome");
var $app = $("#app");
var $signInButtons = $(".welcome-hero-signin");
var $error = $(".error");

var updateUIForLogout = function(){
  $welcome.removeClass("is-hidden");
  $app.addClass("is-hidden");
};

var updateUIForLogin = function(){
  $welcome.addClass("is-hidden");
  $app.removeClass("is-hidden");
};

var welcome = {

  init: function(){

    // SETUP LOGIN BUTTON
    $signInButtons.on("click", function(e){
      var provider = $(this).data("provider");

      rootRef.authWithOAuthPopup(provider, function(error, authData){
        if (error){
          $error.removeClass("error-hide");
        }
        else {
          $error.addClass("error-hide");
          updateUIForLogin();
        }
      });
    });

    // SETUP LOGOUT BUTTON
    $(".toolbar-sign-out").on("click", function(e){
      rootRef.unauth();
    });

    // SET AUTH LISTENER
    rootRef.onAuth(function(authData){
      if (authData){
        updateUIForLogin();  // USER IS LOGGED IN
      }
      else {
        updateUIForLogout(); // USER IS LOGGED OUT
      }
    });
  }
};

module.exports = welcome;