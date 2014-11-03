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
      background: data[i].background,
      icon: data[i].icon,
      type: type
    });
  }

  // ADD DROPDOWN TO DOM
  $parent.append(ListTemplate({items: buttonList}));

  //TOGGLE MENU OPEN/CLOSE
  $parent.on('click', function(e) {
    e.preventDefault();
    $parent.find('.dropdown, .dropdown-overlay').toggleClass('is-visible');
  });

  // CLOSE MENU WHEN CLICKING OVERLAY
  $parent.on('click', '.dropdown-overlay', function(e) {
    e.stopPropagation();
    $parent.find('.dropdown, .dropdown-overlay').removeClass('is-visible');
  });
};


// EXPORT MODULE
module.exports = Dropdown;