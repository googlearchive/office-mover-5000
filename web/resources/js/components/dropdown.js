/*
* Dropdown Menu Module
*
*/

var Dropdown = function($parent, data) {
  var ListTemplate = _.template($('#template-dropdown').html());
  var liTemplate = _.template('<li class="<%= name %>"><button><%= description %></button></li>');
  var buttonList = '';

  // LOOP THROUGH DATA & CREATE BUTTONS
  for(var i = 0, l = data.length; i < l; i++) {
    buttonList = buttonList + liTemplate(data[i]);
  }


  $parent.append(ListTemplate({
    items: buttonList
  }));
};

module.exports = Dropdown;