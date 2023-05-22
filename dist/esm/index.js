import { registerPlugin } from '@capacitor/core';
const CapacitorPosturl = registerPlugin('CapacitorPosturl', {
    web: () => import('./web').then(m => new m.CapacitorPosturlWeb()),
});
export * from './definitions';
export { CapacitorPosturl };
//# sourceMappingURL=index.js.map