package br.com.zup.exceptions.handler

import br.com.zup.exceptions.ExceptionHandler
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import jakarta.inject.Singleton
import javax.validation.ValidationException

@Singleton
class ValidationExceptionHandler : ExceptionHandler<ValidationException> {

    override fun handle(e: ValidationException): StatusWithDetails {
        return StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription("Dados inválidos. " + e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ValidationException
    }

}