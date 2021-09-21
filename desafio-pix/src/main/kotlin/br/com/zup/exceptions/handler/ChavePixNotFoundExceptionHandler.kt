package br.com.zup.exceptions.handler

import br.com.zup.chavePix.ChavePixNotFoundException
import br.com.zup.exceptions.ExceptionHandler
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import jakarta.inject.Singleton

@Singleton
class ChavePixNotFoundExceptionHandler : ExceptionHandler<ChavePixNotFoundException> {

    override fun handle(e: ChavePixNotFoundException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixNotFoundException
    }

}
