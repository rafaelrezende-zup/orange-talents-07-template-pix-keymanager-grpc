package br.com.zup.chavePix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixResponse
import br.com.zup.KeymanagerConsultaGrpcServiceGrpc
import br.com.zup.chavePix.ChavePixRepository
import br.com.zup.chavePix.paraConsultaChavePix
import br.com.zup.clients.BcbClient
import br.com.zup.exceptions.ErrorHandler
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.validation.Validator

@ErrorHandler
@Singleton
class ConsultaChavePixServer(
    val validator: Validator,
    val chavePixRepository: ChavePixRepository,
    val bcbClient: BcbClient
) : KeymanagerConsultaGrpcServiceGrpc.KeymanagerConsultaGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun consulta(
        request: ConsultaChavePixRequest,
        responseObserver: StreamObserver<ConsultaChavePixResponse>
    ) {
        logger.info("Consultando chave pix: $request")

        val chavePix = request.paraConsultaChavePix(validator)
        val chaveResponse = chavePix.filtra(chavePixRepository, bcbClient)

        responseObserver.onNext(chaveResponse)
        responseObserver.onCompleted()

    }

}