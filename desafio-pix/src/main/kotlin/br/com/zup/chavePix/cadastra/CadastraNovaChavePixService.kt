package br.com.zup.chavePix.cadastra

import br.com.zup.chavePix.ChavePix
import br.com.zup.chavePix.ChavePixExistenteException
import br.com.zup.chavePix.ChavePixRepository
import br.com.zup.clients.ErpItauClient
import io.micronaut.validation.Validated
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraNovaChavePixService(
    val chavePixRepository: ChavePixRepository,
    val erpItauClient: ErpItauClient
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
        val response = erpItauClient.findBankAccount(
            clienteId = chavePix.idCliente!!,
            tipo = chavePix.tipoConta!!.name
        )
        val contaBancaria = response.body()?.paraContaBancaria() ?: throw IllegalStateException("Cliente Itaú não encontrado.")

        /**
         * Salva os dados da Chave Pix no banco de dados
         */
        val chave = chavePix.paraChavePix(contaBancaria)
        chavePixRepository.save(chave)

        return chave
    }

}