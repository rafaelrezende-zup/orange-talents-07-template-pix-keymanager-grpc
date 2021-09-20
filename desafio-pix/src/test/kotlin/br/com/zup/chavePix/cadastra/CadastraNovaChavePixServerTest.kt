package br.com.zup.chavePix.cadastra

import br.com.zup.KeymanagerCadastraGrpcServiceGrpc
import br.com.zup.NovaChavePixRequest
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chavePix.ChavePix
import br.com.zup.chavePix.ChavePixRepository
import br.com.zup.chavePix.ContaBancaria
import br.com.zup.chavePix.TipoChave.CPF
import br.com.zup.chavePix.TipoConta.CONTA_CORRENTE
import br.com.zup.clients.ErpItauClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

@MicronautTest(transactional = false)
internal class CadastraNovaChavePixServerTest(
    val grpcClient: KeymanagerCadastraGrpcServiceGrpc.KeymanagerCadastraGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {

    @Inject
    lateinit var erpItauClient: ErpItauClient

    companion object {
        val UUID_RANDOM = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave pix`() {
        // cenário
        `when`(erpItauClient.findBankAccount(clienteId = UUID_RANDOM, tipo = TipoConta.CONTA_CORRENTE.toString()))
            .thenReturn(HttpResponse.ok(bankAccountResponse()))

        // ação
        val response = grpcClient.cadastra(NovaChavePixRequest
            .newBuilder()
            .setChave("usuario@zup.com")
            .setIdCliente(UUID_RANDOM)
            .setTipoChave(TipoChave.EMAIL)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        // validação
        with(response) {
            assertNotNull(idPix)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave pix quando chave ja existente` () {
        // cenário
        val chavePix = chavePix()
        repository.save(chavePix)

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(NovaChavePixRequest
                .newBuilder()
                .setChave(chavePix.chave)
                .setIdCliente(chavePix.idCliente)
                .setTipoChave(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
            )
        }

        // validação
        with(response) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix ${chavePix.chave} existente.", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar nova chave pix quando dados invalidos`() {
        // cenário

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(NovaChavePixRequest
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

    @Test
    fun `nao deve cadastrar nova chave pix quando nao encontrar cliente itau`() {
        // cenário
        `when`(erpItauClient.findBankAccount(clienteId = UUID_RANDOM, tipo = TipoConta.CONTA_CORRENTE.toString()))
            .thenReturn(HttpResponse.notFound())

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(NovaChavePixRequest
                .newBuilder()
                .setChave("usuario@zup.com")
                .setIdCliente(UUID_RANDOM)
                .setTipoChave(TipoChave.EMAIL)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
            )
        }

        // validação
        with(response) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente Itaú não encontrado.", status.description)
        }
    }

    private fun bankAccountResponse(): ErpItauClient.BankAccountResponse {
        return ErpItauClient.BankAccountResponse(
            CONTA_CORRENTE.toString(),
            "3166",
            "21724",
            ErpItauClient.Titular(
                UUID_RANDOM,
                "Usuario Teste",
                "61144478081"),
            ErpItauClient.Instituicao(
                "Banco",
                "60701190"
            )
        )
    }

    private fun chavePix() : ChavePix {
        return ChavePix(
            "61144478081",
            CPF,
            UUID_RANDOM,
            CONTA_CORRENTE,
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
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeymanagerCadastraGrpcServiceGrpc.KeymanagerCadastraGrpcServiceBlockingStub? {
            return KeymanagerCadastraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ErpItauClient::class)
    fun erpItauClient() : ErpItauClient? {
        return Mockito.mock(ErpItauClient::class.java)
    }

}