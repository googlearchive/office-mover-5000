/*
* User Profile Module
*
*/

var userProfile = {
  template: _.template($('#template-profile').html()),
  container: $('#profile'),

  /*
  * Initalize Profile Module
  *
  */

  init: function(data) {
    var hasData = (data && data.google && data.google.cachedUserProfile);

    if(hasData) {
      this.data = data.google.cachedUserProfile;
      this.render();
    }
  },


  /*
  * Render Profile to DOM
  *
  */

  render: function() {
    var $profile = this.template(this.data);

    this.container.html('').addClass('is-visible').append($profile);
  }
};


// EXPORT MODULE
module.exports = userProfile;