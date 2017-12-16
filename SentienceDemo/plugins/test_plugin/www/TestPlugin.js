var exec = require('cordova/exec');

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, "TestPlugin", "coolMethod", [arg0]);
};

exports.subscribleEvent = function(arg0, success, error) {
    exec(success, error, "TestPlugin", "subscribleEvent", [arg0]);
};


exports.backEvent = function(arg0, success, error) {
    exec(success, error, "TestPlugin", "backEvent", [arg0]);
};