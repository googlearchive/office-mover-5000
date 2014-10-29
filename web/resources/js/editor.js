var utils = require("./helpers/utils");
var dragOptions = require("./helpers/drag-options");

var rootRef = new Firebase(utils.urls.root);
var furnitureRef = new Firebase(utils.urls.furniture);

var furnitureTemplates = {
  desk: "<div class='editor-furniture editor-desk'></div>",
  plant: "<div class='editor-furniture editor-plant'></div>"
};


var editor = {
  init: function(){

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
