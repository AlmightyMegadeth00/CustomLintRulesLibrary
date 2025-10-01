package com.helpfullintrules.utils

import java.io.File
import java.io.IOException

object FileUtils {
    private const val TWO_GB_IN_BYTES = 2L * 1024 * 1024 * 1024

    /** Function to check if a filePath is larger than 2GB
     *
     * @param filePath The path to the file to be checked
     * @return Boolean true if file is larger than 2GB in bytes
     */
    fun isFileLargerThan2GB(filePath: String): Boolean {
        val file = File(filePath)

        // Check if the file exists and is actually a file (not a directory)
        if (!file.exists() || !file.isFile) {
            return false // Or throw an exception, depending on your error handling
        }
        val fileSizeInBytes = file.length()

        return fileSizeInBytes > TWO_GB_IN_BYTES
    }

    /**
    * Ignore files over 2 GB, which is the maximum according to kotlin standard library documentation
    * https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.io/read-text.html
     *
     * @param File file to be read
     * @return String if appropriate text is found otherwise null
    */
    fun readTextFromFile(file: File): String? {
        return try {
            if (!FileUtils.isFileLargerThan2GB(file.absolutePath)) {
                file.readText()
            } else {
                null
            }
        } catch (e: IOException) {
            println(e.message)
            null
        }
    }
}