package com.helpfullintrules.lint_rules.detectors


import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.isNumberLiteral

class MagicNumberDetector : Detector(), Detector.UastScanner {
    companion object {
        private val TAG = MagicNumberDetector::class.java.simpleName

        val ISSUE = Issue.create(
            id = "MagicNumberUsage",
            briefDescription = "Avoid using magic numbers",
            explanation = "Hardcoded numbers that are used multiple times or represent important values should be extracted into constants for better maintainability.",
            category = Category.PERFORMANCE,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(MagicNumberDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(ULiteralExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitLiteralExpression(node: ULiteralExpression) {
                // only check numbers. TODO: find a clean way to exclude resource file constants
                if  (node.isNumberLiteral()) {
                    (node.value as Number?)?.let { value ->
                        // Exclude common and acceptable numbers
                        if (value.toInt() == 0 || value.toInt() == 1 || value.toInt() == -1) {
                            return
                        }
                        context.report(
                            ISSUE, node, context.getLocation(node),
                            "Avoid magic numbers " + value + "; define it as a named constant."
                        )
                    }
                }
            }
        }
    }
}