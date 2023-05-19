
import Foundation
import Capacitor
import UIKit
import WebKit

@objc public class CapacitorPosturl: NSObject, WKNavigationDelegate {
    private var urlString: String?

    @objc public func posturl(webView:WKWebView, body: [String: Any], headers: [String: Any], url: String) {
        DispatchQueue.main.async {
            self.urlString = url
            webView.navigationDelegate = self

            if let url = URL(string: url) {
                var request = URLRequest(url: url)
                request.httpMethod = "POST"
                request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
                for (key, value) in headers {
                    request.setValue("\(value)", forHTTPHeaderField: key)
                }
                let postString = self.createPostData(params: body)
                request.httpBody = postString.data(using: .utf8)
                webView.load(request)
            }
        }
    }

    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if let url = navigationAction.request.url {
            if self.urlString != nil && url.absoluteString.contains(self.urlString!) {
                decisionHandler(.allow)
            } else {
                decisionHandler(.cancel)
            }
        } else {
            decisionHandler(.cancel)
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



}
