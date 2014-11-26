var gulp            = require('gulp');
var sass            = require('gulp-sass');
var autoprefixer    = require('gulp-autoprefixer');
var minifyCSS       = require('gulp-minify-css');
var connect         = require('gulp-connect');
var openPage        = require("gulp-open");
var webpack         = require('gulp-webpack');



/**
*  HTML
*
*  reload html
*/

gulp.task("html", function() {
  var stream = gulp.src('*.html')
    .pipe(connect.reload());

  return stream;
});



/**
*  CSS PREPROCESSING
*
*  Sass, vender prefix, minify, move
*/

gulp.task('css', function() {
  var stream = gulp.src('resources/scss/**/*.scss')
      .pipe(sass())
      .pipe(autoprefixer())
      .pipe(minifyCSS({ noAdvanced: true }))
      .pipe(gulp.dest('resources/css'))
      .pipe(connect.reload());

  return stream;
});


/**
*  WEBPACK
*
*  Process javascript modules into common.js format
*/

gulp.task("webpack", function() {
  var stream = gulp.src('resources/js/app.js')
    .pipe(webpack({
      output: {
        filename: 'production.js'
      }
    }))
    .pipe(gulp.dest('resources/js/'))
    .pipe(connect.reload());

  return stream;
});


/**
*  WATCH
*
*  Rerun process after any of these files are edited
*/

gulp.task('watch', function() {
  gulp.watch('resources/scss/**/*.scss', ['css']);
  gulp.watch('resources/js/**/*.js', ['webpack']);
  gulp.watch('resources/images/**/*.{jpg,png,gif}');
  gulp.watch('index.html', ['html']);
});


/**
*  CONNECT SERVER
*
*  Loads the server locally and reloads when
*  connect.reload() is called.
*/
gulp.task('connect', function() {
  connect.server({
    root: '.',
    port: 8000,
    livereload: true
  });
});


/**
*  BUILD TASKS
*
*  Local and production build tasks
*/
gulp.task('default', ['css', 'webpack', 'watch', 'connect'], function() {
  //Now open in browser
  var stream = gulp.src("index.html")
      .pipe(openPage("", {
        app: "Google Chrome",
        url: "http://localhost:8000"
      }));

  return stream;
});