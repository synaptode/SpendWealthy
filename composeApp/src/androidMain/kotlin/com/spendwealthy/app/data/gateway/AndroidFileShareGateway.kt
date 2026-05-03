package com.spendwealthy.app.data.gateway

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import java.io.File

class AndroidFileShareGateway(
    private val context: Context
) : FileShareGateway {

    override suspend fun shareTextFile(fileName: String, mimeType: String, content: String): GatewayResult<Unit> {
        return runCatching {
            val exportDir = File(context.cacheDir, EXPORT_DIR).apply { mkdirs() }
            val file = File(exportDir, fileName.sanitizeFileName())
            file.writeText(content)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(sendIntent, "Bagikan laporan").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            GatewayResult.Success(Unit)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal membagikan file.", true)
        }
    }

    private fun String.sanitizeFileName(): String {
        return replace(Regex("[^A-Za-z0-9._-]"), "_")
    }

    private companion object {
        const val EXPORT_DIR = "exports"
    }
}
