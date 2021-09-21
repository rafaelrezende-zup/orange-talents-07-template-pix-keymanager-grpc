package br.com.zup.exceptions.handler

import br.com.zup.exceptions.ExceptionHandler
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import jakarta.inject.Singleton

@Singleton
class IllegalStateExceptionHandler : ExceptionHandler<IllegalStateException> {

    override fun handle(e: IllegalStateException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is IllegalStateException
    }

}