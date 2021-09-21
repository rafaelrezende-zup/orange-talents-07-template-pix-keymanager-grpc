package br.com.zup.chavePix.remove

import br.com.zup.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.RemoveChavePixRequest
import br.com.zup.RemoveChavePixResponse
import br.com.zup.chavePix.cadastra.paraRemoveChavePix
import br.com.zup.exceptions.ErrorHandler
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@ErrorHandler
@Singleton
class RemoveChavePixServer(val service: RemoveChavePixService) : KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun remove(
        request: RemoveChavePixRequest,
        responseObserver: StreamObserver<RemoveChavePixResponse>
    ) {
        logger.info("Removendo chave pix: $request")

        val removeChavePix = request.paraRemoveChavePix()
        service.remove(removeChavePix)

        responseObserver.onNext(RemoveChavePixResponse.getDefaultInstance())
        responseObserver.onCompleted()
    }

}