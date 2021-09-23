package br.com.zup.chavePix.consulta

import br.com.zup.ConsultaChavePixResponse
import br.com.zup.chavePix.ChavePixNotFoundException
import br.com.zup.chavePix.ChavePixRepository
import br.com.zup.chavePix.UnauthorizedException
import br.com.zup.clients.BcbClient
import br.com.zup.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): ConsultaChavePixResponse

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val idCliente: String,
        @field:NotBlank @field:ValidUUID val idChave: String
    ) : Filtro() {

        override fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): ConsultaChavePixResponse {
            val possivelChave = repository.findById(idChave)
            if (possivelChave.isEmpty) {
                throw ChavePixNotFoundException("Chave Pix ${idChave} não encontrada.")
            }
            val chavePix = possivelChave.get()
            if (chavePix.idCliente != idCliente) {
                throw UnauthorizedException("Permissão negada. Por favor, utilize seu Id Cliente.")
            }
            return ConsultaChavePixResponseConverter().deChavePix(chavePix)
        }

    }

    @Introspected
    data class PorChavePix(
        @field:NotBlank @field:Size(max = 77) val chavePix: String
    ) : Filtro() {

        override fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): ConsultaChavePixResponse {
            val possivelChave = repository.findByChave(chavePix)
            if (possivelChave.isPresent) {
                return ConsultaChavePixResponseConverter().deChavePix(possivelChave.get())
            }
            val responseBcb = bcbClient.consultaPixKey(chavePix)
            if (responseBcb.status != HttpStatus.OK) {
                throw ChavePixNotFoundException("Chave Pix ${chavePix} não encontrada.")
            }
            return ConsultaChavePixResponseConverter().deResponseBcb(responseBcb.body())
        }

    }

    @Introspected
    class Unknown() : Filtro() {

        override fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): ConsultaChavePixResponse {
            throw IllegalArgumentException("Chave Pix não informada.")
        }

    }

}
