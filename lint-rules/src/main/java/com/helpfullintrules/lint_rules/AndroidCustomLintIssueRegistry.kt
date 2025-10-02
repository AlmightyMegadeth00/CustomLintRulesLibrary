package com.helpfullintrules.lint_rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.helpfullintrules.lint_rules.detectors.JsonSchemaDetector
import com.helpfullintrules.lint_rules.detectors.JsonSyntaxDetector
import com.helpfullintrules.lint_rules.detectors.JsonTrailingCommaDetector
import com.helpfullintrules.lint_rules.detectors.MagicNumberDetector
import com.helpfullintrules.lint_rules.detectors.MagicStringDetector

class AndroidCustomLintIssueRegistry : IssueRegistry() {
    companion object {
        private val TAG = AndroidCustomLintIssueRegistry::class.java.simpleName
    }
    override val issues: List<Issue>
        get() = listOf(
            JsonSyntaxDetector.ISSUE,
            JsonTrailingCommaDetector.ISSUE,
            MagicStringDetector.ISSUE,
            MagicNumberDetector.ISSUE,
            JsonSchemaDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API

    override val vendor: Vendor?
        get() = Vendor(vendorName = TAG)
}