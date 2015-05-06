module.exports = function(grunt) {
  grunt.initConfig({

    // `grunt concat` to concat our source
    concat: {

      // the angular app
      basic: {
        src: './src/js/**/*.js',
        dest: './resources/public/js/darg/app.js'
      },

      // all of our html templates for uncss, etc.
      extras: {
        src: ['./resources/build/raw/index.html', 
              './resources/public/templates/**/*.html'],
        dest: './resources/build/concat/templates.html'
      }
    },

    // `grunt less` to compile our css files
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

    // `grunt uncss` to compile a CSS file for only the required CSS
    uncss: {
      dist: {
        options: {
          // don't try to get around the CDN
          ignoreSheets: [/cdnjs/, /maxcdn/],
          timeout: 1000,
        },
        files: {
          // email css
          "./resources/email/css/email.css": 
            './resources/email/templates/raw/digest.html',

          // www css
          "./resources/public/css/darg.css": 
            "./resources/build/concat/templates.html",
        }
      },

      web: {
        options: {
          // don't try to get around the CDN
          ignoreSheets: [/cdnjs/, /maxcdn/],
          timeout: 1000,
        },
        files: {
          // email css
          "./resources/email/css/email.css": 
            './resources/email/templates/raw/digest.html',

          // www css
          "./resources/public/css/darg.css": 
            "./resources/build/concat/templates.html",
        }
      }

    },

    // replace stylesheet with economy version
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
      },
      web: {
        files: ["./resources/**/*.html"],
        tasks: ["concat", "uncss"]
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

