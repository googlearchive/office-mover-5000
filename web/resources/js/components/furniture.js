var utils  = require('../helpers/utils');
var furnitureRef = new Firebase(utils.urls.furniture);

/*
* FURNITURE MODULES
*
* This is a furniture class and must be instaniated like
* a normal class with the "new" keyword.
*/

var Furniture = function(snapshot, options) {
  options = options || {};
  var self = this;
  var data = snapshot.val();

  /*
  * Register Furniture Values
  *
  */

  this.officeSpace = $('#office-space');
  this.element = $("<div class='furniture'></div>");
  this.id = snapshot.name();
  this.ref = snapshot.ref();
  this.type = data.type;
  this.locked = data.locked;
  this.rotation = data.rotation;
  this.top = data.top;
  this.left = data.left;
  this.name = data.name;


  /*
  * Create Firebase Reference
  *
  */

  this.ref  = new Firebase(utils.urls.furniture + this.id);




  /*
  * Render Furniture to DOM
  *
  */

  this.render = function(){

    // REMOVE ELEMENT FROM DOM
    this.element.detach();

    // SET CURRENT LOCATION
    this.element.css({
      "top": parseInt(this.top, 10),
      "left": parseInt(this.left, 10)
    });

    if (this.locked){
      this.element.addClass("is-active");
    }
    else {
      this.element.removeClass("is-active");
    }

    // ADD TO DOM
    this.officeSpace.append(this.element);
  };


  /*
  * Initialize furniture module
  *
  */

  this.initElement = function() {

    //SET DRAG OPTIONS
    this.element.draggable({
      containment: self.officeSpace,
      start: function(event, ui){
        self.element.addClass("is-active");
        self.ref.child("locked").set(true);
      },

      drag: function(event, ui){
        self.ref.child("left").set(ui.position.left);
        self.ref.child("top").set(ui.position.top);
      },

      stop: function(event, ui){
        self.element.removeClass("is-active");
        self.ref.child("locked").set(false);
      }
    });

    this.element.addClass(this.type);

    // RENDER
    this.render();
  };



  /*
  * Destroy element
  *
  */

  this.destroy = function() {
    this.element.remove();
  };


  /*
  * Listen for updates
  *
  */

  this.ref.on("value", function(snap){
    var value = snap.val();

    if(value === null) {
      self.ref.off();
      self.element.addClass('animated fadeOut');

      setTimeout(function() {
        self.destroy();
      }, 2000);
    }
    else {
      _.extend(self, value);
      self.render();
    }
  });



  /*
  * Create Furniture Element
  *
  */

  this.initElement();
};

module.exports = Furniture;