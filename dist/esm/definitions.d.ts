import type { PluginListenerHandle } from "@capacitor/core";
export interface UrlEvent {
    /**
     * Emit when the url changes
     *
     * @since 0.0.1
     */
    url: string;
}
export interface BtnEvent {
    /**
     * Emit when a button is clicked.
     *
     * @since 0.0.1
     */
    url: string;
}
export declare type UrlChangeListener = (state: UrlEvent) => void;
export declare type ConfirmBtnListener = (state: BtnEvent) => void;
export declare enum BackgroundColor {
    WHITE = "white",
    BLACK = "black"
}
export declare enum ToolBarType {
    ACTIVITY = "activity",
    NAVIGATION = "navigation",
    BLANK = "blank",
    DEFAULT = ""
}
export interface DisclaimerOptions {
    title: string;
    message: string;
    confirmBtn: string;
    cancelBtn: string;
}
export interface OpenWebViewOptions {
    /**
     * share options
     * @since 0.1.0
     */
    shareDisclaimer?: DisclaimerOptions;
    /**
     * Toolbar type
     * @since 0.1.0
     * @default ToolBarType.DEFAULT
     */
    toolbarType?: ToolBarType;
    /**
     * Share subject
     * @since 0.1.0
     */
    shareSubject?: string;
    /**
     * Title of the browser
     * @since 0.1.0
     * @default 'New Window'
     */
    title: string;
    /**
     * Background color of the browser, only on IOS
     * @since 0.1.0
     * @default BackgroundColor.BLACK
     */
    backgroundColor?: BackgroundColor;
    /**
     * Open url in a new window fullscreen
     *
     * isPresentAfterPageLoad: if true, the browser will be presented after the page is loaded, if false, the browser will be presented immediately.
     * @since 0.1.0
     * @default false
     */
    isPresentAfterPageLoad?: boolean;
    /**
     * Shows a reload button that reloads the web page
     * @since 1.0.15
     * @default false
     */
    showReloadButton?: boolean;
    /**
     * CloseModal: if true a confirm will be displayed when user clicks on close button, if false the browser will be closed immediately.
     *
     * @since 1.1.0
     * @default false
     */
    closeModal?: boolean;
    /**
     * CloseModalTitle: title of the confirm when user clicks on close button, only on IOS
     *
     * @since 1.1.0
     * @default 'Close'
     */
    closeModalTitle?: string;
    /**
     * CloseModalDescription: description of the confirm when user clicks on close button, only on IOS
     *
     * @since 1.1.0
     * @default 'Are you sure you want to close this window?'
     */
    closeModalDescription?: string;
    /**
     * CloseModalOk: text of the confirm button when user clicks on close button, only on IOS
     *
     * @since 1.1.0
     * @default 'Close'
     */
    closeModalOk?: string;
    /**
     * CloseModalCancel: text of the cancel button when user clicks on close button, only on IOS
     *
     * @since 1.1.0
     * @default 'Cancel'
     */
    closeModalCancel?: string;
    /**
     * visibleTitle: if true the website title would be shown else shown empty
     *
     * @since 1.2.5
     * @default true
     */
    visibleTitle?: boolean;
    /**
     * toolbarColor: color of the toolbar in hex format
     *
     * @since 1.2.5
     * @default '#ffffff''
     */
    toolbarColor?: string;
    /**
     * showArrow: if true an arrow would be shown instead of cross for closing the window
     *
     * @since 1.2.5
     * @default false
     */
    showArrow?: boolean;
}
export interface PostData {
    url: string;
    body: Record<string, string>;
    headers?: Record<string, string>;
    webviewOptions?: OpenWebViewOptions;
}
export interface CapacitorPosturlPlugin {
    posturl(data: PostData): Promise<void>;
    /**
     * Listen for url change
     *
     * @since 0.0.1
     */
    addListener(eventName: "urlChangeEvent", listenerFunc: UrlChangeListener): Promise<PluginListenerHandle> & PluginListenerHandle;
    /**
     * Listen for close click
     *
     * @since 0.4.0
     */
    addListener(eventName: "closeEvent", listenerFunc: UrlChangeListener): Promise<PluginListenerHandle> & PluginListenerHandle;
    /**
     * Will be triggered when user clicks on confirm button when disclaimer is required, works only on iOS
     *
     * @since 0.0.1
     */
    addListener(eventName: "confirmBtnClicked", listenerFunc: ConfirmBtnListener): Promise<PluginListenerHandle> & PluginListenerHandle;
    /**
     * Remove all listeners for this plugin.
     *
     * @since 1.0.0
     */
    removeAllListeners(): Promise<void>;
}
