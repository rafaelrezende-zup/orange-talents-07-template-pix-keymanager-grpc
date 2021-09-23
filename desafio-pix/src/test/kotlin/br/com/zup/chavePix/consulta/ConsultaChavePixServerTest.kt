package br.com.zup.chavePix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.KeymanagerConsultaGrpcServiceGrpc
import br.com.zup.chavePix.*
import br.com.zup.chavePix.cadastra.*
import br.com.zup.clients.BcbClient
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
import java.time.LocalDateTime
import java.util.*

@MicronautTest(transactional = false)
internal class ConsultaChavePixServerTest(
    val grpcClient: KeymanagerConsultaGrpcServiceGrpc.KeymanagerConsultaGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {

    @Inject
    lateinit var bcbClient: BcbClient

    companion object {
        val UUID_RANDOM = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve consultar chave pix pelo pix id`() {
        // cenario
        val chavePix = chavePix()
        repository.save(chavePix)

        // acao
        val response = grpcClient.consulta(ConsultaChavePixRequest
            .newBuilder()
            .setPixId(ConsultaChavePixRequest.ConsultaPixId
                .newBuilder()
                    .setIdCliente(chavePix.idCliente)
                    .setIdChave(chavePix.id))
            .build())

        // validacao
        with(response) {
            assertEquals(chavePix.id, idChave)
            assertEquals(chavePix.idCliente, idCliente)
            assertEquals(chavePix.chave, response.chavePix.chave)
        }

    }

    @Test
    fun `deve consultar chave pix pela chave pix sistema interno`() {
        // cenario
        val chavePix = chavePix()
        repository.save(chavePix)

        // acao
        val response = grpcClient.consulta(ConsultaChavePixRequest
            .newBuilder()
            .setChavePix(chavePix.chave)
            .build())

        // validacao
        with(response) {
            assertEquals(chavePix.id, idChave)
            assertNotNull(chavePix.idCliente, idCliente)
            assertEquals(chavePix.chave, response.chavePix.chave)
        }

    }

    @Test
    fun `deve consultar chave pix pela chave pix no bcb`() {
        // cenario
        val key = "teste@teste.com"
        `when`(
            bcbClient.consultaPixKey(key)
        ).thenReturn(HttpResponse.ok(pixKeyResponse(KeyType.EMAIL, key)))

        // acao
        val response = grpcClient.consulta(ConsultaChavePixRequest
            .newBuilder()
            .setChavePix(key)
            .build())

        // validacao
        with(response) {
            assertEquals(key, chavePix.chave)
        }

    }

    @Test
    fun `nao deve consultar chave pix quando dados invalidos`() {
        // cenario

        // acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(ConsultaChavePixRequest
                .newBuilder()
                .setPixId(ConsultaChavePixRequest.ConsultaPixId
                        .newBuilder()
                        .setIdCliente("")
                        .setIdChave(""))
                .setChavePix("")
                .build())
        }

        // validacao
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
        }

    }

    @Test
    fun `nao deve consultar chave pix quando dados nao informados`() {
        // cenario

        // acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(ConsultaChavePixRequest
                    .newBuilder()
                    .build()
            )
        }

        // validacao
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave Pix não informada.", status.description)
        }

    }

    @Test
    fun `nao deve consultar chave pix por pix id quando registros nao encontrados`() {
        // cenario

        // acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(ConsultaChavePixRequest
                .newBuilder()
                .setPixId(ConsultaChavePixRequest.ConsultaPixId
                    .newBuilder()
                    .setIdCliente(UUID_RANDOM)
                    .setIdChave(UUID_RANDOM))
                .build())
        }

        // validacao
        with(response) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix $UUID_RANDOM não encontrada.", status.description)
        }
    }

    @Test
    fun `nao deve consultar chave pix por pix id quando usuario nao autorizado`() {
        // cenario
        val chavePix = chavePix()
        repository.save(chavePix)

        // acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(ConsultaChavePixRequest
                .newBuilder()
                .setPixId(ConsultaChavePixRequest.ConsultaPixId
                    .newBuilder()
                    .setIdCliente(UUID.randomUUID().toString())
                    .setIdChave(chavePix.id))
                .build())
        }

        // validacao
        with(response) {
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            assertEquals("Permissão negada. Por favor, utilize seu Id Cliente.", status.description)
        }
    }

    @Test
    fun `nao deve consultar chave pix no bcb quando registros nao encontrados`() {
        // cenario
        `when`(bcbClient.consultaPixKey(UUID_RANDOM))
            .thenReturn(HttpResponse.notFound())

        // acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(ConsultaChavePixRequest
                .newBuilder()
                .setChavePix(UUID_RANDOM)
                .build())
        }

        // validacao
        with(response) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix $UUID_RANDOM não encontrada.", status.description)
        }
    }

    private fun chavePix() : ChavePix {
        return ChavePix(
            "61144478081",
            TipoChave.CPF,
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

    private fun pixKeyResponse(
        keyType: KeyType,
        key: String
    ): PixKeyResponse {
        return PixKeyResponse(
            keyType.toString(),
            key,
            BankAccount(
                "60701190",
                "3166",
                "21724",
                AccountType.CACC
            ),
            Owner(
                "Usuario Teste",
                "61144478081"
            ),
            LocalDateTime.now()
        )
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeymanagerConsultaGrpcServiceGrpc.KeymanagerConsultaGrpcServiceBlockingStub? {
            return KeymanagerConsultaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient() : BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }

}