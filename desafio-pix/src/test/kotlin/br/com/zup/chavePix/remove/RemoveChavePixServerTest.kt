package br.com.zup.chavePix.remove

import br.com.zup.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.RemoveChavePixRequest
import br.com.zup.chavePix.*
import br.com.zup.chavePix.cadastra.CadastraNovaChavePixServerTest
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
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChavePixServerTest(
    val grpcClient: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {

    companion object {
        val UUID_RANDOM = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover chave pix`() {
        // cenário
        val chavePix = chavePix()
        repository.save(chavePix)

        // ação
        val response = grpcClient.remove(RemoveChavePixRequest
            .newBuilder()
            .setIdChave(chavePix.id)
            .setIdCliente(chavePix.idCliente)
            .build()
        )

        // validação
        with(response) {
            assertEquals("", idPix)
        }
    }

    @Test
    fun `nao deve remover uma chave pix quando chave nao encontrada`() {
        // cenário
        val chavePix = chavePix()
        repository.save(chavePix)

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChavePixRequest
                    .newBuilder()
                    .setIdChave(UUID_RANDOM)
                    .setIdCliente(chavePix.idCliente)
                    .build()
            )
        }

        // validação
        with(response) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix $UUID_RANDOM não encontrada.", status.description)
        }
    }

    @Test
    fun `nao deve remover uma chave pix quando cliente nao autorizado`() {
        // cenário
        val chavePix = chavePix()
        repository.save(chavePix)

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChavePixRequest
                    .newBuilder()
                    .setIdChave(chavePix.id)
                    .setIdCliente(UUID_RANDOM)
                    .build()
            )
        }

        // validação
        with(response) {
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            assertEquals("Permissão negada. Por favor, utilize seu Id Cliente.", status.description)
        }
    }

    @Test
    fun `nao deve remover uma chave pix quando dados invalidos`() {
        // cenário

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChavePixRequest
                    .newBuilder()
                    .build()
            )
        }

        // validação
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
        }
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub? {
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chavePix() : ChavePix {
        return ChavePix(
            "61144478081",
            TipoChave.CPF,
            CadastraNovaChavePixServerTest.UUID_RANDOM,
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

}