import Foundation
import Capacitor

@objc(CapacitorPosturlPlugin)
public class CapacitorPosturlPlugin: CAPPlugin, WKNavigationDelegate {
    private var urlString: String?
    private var callbackUrl: URL?
    
    var navigationWebViewController: UINavigationController?
    private var privacyScreen: UIImageView?
    private var isSetupDone = false
    var currentPluginCall: CAPPluginCall?
    var isPresentAfterPageLoad = false
    var webViewController: WKWebViewController?
    
    public func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        if error is URLError {
               self.currentPluginCall?.reject(error.localizedDescription)
           }
       }
    
    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if let url = navigationAction.request.url {
            if url.host == URL(string: self.urlString ?? "")?.host || url == self.callbackUrl {
                decisionHandler(.allow)
            } else {
                decisionHandler(.cancel)
            }
        } else {
            decisionHandler(.cancel)
        }
    }
    
    
    @objc func posturl(_ call: CAPPluginCall) {
        self.currentPluginCall = call
        let body = call.getObject("body") ?? [:]
        let headers = call.getObject("headers") ?? [:]
        let url = call.getString("url") ?? ""
        let webviewOptions = call.getObject("webviewOptions") ?? nil
                
        if (webviewOptions != nil) {
            self.openWebView(url, body, headers,options: webviewOptions!)
        }
        else{
            self.changeWebView(url, body, headers)
        }
        call.resolve()
    }
    
    private func changeWebView(_ url: String, _ body: JSObject, _ headers: JSObject) {
        DispatchQueue.main.async {
            self.urlString = url
            if let callbackUrlString = body["callbackUrl"] as? String {
                self.callbackUrl = URL(string: callbackUrlString)
            }
            self.webView?.navigationDelegate = self
            
            if let url = URL(string: url) {
                var request = URLRequest(url: url)
                request.timeoutInterval = 3.0
                request.httpMethod = "POST"
                request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
                for (key, value) in headers {
                    request.setValue("\(value)", forHTTPHeaderField: key)
                }
                let postString = self.createPostData(params: body)
                request.httpBody = postString.data(using: .utf8)
                self.bridge?.webView!.load(request)
            }
        }
    }
    
    func createPostData(params: [String: Any]) -> String {
        var data = [String]()
        var charset = CharacterSet.urlQueryAllowed
        charset.remove("+")
        for(key, value) in params {
            let keyString = key.addingPercentEncoding(withAllowedCharacters: charset)!
            let valueString = "\(value)".addingPercentEncoding(withAllowedCharacters: charset)!
            data.append(keyString + "=" + valueString)
        }
        return data.map { String($0) }.joined(separator: "&")
    }
    
    func openWebView(_ url: String, _ body: JSObject, _ headersJSObject: JSObject,options: [String:Any]) {
        if !self.isSetupDone {
            self.setup()
        }
        var headers = [String: String]()
           
        for (key, value) in headersJSObject {
            headers[key] = "\(value)"
        }
        let postString = self.createPostData(params: body)
        let closeModal = options["closeModal"] as? Bool ?? false
        let closeModalTitle = options["closeModalTitle"] as? String ?? "Close"
        let closeModalDescription = options["closeModalDescription"] as? String ?? "Are you sure you want to close this window?"
        let closeModalOk = options["closeModalOk"] as? String ?? "OK"
        let closeModalCancel = options["closeModalCancel"] as? String ?? "Cancel"
        var disclaimerContent = options["shareDisclaimer"] as? [String: String]
        let toolbarType = options["toolbarType"] as? String ?? ""
        let backgroundColor = options["backgroundColor"] as? String == "white" ? UIColor.white : UIColor.black
        if toolbarType != "activity" {
            disclaimerContent = nil
        }
        self.isPresentAfterPageLoad = options["isPresentAfterPageLoad"] as? Bool ?? false
        let showReloadButton = options["showReloadButton"] as? Bool ?? false
        let useFullScreen = options["useFullScreen"] as? Bool ?? false

        DispatchQueue.main.async {
            if self.isPresentAfterPageLoad {
                self.webViewController = WKWebViewController.init(url:URL(string: url)!, body: postString,headers: headers)
            } else {
                self.webViewController = WKWebViewController.init()
                self.webViewController?.setBody(body: postString)
                self.webViewController?.setHeaders(headers: headers)
            }

            self.webViewController?.source = .remote(URL(string: url)!)
            self.webViewController?.leftNavigaionBarItemTypes = self.getToolbarItems(toolbarType: toolbarType) + [.reload]
            self.webViewController?.leftNavigaionBarItemTypes = self.getToolbarItems(toolbarType: toolbarType)
            self.webViewController?.toolbarItemTypes = []
            self.webViewController?.doneBarButtonItemPosition = .right
            if (options["showArrow"] as? Bool ??  false) {
                self.webViewController?.stopBarButtonItemImage = UIImage(named: "Forward@3x", in: Bundle(for: CapacitorPosturlPlugin.self), compatibleWith: nil)
            }
    

            self.webViewController?.capBrowserPlugin = self
            self.webViewController?.title = options["title"] as? String ?? "New Window"
            self.webViewController?.shareSubject = options["shareSubject"] as? String
            self.webViewController?.shareDisclaimer = disclaimerContent
            self.webViewController?.websiteTitleInNavigationBar = options["visibleTitle"] as? Bool ?? true
            if closeModal {
                self.webViewController?.closeModal = true
                self.webViewController?.closeModalTitle = closeModalTitle
                self.webViewController?.closeModalDescription = closeModalDescription
                self.webViewController?.closeModalOk = closeModalOk
                self.webViewController?.closeModalCancel = closeModalCancel
            }
            self.navigationWebViewController = UINavigationController.init(rootViewController: self.webViewController!)
            self.navigationWebViewController?.navigationBar.isTranslucent = false
            self.navigationWebViewController?.toolbar.isTranslucent = false
            self.navigationWebViewController?.navigationBar.backgroundColor = backgroundColor
            self.navigationWebViewController?.toolbar.backgroundColor = backgroundColor
            if useFullScreen {
                self.navigationWebViewController?.modalPresentationStyle = .fullScreen
            } else {
                if #available(iOS 13.0, *) {
                    self.navigationWebViewController?.modalPresentationStyle = .pageSheet
                } else {
                    self.navigationWebViewController?.modalPresentationStyle = .formSheet
                }
            }
            if toolbarType == "blank" {
                self.navigationWebViewController?.navigationBar.isHidden = true
            }
            if showReloadButton {
                let toolbarItems = self.getToolbarItems(toolbarType: toolbarType)
                self.webViewController?.leftNavigaionBarItemTypes = toolbarItems + [.reload]
            }
            if !self.isPresentAfterPageLoad {
                self.presentView()
            }
        }
    }
    
    private func setup() {
        self.isSetupDone = true

        #if swift(>=4.2)
        NotificationCenter.default.addObserver(self, selector: #selector(appDidBecomeActive(_:)), name: UIApplication.didBecomeActiveNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(appWillResignActive(_:)), name: UIApplication.willResignActiveNotification, object: nil)
        #else
        NotificationCenter.default.addObserver(self, selector: #selector(appDidBecomeActive(_:)), name: .UIApplicationDidBecomeActive, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(appWillResignActive(_:)), name: .UIApplicationWillResignActive, object: nil)
        #endif
    }
    
    func presentView() {
            self.bridge?.viewController?.present(self.navigationWebViewController!, animated: true, completion: {
                self.currentPluginCall?.resolve()
            })
        }
    
    func getToolbarItems(toolbarType: String) -> [BarButtonItemType] {
            var result: [BarButtonItemType] = []
            if toolbarType == "activity" {
                result.append(.activity)
            } else if toolbarType == "navigation" {
                result.append(.back)
                result.append(.forward)
            }
            return result
        }

    
    
    private func showPrivacyScreen() {
            if privacyScreen == nil {
                self.privacyScreen = UIImageView()
                if let launchImage = UIImage(named: "LaunchImage") {
                    privacyScreen!.image = launchImage
                    privacyScreen!.frame = UIScreen.main.bounds
                    privacyScreen!.contentMode = .scaleAspectFill
                    privacyScreen!.isUserInteractionEnabled = false
                } else if let launchImage = UIImage(named: "Splash") {
                    privacyScreen!.image = launchImage
                    privacyScreen!.frame = UIScreen.main.bounds
                    privacyScreen!.contentMode = .scaleAspectFill
                    privacyScreen!.isUserInteractionEnabled = false
                }
            }
            self.navigationWebViewController?.view.addSubview(self.privacyScreen!)
        }

        private func hidePrivacyScreen() {
            self.privacyScreen?.removeFromSuperview()
        }

        @objc func appDidBecomeActive(_ notification: NSNotification) {
            self.hidePrivacyScreen()
        }

        @objc func appWillResignActive(_ notification: NSNotification) {
            self.showPrivacyScreen()
        }
}
