var exec = require('cordova/exec');

var PLUGIN = 'Buttons';

/**
 * @type {function}
 */
var _callback = undefined;

module.exports = {

    /**
     * @param {function} callback
     * @returns {Promise<void>}
     */
    subscribe: function (callback) {
        return new Promise(function (resolve, reject) {
            _callback = function (result) {
                if (result === "subscribed") {
                    resolve(result);
                } else if (typeof result === "object") {
                    callback(result);
                }
            };
            exec(_callback, reject, PLUGIN, 'subscribe', []);
        });
    },

    /**
     * @returns {Promise<void>}
     */
    unsubscribe: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'unsubscribe', []);
        }).finally(function () {
            _callback = undefined;
        });
    }

};
