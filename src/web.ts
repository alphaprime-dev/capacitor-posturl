import { WebPlugin } from '@capacitor/core';

import type { CapacitorPosturlPlugin, PostData } from './definitions';

export class CapacitorPosturlWeb
  extends WebPlugin
  implements CapacitorPosturlPlugin
{
  async posturl(_data: PostData): Promise<void> {
    throw this.unimplemented("Not implemented on web.");
  }
}
