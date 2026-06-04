package com.openclaude.android.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ShareFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun shareText(filePath: String, content: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "Shared from OpenClaude: ${filePath.substringAfterLast("/")}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun shareFile(filePath: String): Intent? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mimeType = when {
                filePath.endsWith(".kt") || filePath.endsWith(".java") -> "text/plain"
                filePath.endsWith(".xml") -> "text/xml"
                filePath.endsWith(".json") -> "application/json"
                filePath.endsWith(".md") -> "text/markdown"
                else -> "text/plain"
            }

            Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            null
        }
    }
}
