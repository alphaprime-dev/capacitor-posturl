import Foundation
import Capacitor

@objc(CapacitorPosturlPlugin)
public class CapacitorPosturlPlugin: CAPPlugin {
    private let implementation = CapacitorPosturl()

    @objc func posturl(_ call: CAPPluginCall) {
        let body = call.getObject("body") ?? [:]
        let headers = call.getObject("headers") ?? [:]
        let url = call.getString("url") ?? ""
        implementation.posturl(webView: (bridge?.webView)!, body: body, headers: headers, url: url)
        call.resolve()
    }
}
