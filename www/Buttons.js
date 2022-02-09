var API = require('./API');

module.exports = {

    /**
     * @param {function} callback
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<boolean>|void}
     */
    subscribe: function (callback, success, failure) {
        if (success == null && failure == null) {
            return API.subscribe(callback);
        } else {
            API.subscribe(callback).then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<boolean>|void}
     */
    unsubscribe: function (success, failure) {
        if (success == null && failure == null) {
            return API.unsubscribe();
        } else {
            API.unsubscribe().then(success).catch(failure);
        }
    }

};