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
          "./resources/email/css/darg.css": "./src/less/email.less",
        }
      }
    },

    // `grunt uncss` to compile a minified, JIT css file for email
    uncss: {
      dist: {
        files: {
          "./resources/email/css/email.css":
            ['./resources/email/templates/raw/digest.html']
        }
      }
    },

    // replace stylesheet with economy version
    processhtml: {
      dist: {
        files: {
          'resources/email/templates/processed/digest.html': ['resources/email/templates/raw/digest.html']
        }
      }
    },

    premailer: {
      main: {
        options: {
          verbose: true
        },
        files: {
          'resources/email/templates/inlined/digest.html': 
            ['resources/email/templates/processed/digest.html']
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
      },
      email: {
        files: ["./resources/email/templates/**/*.html", "./src/less/**/*.less"],
        tasks: ["uncss", "processhtml", "premailer"]
      }
    }
  });
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-uncss');
  grunt.loadNpmTasks('grunt-processhtml');
  grunt.loadNpmTasks('grunt-premailer');
};

