import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinModulesKt.doInitKoin(config: nil)
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}