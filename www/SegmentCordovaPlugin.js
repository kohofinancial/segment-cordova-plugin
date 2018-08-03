// var exec = require('cordova/exec');

function SegmentCordovaPlugin() {}

SegmentCordovaPlugin.prototype.startWithConfiguration = function(id, inputs, success, error) {
  cordova.exec(success, error, 'SegmentCordovaPlugin', 'startWithConfiguration', [id, inputs]);
};

SegmentCordovaPlugin.prototype.identify = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'identify', [inputs]);
};

SegmentCordovaPlugin.prototype.track = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'track', [inputs]);
};

SegmentCordovaPlugin.prototype.screen = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'screen', [inputs]);
};

SegmentCordovaPlugin.prototype.group = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'group', [inputs]);
};

SegmentCordovaPlugin.prototype.alias = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'alias', [inputs]);
};

SegmentCordovaPlugin.prototype.getAnonymousId = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'getAnonymousId', [inputs]);
};

SegmentCordovaPlugin.prototype.reset = function(inputs, success, error) {
    cordova.exec(success, error, 'SegmentCordovaPlugin', 'reset', [inputs]);
};

module.exports = new SegmentCordovaPlugin();
