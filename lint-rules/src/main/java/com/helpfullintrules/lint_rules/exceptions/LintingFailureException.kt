package com.helpfullintrules.lint_rules.exceptions

public open class LintingFailureException : IllegalArgumentException {

    /**
     * Creates an instance of [LintingFailureException] without any details.
     */
    public constructor()

    /**
     * Creates an instance of [LintingFailureException] with the specified detail [message].
     */
    public constructor(message: String?) : super(message)

    /**
     * Creates an instance of [LintingFailureException] with the specified detail [message], and the given [cause].
     */
    public constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * Creates an instance of [LintingFailureException] with the specified [cause].
     */
    public constructor(cause: Throwable?) : super(cause)
}