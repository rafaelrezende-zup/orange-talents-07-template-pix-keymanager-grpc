package br.com.zup.exceptions

import br.com.zup.chavePix.ChavePixExistenteException
import io.grpc.Status
import jakarta.inject.Singleton
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails as StatusWithDetails1

@Singleton
class ChavePixExistenteExceptionHandler : ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: ChavePixExistenteException): StatusWithDetails1 {
        return StatusWithDetails1(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }

}
