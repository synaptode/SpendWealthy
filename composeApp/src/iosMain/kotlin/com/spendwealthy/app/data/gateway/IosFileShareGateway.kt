package com.spendwealthy.app.data.gateway

import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import kotlinx.cinterop.BetaInteropApi

/**
 * Native iOS File Share Gateway via UIActivityViewController.
 * Safely writes exported reports (CSV/PDF) to NSTemporaryDirectory and triggers the system share sheet.
 */
class IosFileShareGateway : FileShareGateway {

    @OptIn(BetaInteropApi::class)
    override suspend fun shareTextFile(fileName: String, mimeType: String, content: String): GatewayResult<Unit> {

        return runCatching {
            val tempDir = NSTemporaryDirectory()
            val sanitized = fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val filePath = "$tempDir$sanitized"
            val fileUrl = NSURL.fileURLWithPath(filePath)

            val nsString = NSString.create(string = content)
            val data = nsString.dataUsingEncoding(NSUTF8StringEncoding)
            data?.writeToURL(fileUrl, true)

            val rootViewController = findTopViewController()
                ?: return GatewayResult.Failure("Tidak dapat menemukan view controller aktif untuk menampilkan share sheet.", recoverable = true)

            val activityController = UIActivityViewController(
                activityItems = listOf(fileUrl),
                applicationActivities = null
            )

            rootViewController.presentViewController(activityController, animated = true, completion = null)
            GatewayResult.Success(Unit)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal membagikan berkas di iOS.", recoverable = true)
        }
    }

    private fun findTopViewController(): UIViewController? {
        val app = UIApplication.sharedApplication
        val windows = app.windows
        val keyWindow = app.keyWindow ?: windows.filterIsInstance<UIWindow>().firstOrNull { it.isKeyWindow() }
        var current = keyWindow?.rootViewController
        while (current?.presentedViewController != null) {
            current = current.presentedViewController
        }
        return current
    }
}
