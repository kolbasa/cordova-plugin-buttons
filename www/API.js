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
                if (result === 1) {
                    resolve('OK');
                } else {
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
