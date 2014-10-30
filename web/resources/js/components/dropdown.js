/*
* Dropdown Menu Module
*
*/

var Dropdown = function($parent, data, type) {
  var ListTemplate = _.template($('#template-dropdown').html());
  var liTemplate = _.template($('#template-dropdown-item').html());
  var buttonList = '';



  // LOOP THROUGH DATA & CREATE BUTTONS
  for(var i = 0, l = data.length; i < l; i++) {
    buttonList = buttonList + liTemplate({
      name: data[i].name,
      description: data[i].description,
      type: type
    });
  }


  $parent.append(ListTemplate({
    items: buttonList
  }));


  $parent.on('click', function(e) {
    e.preventDefault();
    $parent.find('.dropdown').toggleClass('is-visible');
  });
};

module.exports = Dropdown;