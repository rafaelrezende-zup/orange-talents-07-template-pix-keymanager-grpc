package br.com.zup.chavePix.lista

import br.com.zup.KeymanagerListaGrpcServiceGrpc
import br.com.zup.ListaChavePixRequest
import br.com.zup.chavePix.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class ListaChavePixServerTest(
    val grpcClient: KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {

    companion object {
        val UUID_RANDOM = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        repository.saveAll(arrayListOf(
            chavePix(
                "usuario@teste.com",
                TipoChave.EMAIL),
            chavePix(
                "90767694007",
                TipoChave.CPF)
        ))
    }

    @Test
    fun `deve listar as chaves pix`() {
        // cenario

        // acao
        val response = grpcClient.lista(ListaChavePixRequest
            .newBuilder()
            .setIdCliente(UUID_RANDOM)
            .build())

        // validacao
        with(response) {
            assertEquals(2, chavesCount)
            assertEquals(UUID_RANDOM, idCliente)
        }

    }

    @Test
    fun `nao deve listar as chaves pix quando cliente id invalido`() {
        // cenario

        // acao
        val response = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.lista(
                ListaChavePixRequest
                    .newBuilder()
                    .build()
            )
        }

            // validacao
            with(response) {
                assertEquals(Status.FAILED_PRECONDITION.code, status.code)
                assertEquals("Cliente Id Nulo ou Vazio.", status.description)
            }

        }

    private fun chavePix(key: String, tipoChave: TipoChave) : ChavePix {
        return ChavePix(
            key,
            tipoChave,
            UUID_RANDOM,
            TipoConta.CONTA_CORRENTE,
            ContaBancaria(
                "Usuario Teste",
                "61144478081",
                "3166",
                "21724",
                "Banco",
                "60701190"
            )
        )
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceBlockingStub? {
            return KeymanagerListaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}