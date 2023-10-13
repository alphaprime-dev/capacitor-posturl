# capacitor-posturl

capacitor-posturl

## Install

```bash
npm install capacitor-posturl
npx cap sync
```

## API

<docgen-index>

* [`posturl(...)`](#posturl)
* [`addListener('urlChangeEvent', ...)`](#addlistenerurlchangeevent)
* [`addListener('closeEvent', ...)`](#addlistenercloseevent)
* [`addListener('confirmBtnClicked', ...)`](#addlistenerconfirmbtnclicked)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### posturl(...)

```typescript
posturl(data: PostData) => Promise<void>
```

| Param      | Type                                          |
| ---------- | --------------------------------------------- |
| **`data`** | <code><a href="#postdata">PostData</a></code> |

--------------------


### addListener('urlChangeEvent', ...)

```typescript
addListener(eventName: "urlChangeEvent", listenerFunc: UrlChangeListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for url change

| Param              | Type                                                            |
| ------------------ | --------------------------------------------------------------- |
| **`eventName`**    | <code>'urlChangeEvent'</code>                                   |
| **`listenerFunc`** | <code><a href="#urlchangelistener">UrlChangeListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 0.0.1

--------------------


### addListener('closeEvent', ...)

```typescript
addListener(eventName: "closeEvent", listenerFunc: UrlChangeListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for close click

| Param              | Type                                                            |
| ------------------ | --------------------------------------------------------------- |
| **`eventName`**    | <code>'closeEvent'</code>                                       |
| **`listenerFunc`** | <code><a href="#urlchangelistener">UrlChangeListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 0.4.0

--------------------


### addListener('confirmBtnClicked', ...)

```typescript
addListener(eventName: "confirmBtnClicked", listenerFunc: ConfirmBtnListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Will be triggered when user clicks on confirm button when disclaimer is required, works only on iOS

| Param              | Type                                                              |
| ------------------ | ----------------------------------------------------------------- |
| **`eventName`**    | <code>'confirmBtnClicked'</code>                                  |
| **`listenerFunc`** | <code><a href="#confirmbtnlistener">ConfirmBtnListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 0.0.1

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all listeners for this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### PostData

| Prop                 | Type                                                              |
| -------------------- | ----------------------------------------------------------------- |
| **`url`**            | <code>string</code>                                               |
| **`body`**           | <code><a href="#record">Record</a>&lt;string, string&gt;</code>   |
| **`headers`**        | <code><a href="#record">Record</a>&lt;string, string&gt;</code>   |
| **`webviewOptions`** | <code><a href="#openwebviewoptions">OpenWebViewOptions</a></code> |


#### OpenWebViewOptions

| Prop                         | Type                                                            | Description                                                                                                                                                                       | Default                                                    | Since  |
| ---------------------------- | --------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------- | ------ |
| **`shareDisclaimer`**        | <code><a href="#disclaimeroptions">DisclaimerOptions</a></code> | share options                                                                                                                                                                     |                                                            | 0.1.0  |
| **`toolbarType`**            | <code><a href="#toolbartype">ToolBarType</a></code>             | Toolbar type                                                                                                                                                                      | <code>ToolBarType.DEFAULT</code>                           | 0.1.0  |
| **`shareSubject`**           | <code>string</code>                                             | Share subject                                                                                                                                                                     |                                                            | 0.1.0  |
| **`title`**                  | <code>string</code>                                             | Title of the browser                                                                                                                                                              | <code>'New Window'</code>                                  | 0.1.0  |
| **`backgroundColor`**        | <code><a href="#backgroundcolor">BackgroundColor</a></code>     | Background color of the browser, only on IOS                                                                                                                                      | <code>BackgroundColor.BLACK</code>                         | 0.1.0  |
| **`isPresentAfterPageLoad`** | <code>boolean</code>                                            | Open url in a new window fullscreen isPresentAfterPageLoad: if true, the browser will be presented after the page is loaded, if false, the browser will be presented immediately. | <code>false</code>                                         | 0.1.0  |
| **`showReloadButton`**       | <code>boolean</code>                                            | Shows a reload button that reloads the web page                                                                                                                                   | <code>false</code>                                         | 1.0.15 |
| **`closeModal`**             | <code>boolean</code>                                            | CloseModal: if true a confirm will be displayed when user clicks on close button, if false the browser will be closed immediately.                                                | <code>false</code>                                         | 1.1.0  |
| **`closeModalTitle`**        | <code>string</code>                                             | CloseModalTitle: title of the confirm when user clicks on close button, only on IOS                                                                                               | <code>'Close'</code>                                       | 1.1.0  |
| **`closeModalDescription`**  | <code>string</code>                                             | CloseModalDescription: description of the confirm when user clicks on close button, only on IOS                                                                                   | <code>'Are you sure you want to close this window?'</code> | 1.1.0  |
| **`closeModalOk`**           | <code>string</code>                                             | CloseModalOk: text of the confirm button when user clicks on close button, only on IOS                                                                                            | <code>'Close'</code>                                       | 1.1.0  |
| **`closeModalCancel`**       | <code>string</code>                                             | CloseModalCancel: text of the cancel button when user clicks on close button, only on IOS                                                                                         | <code>'Cancel'</code>                                      | 1.1.0  |
| **`visibleTitle`**           | <code>boolean</code>                                            | visibleTitle: if true the website title would be shown else shown empty                                                                                                           | <code>true</code>                                          | 1.2.5  |
| **`toolbarColor`**           | <code>string</code>                                             | toolbarColor: color of the toolbar in hex format                                                                                                                                  | <code>'#ffffff''</code>                                    | 1.2.5  |
| **`showArrow`**              | <code>boolean</code>                                            | showArrow: if true an arrow would be shown instead of cross for closing the window                                                                                                | <code>false</code>                                         | 1.2.5  |


#### DisclaimerOptions

| Prop             | Type                |
| ---------------- | ------------------- |
| **`title`**      | <code>string</code> |
| **`message`**    | <code>string</code> |
| **`confirmBtn`** | <code>string</code> |
| **`cancelBtn`**  | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### UrlEvent

| Prop      | Type                | Description               | Since |
| --------- | ------------------- | ------------------------- | ----- |
| **`url`** | <code>string</code> | Emit when the url changes | 0.0.1 |


#### BtnEvent

| Prop      | Type                | Description                    | Since |
| --------- | ------------------- | ------------------------------ | ----- |
| **`url`** | <code>string</code> | Emit when a button is clicked. | 0.0.1 |


### Type Aliases


#### Record

Construct a type with a set of properties K of type T

<code>{ [P in K]: T; }</code>


#### UrlChangeListener

<code>(state: <a href="#urlevent">UrlEvent</a>): void</code>


#### ConfirmBtnListener

<code>(state: <a href="#btnevent">BtnEvent</a>): void</code>


### Enums


#### ToolBarType

| Members          | Value                     |
| ---------------- | ------------------------- |
| **`ACTIVITY`**   | <code>"activity"</code>   |
| **`NAVIGATION`** | <code>"navigation"</code> |
| **`BLANK`**      | <code>"blank"</code>      |
| **`DEFAULT`**    | <code>""</code>           |


#### BackgroundColor

| Members     | Value                |
| ----------- | -------------------- |
| **`WHITE`** | <code>"white"</code> |
| **`BLACK`** | <code>"black"</code> |

</docgen-api>
