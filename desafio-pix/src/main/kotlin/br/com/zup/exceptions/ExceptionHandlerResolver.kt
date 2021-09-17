package br.com.zup.exceptions

import br.com.zup.exceptions.handler.DefaultExceptionHandler

class ExceptionHandlerResolver(private val handlers : List<ExceptionHandler<*>>) {

    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    /**
     * We can replace the default exception handler through this constructor
     * https://docs.micronaut.io/latest/guide/index.html#replaces
     */
    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>) : this(handlers) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): ExceptionHandler<*> {
        val foundHandlers = handlers.filter { it.supports(e) }

        if (foundHandlers.size > 1)
            throw IllegalStateException("Too many handlers supporting the same exception '${e.javaClass.name}': $foundHandlers")

        return foundHandlers.firstOrNull() ?: defaultHandler
    }

}