/*
* Drag and Drop Settings
*
*/
var state = {};

var dragOptions = {
  start: function(event, ui){
    var $eventTarget = $(event.target);
    var location = $eventTarget.data('id');
    var itemRef;

    state[location] = {
      ref: new Firebase(location)
    };

    $eventTarget.addClass("is-editor-furniture-active");
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

module.exports = dragOptions;