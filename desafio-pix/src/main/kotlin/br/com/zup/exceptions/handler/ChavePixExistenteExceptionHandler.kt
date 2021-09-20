package br.com.zup.exceptions.handler

import br.com.zup.chavePix.ChavePixExistenteException
import br.com.zup.exceptions.ExceptionHandler
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import jakarta.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler : ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: ChavePixExistenteException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }

}
