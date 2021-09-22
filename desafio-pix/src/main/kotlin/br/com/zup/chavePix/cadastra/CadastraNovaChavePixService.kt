package br.com.zup.chavePix.cadastra

import br.com.zup.chavePix.*
import br.com.zup.clients.BcbClient
import br.com.zup.clients.ErpItauClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraNovaChavePixService(
    val chavePixRepository: ChavePixRepository,
    val erpItauClient: ErpItauClient,
    val bcbClient: BcbClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastra(@Valid chavePix: NovaChavePix) : ChavePix {

        /**
         * Verifica existência da chave pix
         */
        logger.info("Verificando se chave já existe no banco de dados.")
        if (chavePixRepository.existsByChave(chavePix.chave)) {
            logger.error("Chave Pix ${chavePix.chave} já existente no banco de dados")
            throw ChavePixExistenteException("Chave Pix ${chavePix.chave} existente.")
        }

        /**
         * Busca conta bancária Itaú
         */
        val responseErp = erpItauClient.findBankAccount(
            clienteId = chavePix.idCliente!!,
            tipo = chavePix.tipoConta!!.name
        )
        val contaBancaria = responseErp.body()?.paraContaBancaria() ?: throw ClientNotFoundException("Cliente Itaú não encontrado.")

        /**
         * Registra chave Pix no Banco Central
         */
        logger.info("Registrando chave pix no BCB")
        val requestBcb = chavePix.paraCreatePixKeyRequest(contaBancaria)
        val responseBcb = bcbClient.createPixKey(requestBcb)
        if (responseBcb.status != HttpStatus.CREATED) {
            throw IllegalStateException("Erro ao registrar chave Pix no BCB")
        }

        /**
         * Em caso de tipo chave ALEATÓRIA, utilizar chave gerada pelo BCB
         */
        if (chavePix.tipoChave == TipoChave.ALEATORIA) {
            logger.info("Atribuindo chave PIX gerada pelo BCB")
            chavePix.chave = responseBcb.body()!!.key
        }

        /**
         * Salva os dados da Chave Pix no banco de dados
         */
        val chave = chavePix.paraChavePix(contaBancaria)
        chavePixRepository.save(chave)

        return chave
    }

}