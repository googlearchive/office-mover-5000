var utils = require("./helpers/utils");
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

    // SETUP LOGOUT BUTTON
    $(".mover-sign-out").on("click", function(e){
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

    // GET FURNITURE POSITIONS AND RENDER
    furnitureRef.once("value", function(snapshot){
      var state = snapshot.val();
      this.render(state);
    }.bind(this));

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
