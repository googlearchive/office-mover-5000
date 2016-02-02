# Inetech Office Mover 5000 for Web

## Setup
0. Edit `resources/js/helpers/utils.js` and change `<your-firebase>` to the subdomain
   for your Firebase.
0. Make sure you have [node.js](http://nodejs.org/) and [bower](http://bower.io/) installed.
0. [Create and configure a Google application](https://www.firebase.com/docs/web/guide/login/google.html) to use for authentication.
0. Navigate to the `/web` directory and run these commands:

        $ bower install
        $ npm install ## or sudo npm install
        $ gulp

   Gulp automatically opens Office Mover in a web browser, and reloads when it detects changes to css, js, and html.
