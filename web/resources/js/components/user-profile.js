/*
* User Profile Module
*
*/


var userProfile = {
  template: _.template($('#template-profile').html()),
  container: $('#profile'),

  init: function(data) {
    var hasData = (data && data.google && data.google.cachedUserProfile);

    if(data && data.google && data.google.cachedUserProfile) {
      this.data = data.google.cachedUserProfile;
      this.render();
    }
  },

  render: function() {
    var $profile = this.template(this.data);
    console.log(this.data);
    this.container.append($profile);
    this.container.addClass('is-visible');
  }

};

module.exports = userProfile;