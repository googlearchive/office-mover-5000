var utils  = require('../helpers/utils');
var furnitureRef = new Firebase(utils.urls.furniture);

/*
* FURNITURE MODULES
*
* This is a furniture class and must be instaniated like
* a normal class with the "new" keyword.
*/

var Furniture = function(snapshot, app) {
  var self = this;
  var data = snapshot.val();
  var template = _.template($('#template-furniture-item').html());
  this.app = app;
  this.id = snapshot.name();
  this.ref = snapshot.ref();

  /*
  * Update Furniture Values
  *
  */

  this.updateValues = function (data) {
    this.type = data.type;
    this.locked = data.locked;
    this.rotation = data.rotation;
    this.top = data.top;
    this.left = data.left;
    this.name = data.name;
    this.zIndex = data['z-index'];
  };

  this.updateValues(data);


  /*
  * Register DOM ELEMENTS
  *
  */
  var furniture = template({
    type: this.type,
    name: this.name
  });

  this.officeSpace = $('#office-space');
  this.element = $(furniture.trim());
  this.tooltip = this.element.find(".tooltip");
  this.nameEl = this.element.find(".furniture-name");


  /*
  * Render Furniture to DOM
  *
  */

  this.render = function(){
    // SET DESK NAME
    if(this.type === 'desk') {
      this.nameEl.text(this.name);
    }

    // ROTATE ELEMENT
    this.element.removeClass('rotate-0 rotate-90 rotate-180 rotate-270')
    .addClass('rotate-' + this.rotation);


    // SET CURRENT LOCATION ON CANVAS
    this.element.css({
      "top": parseInt(this.top, 10),
      "left": parseInt(this.left, 10),
      "zIndex": parseInt(this.zIndex, 10),
    });


    // SET ACTIVE STATE
    if (this.locked){
      this.element.addClass("is-active is-top");
    }
    else {
      this.element.removeClass("is-active is-top");
    }

    // ADD TO DOM
    this.officeSpace.append(this.element);
  };


  /*
  * Edit name on desk
  */

  this.editName = function(){
    var name = window.prompt("Who sits here?", this.name);
    this.ref.child("name").set(name);
  };


  /*
  * Rotate furniture
  */

  this.rotate = function(){
    var rotate = (this.rotation <= 0) ? 270 : this.rotation - 90;

    //FIND CURRENT LOCATION
    var left = parseInt(this.element.css('left'), 10);
    var top = parseInt(this.element.css('top'), 10);
    var height = parseInt(this.element.height(), 10);
    var width = parseInt(this.element.width(), 10);

    //TOP: ADD HALF OF HEIGHT SUBTRACT HALF WIDTH
    var newTop = top + (height / 2) - (width / 2);

    //LEFT: ADD HALF THE WIDTH SUBTRACT HALF THE HEIGHT
    var newLeft = left + (width / 2) - (height / 2);


    this.ref.update({
      rotation: rotate,
      left: newLeft,
      top: newTop
    });
  };


  /*
  * Delete furniture and remove from screen
  */

  this.delete = function(){
    this.ref.remove();
  };


  /*
  * Destroy element
  */

  this.destroy = function() {
    this.ref.off();
    this.element.addClass('animated fadeOut');

    setTimeout(function() {
      self.element.remove();
    }, 2000);
  };


  /*
  * Activated Tooltip Menu
  */

  this.activateTooltip = function(){
    // SHOW TOOLTIP WHEN CLICKING ON FURNITURE
    this.element.on("click", function(e){
      self.tooltip.toggleClass("is-hidden");
      self.element.toggleClass("is-active is-top");
    });

    // ADD CLICK EVENT TO BUTTONS
    this.tooltip.on("click", function(e){
      e.stopPropagation();
      var $el = $(e.target);
      var action = $el.data("tooltip-action");

      // HIDE TOOLTIP AND DESELECT
      self.tooltip.addClass("is-hidden");
      self.element.removeClass("is-active is-top");

      switch (action) {
        case "edit": self.editName(); break;
        case "rotate": self.rotate(); break;
        case "delete": self.delete(); break;
      }
    });
  };


  /*
  * Listen for updates
  */

  this.ref.on("value", function(snap){
    var value = snap.val();

    if(value === null) {
      //FURNITURE HAS BEEN DELETED
      self.destroy();
    }
    else {
      // FURNITURE UPDATED WITH NEW VALUES
      self.updateValues(value);
      self.app.setMaxZIndex(snap);
      self.render();
    }
  });


  /*
  * Initialize furniture module
  *
  */

  this.initElement = function() {
    //SET DRAG OPTIONS
    this.element.draggable({
      containment: self.officeSpace,
      start: function(event, ui){
        self.tooltip.addClass("is-hidden");
        self.element.addClass("is-active is-top");
        self.ref.child("locked").set(true);
      },

      drag: function(event, ui){
        self.ref.child("left").set(ui.position.left);
        self.ref.child("top").set(ui.position.top);
      },

      stop: function(event, ui){
        var zIndex = self.app.maxZIndex + 1;
        self.element.removeClass("is-active is-top");
        self.ref.child("locked").set(false);
        self.ref.child("z-index").set(zIndex);
        self.app.maxZIndex = zIndex;
      }
    });

    // ACTIVATE TOOLTIP MENU
    this.activateTooltip();

    // RENDER
    this.render();
  };

  /*
  * Create Furniture Element
  */

  this.initElement();
};


// EXPORT MODULE
module.exports = Furniture;