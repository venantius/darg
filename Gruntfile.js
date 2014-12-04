module.exports = function(grunt) {
  grunt.initConfig({
    // running `grunt concat` will concat our source js
    concat: {
      dist: {
        src: ['./src/js/**/*.js'],
        dest: './resources/public/js/darg/app.js'
      }
    },

    // running `grunt less` will compile our less files
    less: {
      development: {
        options: {
          paths: ["./css"],
          yuicompress: true
        },
        files: {
          "./resources/dependencies/Flat-UI-master/css/flat-ui.css": "./resources/dependencies/Flat-UI-master/less/flat-ui.less"
        }
      }
    },

    // running `grunt watch` will watch for changes to both js and less
    watch: {
      concat: {
        files: "./src/js/**/*.js",
        tasks: ["concat"]
      },
      less: {
        files: "./resources/dependencies/Flat-UI-master/less/**/*.less",
        tasks: ["less"]
      }
    }
  });
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
};

