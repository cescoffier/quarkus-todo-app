/*jshint unused:false */

(function (exports) {

    'use strict';

    exports.kubernetes = {
        podName: async function() {
            const response = await axios.get('pod/name');
            console.log("Pod name: " + response.data);
            return response.data;
        },
    };

})(window);
