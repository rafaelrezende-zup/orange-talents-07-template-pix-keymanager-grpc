package br.com.zup.exceptions.handler

import br.com.zup.chavePix.ClientNotFoundException
import br.com.zup.exceptions.ExceptionHandler
import br.com.zup.exceptions.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import jakarta.inject.Singleton

@Singleton
class ClientNotFoundExceptionHandler : ExceptionHandler<ClientNotFoundException> {

    override fun handle(e: ClientNotFoundException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ClientNotFoundException
    }

}