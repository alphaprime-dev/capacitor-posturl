'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const CapacitorPosturl = core.registerPlugin('CapacitorPosturl', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.CapacitorPosturlWeb()),
});

class CapacitorPosturlWeb extends core.WebPlugin {
    async posturl(_data) {
        throw this.unimplemented("Not implemented on web.");
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    CapacitorPosturlWeb: CapacitorPosturlWeb
});

exports.CapacitorPosturl = CapacitorPosturl;
//# sourceMappingURL=plugin.cjs.js.map
