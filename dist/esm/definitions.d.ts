export interface PostData {
    url: string;
    body: Record<string, string>;
    headers?: Record<string, string>;
}
export interface CapacitorPosturlPlugin {
    posturl(data: PostData): Promise<void>;
}
