var capacitorCapacitorPosturl = (function (exports, core) {
    'use strict';

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

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
