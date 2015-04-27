/*
 * Alerts
 */
darg.service('alert', function() {

    this.setAlert = function(alert_list, alert_content) {
        alert_list[0] = {msg: alert_content}
    };

    this.emailConfirmationAlerts = [];
    this.emailConfirmationMessage = "We've e-mailed you with a link to confirm your e-mail address. Didn't get it?"

    this.failedLoginAlerts = [];
    this.failedLoginMessage = "Incorrect e-mail or password."

});
