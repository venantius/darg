module.exports = function(grunt) {
  grunt.initConfig({

    // `grunt concat` to concat our source files together
    concat: {

      // the angular app
      js: {
        src: './src/js/**/*.js',
        dest: './resources/public/js/darg/app.js'
      },

      // all of our html templates for uncss, etc.
      web: {
        src: ['./resources/build/raw/index.html', 
              './resources/public/templates/**/*.html'],
        dest: './resources/build/concat/templates.html'
      }
    },

    // `grunt less` to compile our css files
    less: {
      email: {
        options: {
          paths: ["./css"],
          yuicompress: true
        },
        files: {
          "./resources/email/css/darg.css": "./src/less/email.less",
        }
      },

      web: {
        options: {
          paths: ["./css"],
          yuicompress: true
        },
        files: {
          "./resources/public/css/flat-ui.css": "./src/less/darg.less",
        }
      }
    },

    // `grunt uncss` to compile a CSS file for only the required CSS
    uncss: {
      email: {
        files: {
          "./resources/email/css/email.css": 
            './resources/email/templates/raw/digest.html',
        }
      },

      web: {
        options: {
          // don't try to get around the CDN
          ignore: [/dropdown/, /navbar/, /collapse/, /collapsing/],
          ignoreSheets: [/cdnjs/, /maxcdn/],
          timeout: 1000,
        },
        files: {
          // www css
          "./resources/public/css/darg.css": 
            "./resources/build/concat/templates.html",
        }
      }
    },

    // replace stylesheet block in <head> with a link to the uncss'd
    // stylesheet
    processhtml: {
      email: {
        files: {
          // daily digest email
          'resources/email/templates/processed/digest.html': 
            ['resources/email/templates/raw/digest.html'],
        }
      },
      web: {
        files: {
          'resources/public/index.html':
            ['resources/build/raw/index.html'],
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
      js: {
        files: "./src/js/**/*.js",
        tasks: ["concat"]
      },
      email: {
        files: ["./resources/email/templates/**/*.html", "./src/less/**/*.less"],
        tasks: ["uncss:email", "processhtml:email", "premailer"]
      },
      web: {
        files: ["./resources/**/*.html", "./src/less/**/*.less"],
        tasks: ["concat:web", "less:web", "uncss:web", "processhtml:web"]
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

