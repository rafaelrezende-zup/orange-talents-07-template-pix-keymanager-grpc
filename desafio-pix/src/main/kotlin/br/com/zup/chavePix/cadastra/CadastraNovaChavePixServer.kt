package br.com.zup.chavePix.cadastra

import br.com.zup.KeymanagerCadastraGrpcServiceGrpc
import br.com.zup.NovaChavePixRequest
import br.com.zup.NovaChavePixResponse
import br.com.zup.chavePix.paraNovaChavePix
import br.com.zup.exceptions.ErrorHandler
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@ErrorHandler
@Singleton
class CadastraNovaChavePixServer(val service: CadastraNovaChavePixService) : KeymanagerCadastraGrpcServiceGrpc.KeymanagerCadastraGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastra(
        request: NovaChavePixRequest,
        responseObserver: StreamObserver<NovaChavePixResponse>
    ) {
        logger.info("Gerando nova chave pix para $request")

        val novaChave = request.paraNovaChavePix()
        val chave = service.cadastra(novaChave)

        responseObserver.onNext(NovaChavePixResponse
            .newBuilder()
            .setIdPix(chave.id)
            .build())

        responseObserver.onCompleted()
    }

}