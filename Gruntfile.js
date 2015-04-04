module.exports = function(grunt) {
  grunt.initConfig({
    // running `grunt concat` will concat our source js
    concat: {
      dist: {
        src: './src/js/**/*.js',
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
          "./resources/public/css/flat-ui.css": "./src/less/darg.less",
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
        files: "./src/less/**/*.less",
        tasks: ["less"]
      }
    }
  });
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
};

