var utils  = require('../helpers/utils');
var furnitureRef = new Firebase(utils.urls.furniture);


var Furniture = function(snapshot) {
  this.id = snapshot.name();
  this.ref = snapshot.ref();



};

module.exports = Furniture;