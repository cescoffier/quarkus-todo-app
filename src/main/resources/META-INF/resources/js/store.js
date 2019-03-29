/*jshint unused:false */

(function (exports) {

    'use strict';

    exports.todoStorage = {
        fetch: async function () {
            const response = await axios.get('/');
            console.log(response.data);
            return response.data;
        },
        add : async function(item) {
          console.log("Adding todo item " + item.title);
          return (await axios.post("/", item)).data;
        },
        save: async function (item) {
            console.log("save called with", item);
            await axios.patch('/' + item.id, item);
        },
        delete: async function(item) {
            await axios.delete('/' + item.id);
        }
    };

})(window);