package br.com.zup.chavePix.remove

import br.com.zup.chavePix.ChavePixNotFoundException
import br.com.zup.chavePix.ChavePixRepository
import br.com.zup.chavePix.UnauthorizedException
import br.com.zup.chavePix.cadastra.RemovePixKeyRequest
import br.com.zup.clients.BcbClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(
    val chavePixRepository: ChavePixRepository,
    val bcbClient: BcbClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun remove(@Valid removeChavePix: RemoveChavePix) {

        /**
         * Verifica existência da chave pix
         */
        logger.info("Verificando se chave existe no banco de dados.")
        val possivelChave = chavePixRepository.findById(removeChavePix.idChave!!)
        if (possivelChave.isEmpty) {
            logger.error("Chave Pix ${removeChavePix.idChave} não encontrada.")
            throw ChavePixNotFoundException("Chave Pix ${removeChavePix.idChave} não encontrada.")
        }

        /**
         * Verifica Id Cliente
         */
        val chavePix = possivelChave.get()
        if (chavePix.idCliente != removeChavePix.idCliente) {
            logger.error("Id Cliente diferente. Não autorizado.")
            throw UnauthorizedException("Permissão negada. Por favor, utilize seu Id Cliente.")
        }

        /**
         * Remove chave Pix no Banco Central
         */
        logger.info("Removendo chave pix no BCB")
        val requestBcb = RemovePixKeyRequest(
            chavePix.chave,
            chavePix.contaAssociada.ispbInstituicao
        )
        val responseBcb = bcbClient.removePixKey(
            chavePix.chave,
            requestBcb
        )
        if (responseBcb.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao remover chave Pix no BCB")
        }

        chavePixRepository.delete(chavePix)
    }

}