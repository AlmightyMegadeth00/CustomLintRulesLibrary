package com.helpfullintrules.lint_rules.detectors

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.OtherFileScanner
import com.helpfullintrules.lint_rules.Constants.INVALID_JSON_ASSET_ID
import com.helpfullintrules.lint_rules.Constants.JSON_FILE_EXTENSION
import com.helpfullintrules.lint_rules.exceptions.LintingFailureException
import com.helpfullintrules.utils.FileUtils
import com.helpfullintrules.utils.FileUtils.readTextFromFile
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.jvm.Throws


class JsonSyntaxDetector: Detector(), OtherFileScanner {

    companion object {
        private val TAG = JsonSyntaxDetector::class.java.simpleName

        private val ISSUE = Issue.create(
            id = INVALID_JSON_ASSET_ID,
            briefDescription = "Checks for invalid JSON files in the assets directory",
            explanation = "JSON assets should be valid and follow a specific format. " +
                    "This check ensures the JSON is well-formed to ensure runtime stability.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.FATAL,
            implementation = Implementation(
                JsonSyntaxDetector::class.java,
                Scope.OTHER_SCOPE
            ),
        )
    }

    override fun getApplicableFiles() = Scope.OTHER_SCOPE

    override fun run(context: Context) {
        context.project.assetFolders.forEach { assets ->
            if (assets.isDirectory) {
                assets.listFiles()?.forEach { file ->
                    if (file.name.endsWith(JSON_FILE_EXTENSION)) {
                        checkJsonFile(context, file)
                    }
                }
            }
        }
    }


    private fun buildMessageContent(messageContent: String): StringBuilder {
        return StringBuilder(messageContent).append("\n")
    }

    private fun checkJsonFile(context: Context, file: File) {
        // read file contents as string
        val content = readTextFromFile(file) ?: return
        // start building output log
        val output = buildMessageContent("Asset file: ${file.name}")
        output.append(content)

        val jsonIsWellFormed = try {
            isJsonWellFormed(content)
        } catch (e: LintingFailureException) {
            output.append(buildMessageContent("${e.message}"))
            false
        }

        // evaluate for errors and report output
        if (!jsonIsWellFormed) {
            context.report(
                ISSUE,
                Location.create(file),
                "Invalid JSON asset file: $output"
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class) // used for allowTrailingComma
    @Throws(LintingFailureException::class)
    private fun isJsonWellFormed(jsonString: String): Boolean {
        return try {
            // Attempt to parse the JSON string into a generic JsonElement
            // This will throw a SerializationException if the JSON is malformed.
            // Note, we are allowing trailing commas here because we made a separate lint rule
            // for them, and we really just want to test syntax here to ensure there wasn't a cp pasta error
            val jsonWithTrailingCommas = Json {
                allowTrailingComma = true
            }
            jsonWithTrailingCommas.parseToJsonElement(jsonString)
            true
        } catch (e: SerializationException) {
            throw LintingFailureException("Invalid JSON syntax: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw LintingFailureException("Invalid JSON syntax: ${e.message}")
        }
    }
}
