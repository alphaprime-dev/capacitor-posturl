import { registerPlugin } from '@capacitor/core';

import type { CapacitorPosturlPlugin } from './definitions';

const CapacitorPosturl = registerPlugin<CapacitorPosturlPlugin>(
  'CapacitorPosturl',
  {
    web: () => import('./web').then(m => new m.CapacitorPosturlWeb()),
  },
);

export * from './definitions';
export { CapacitorPosturl };
