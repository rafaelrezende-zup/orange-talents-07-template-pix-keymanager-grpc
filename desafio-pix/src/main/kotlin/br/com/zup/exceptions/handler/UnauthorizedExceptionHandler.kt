package br.com.zup.exceptions.handler

import br.com.zup.chavePix.UnauthorizedException
import br.com.zup.exceptions.ExceptionHandler
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import jakarta.inject.Singleton

@Singleton
class UnauthorizedExceptionHandler : ExceptionHandler<UnauthorizedException> {

    override fun handle(e: UnauthorizedException): StatusWithDetails {
        return StatusWithDetails(
            Status.PERMISSION_DENIED
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is UnauthorizedException
    }

}
