import { WebPlugin } from '@capacitor/core';
import type { CapacitorPosturlPlugin, PostData } from './definitions';
export declare class CapacitorPosturlWeb extends WebPlugin implements CapacitorPosturlPlugin {
    posturl(_data: PostData): Promise<void>;
}
