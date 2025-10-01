package com.helpfullintrules.lint_rules.detectors

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.OtherFileScanner
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.helpfullintrules.lint_rules.Constants.INVALID_JSON_TRAILING_COMMA_ID
import com.helpfullintrules.lint_rules.Constants.JSON_FILE_EXTENSION
import com.helpfullintrules.utils.FileUtils.readTextFromFile
import java.io.File

class JsonTrailingCommaDetector: Detector(), OtherFileScanner {
    companion object {
        private val TAG = JsonTrailingCommaDetector::class.java.simpleName
        private val TRAILING_COMMA_REGEX = Regex(",\\s*[}\\]]")

        val ISSUE = Issue.create(
            id = INVALID_JSON_TRAILING_COMMA_ID,
            briefDescription = "Checks Json files in the assets directory for trailing commas",
            explanation = "JSON assets should be valid and follow a specific format that does not contain trailing commas. " +
                    "This check ensures the Json is well-formed to ensure runtime stability.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                JsonTrailingCommaDetector::class.java,
                Scope.OTHER_SCOPE
            ),
        )
    }


    override fun getApplicableFiles() = Scope.OTHER_SCOPE

    override fun run(context: Context) {
        context.project.assetFolders.forEach { assets ->
            if (assets.isDirectory) {
                assets.listFiles()?.forEach { file ->
                    checkFile(file, context, file.name)
                }
            }
        }
    }

    private fun checkFile(file: File, context: Context, output: String) {
        if (file.name.endsWith(JSON_FILE_EXTENSION)) {
            if (hasTrailingComma(file)) {
                context.report(
                    JsonSyntaxDetector.Companion.ISSUE,
                    Location.create(file),
                    "Trailing comma found in Json file: $output"
                )
            }
        }
    }

    private fun hasTrailingComma(file: File): Boolean {
        val content = readTextFromFile(file) ?: ""
        return TRAILING_COMMA_REGEX.containsMatchIn(content)
    }
}