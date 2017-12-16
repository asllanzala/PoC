cordova.define("testPlugin.TestPlugin", function(require, exports, module) {
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

    exports.getData = function(arg0, success, error) {
        exec(success, error, "TestPlugin", "getData", [arg0]);
    };

    exports.playMusic = function(arg0, success, error) {
        exec(success, error, "TestPlugin", "playMusic", [arg0]);
    };

    exports.polling = function(arg0, success, error) {
        exec(success, error, "TestPlugin", "polling", [arg0]);
    };

    exports.takePhoto = function(arg0, success, error) {
        exec(success, error, "TestPlugin", "takePhoto", [arg0]);
    };

});