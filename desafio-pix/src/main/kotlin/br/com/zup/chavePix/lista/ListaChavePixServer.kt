package br.com.zup.chavePix.lista

import br.com.zup.*
import br.com.zup.chavePix.ChavePixRepository
import br.com.zup.exceptions.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.ZoneId

@Singleton
@ErrorHandler
class ListaChavePixServer(val chavePixRepository: ChavePixRepository) : KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun lista(
        request: ListaChavePixRequest,
        responseObserver: StreamObserver<ListaChavePixResponse>
    ) {
        logger.info("Listando chave pix do cliente: $request")

        if (request.idCliente.isNullOrBlank())
            throw IllegalStateException("Cliente Id Nulo ou Vazio.")

        val chavePix = chavePixRepository.findByIdCliente(request.idCliente)
            .map {
            ListaChavePixResponse.ChavePix
                .newBuilder()
                .setIdChave(it.id)
                .setTipoChave(TipoChave.valueOf(it.tipoChave.name))
                .setChave(it.chave)
                .setTipoConta(TipoConta.valueOf(it.tipoConta.name))
                .setDataCriacao(it.criadoEm.let {
                    val dataCriacao = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(dataCriacao.epochSecond)
                        .setNanos(dataCriacao.nano)
                        .build()
                })
                .build()
        }

        responseObserver.onNext(ListaChavePixResponse
            .newBuilder()
            .setIdCliente(request.idCliente)
            .addAllChaves(chavePix)
            .build())
        responseObserver.onCompleted()

    }

}