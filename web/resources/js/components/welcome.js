var utils  = require('../helpers/utils');
var rootRef = new Firebase(utils.urls.root);

/*
* Welcome module
*
* This is the module that sets up the welcome page and Google login
*/

var $loggedInElements =  $(".toolbar > .toolbar-menu," +
                           ".toolbar > .toolbar-title," +
                           ".toolbar > .toolbar-sign-out," +
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

var welcome = {

  init: function(){
    // SETUP LOGIN BUTTON
    $(".google-signin").on("click", function(e){
      rootRef.authWithOAuthPopup("google", function(error, authData){
        if (error){
          $(".error").removeClass("error-hide");
        }
        else {
          $(".error").addClass("error-hide");
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

    // SET LISTENERS ON ADD FURNITURE BUTTONS
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
  }
};

module.exports = welcome;