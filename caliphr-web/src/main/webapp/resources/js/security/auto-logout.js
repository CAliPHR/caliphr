// Set timeout variables.
var timoutWarning = 840000; // Display warning in 14 Mins.
// var timoutWarning = 120000; // Display warning in 2 Mins.
var timoutNow = 60000; // Warning has been shown, give the user 1 minute to interact
var logoutUrl = '/web/auth/logout'; // URL to logout page.

var warningTimer;
var timeoutTimer;

// Start warning timer.
function StartWarningTimer() {
    warningTimer = setTimeout("IdleWarning()", timoutWarning);
    console.log('WARNING TIMER IS :'+warningTimer+' Total time to time out :'+timoutWarning);
}

// Reset timers.
function ResetTimeOutTimer() {
    console.log('THE TIMEOUT IS AS FOLLOWS :'+timeoutTimer);
    clearTimeout(timeoutTimer);
    clearTimeout(warningTimer);
    StartWarningTimer();
    $('#logout-modal').modal('hide');

    // Keep session alive
    $.post(window.location.href);
}

// Show idle timeout warning dialog.
function IdleWarning() {
    clearTimeout(warningTimer);
    timeoutTimer = setTimeout("IdleTimeout()", timoutNow);
    $('#logout-modal').modal('show');
    // Add code in the #timeout element to call ResetTimeOutTimer() if
    // the "Stay Logged In" button is clicked
}

// Logout the user.
function IdleTimeout() {
    window.location = logoutUrl;
}

// Trigger the auto-logout
StartWarningTimer();
