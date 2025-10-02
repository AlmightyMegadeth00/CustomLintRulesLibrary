package com.helpfullintrules.lint_rules.detectors

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.OtherFileScanner
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.google.gson.Gson
import com.helpfullintrules.lint_rules.Constants.JSON_FILE_EXTENSION
import io.github.optimumcode.json.schema.ErrorCollector
import io.github.optimumcode.json.schema.JsonSchema
import io.github.optimumcode.json.schema.ValidationError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class JsonSchemaDetector : Detector(), OtherFileScanner {

    companion object {
        val ISSUE = Issue.Companion.create(
            id = "JsonSchemaViolation",
            briefDescription = "JSON file does not conform to its schema",
            explanation = "This check validates JSON files against a schema to ensure structural correctness.",
            category = Category.Companion.CORRECTNESS,
            priority = 8,
            severity = Severity.FATAL,
            implementation = Implementation(
                JsonSchemaDetector::class.java,
                Scope.Companion.OTHER_SCOPE
            )
        )
    }

    override fun getApplicableFiles() = Scope.OTHER_SCOPE

    override fun run(context: Context) {
        val schemaFile: JsonSchema = getSampleSchemaFile()
        context.project.assetFolders.forEach { assets ->
            if (assets.isDirectory) {
                assets.listFiles()?.forEach { file ->
                    if (file.name.endsWith(JSON_FILE_EXTENSION)) {
                        val jsonText = file.readText()
                        val jsonElement: JsonElement = Json.Default.parseToJsonElement(jsonText)
                        val errors = mutableListOf<ValidationError>()
                        val validationResult = schemaFile.validate(jsonElement, ErrorCollector { error ->
                            errors.add(error)
                        })

                        if (!validationResult) {
                            context.report(
                                issue = ISSUE,
                                location = context.getLocation(file),
                                message = "JSON file does not conform to the schema: ${false}}."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getSampleSchemaFile(): JsonSchema {
        val key = "\$"
        return JsonSchema.fromDefinition(
                """
              {
                "${key}schema": "http://json-schema.org/draft-07/schema#",
                "title": "Product",
                "type": "object",
                "properties": {
                  "id": {
                    "type": "integer"
                  },
                  "name": {
                    "type": "string"
                  }
                },
                "required": ["id", "name"]
              }
              """.trimIndent(),
        )
    }
}