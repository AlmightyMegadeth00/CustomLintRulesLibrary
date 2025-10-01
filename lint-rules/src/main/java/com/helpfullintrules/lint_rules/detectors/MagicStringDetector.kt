package com.helpfullintrules.lint_rules.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression

class MagicStringDetector : Detector(), Detector.UastScanner {
    companion object {
        private val TAG = MagicStringDetector::class.java.simpleName

        private val ISSUE = Issue.create(
            id = "MagicStringUsage",
            briefDescription = "Avoid using magic strings",
            explanation = "Hardcoded strings that are used multiple times or represent important values should be extracted into string resources or constants for better maintainability and localization.",
            category = Category.PERFORMANCE,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(MagicStringDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(ULiteralExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitLiteralExpression(node: ULiteralExpression) {
                if (node.isString) {
                    val value = node.value as? String ?: return
                    if (value.isNotBlank() && !isResourceReference(value) && !isKnownConstant(value)) {
                        val magicStringMessage = "Magic string found: \"$value\""
                        context.report(ISSUE, node, context.getLocation(node), magicStringMessage)
                        println("$magicStringMessage for issue: ${ISSUE.getExplanation(TextFormat.TEXT)}")
                    }
                }
            }
        }
    }

    /*
     * We need to exclude resource files from the check ...
     */
    private fun isResourceReference(s: String): Boolean {
        return s.startsWith("@string/") || s.startsWith("@array/")
    }

    /*
     * ... and I feel like these might get annoying too
     */
    private fun isKnownConstant(s: String): Boolean {
        return s == "" || s == "true" || s == "false"
    }
}