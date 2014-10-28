var rootURL = 'https://office-mover.firebaseio.com/';
var furnitureURL = rootURL + "furniture/";

var rootRef = new Firebase(rootURL);
var furnitureRef = new Firebase(furnitureURL);

var furnitureTemplates = {
  desk: "<div class='editor-furniture editor-desk'></div>",
  plant: "<div class='editor-furniture editor-plant'></div>"
};

var state = {};
var draggableOptions = {
  start: function(event, ui){
    var $eventTarget = $(event.target);
    var location = $eventTarget.data('id');
    var itemRef;

    $eventTarget.addClass("is-editor-furniture-active");

    state[location] = {
      ref: new Firebase(location)
    };

    itemRef = state[location].ref;
    itemRef.child("locked").set(true);
  },

  drag: function(event, ui){
    var $eventTarget = $(event.target);
    var location = $eventTarget.data('id');
    var itemRef = state[location].ref;

    itemRef.child("left").set(ui.position.left);
    itemRef.child("top").set(ui.position.top);
  },

  stop: function(event, ui){
    var $eventTarget = $(event.target);
    var location = $eventTarget.data('id');
    var itemRef = state[location].ref;

    $eventTarget.removeClass("is-editor-furniture-active");

    itemRef.child("locked").set(false);
  }
};


var render = function(state){
  var $furnitures = _.map(state, function(furniture, index){
    var $furniture = $(furnitureTemplates[furniture.type]);
    var url = furnitureURL + index;

    $furniture.data("id", url);
    $furniture.draggable(draggableOptions);
    $furniture.css({
      "top": parseInt(furniture.top, 10),
      "left": parseInt(furniture.left, 10)
    });

    return $furniture;
  });

  $(".editor").empty().append($furnitures);

};

furnitureRef.once("value", function(snapshot){
  state = snapshot.val();
  render(state);
});

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

  // MAKE DRAGGABLE WITH draggableOptions AND APPEND TO DOM
  $item.data('id', itemID);
  $item.draggable(draggableOptions);

  $(".editor").append($item);
});
