package br.com.zup.chavePix.cadastra

import br.com.zup.chavePix.ChavePix
import br.com.zup.chavePix.ContaBancaria
import br.com.zup.chavePix.TipoChave
import br.com.zup.chavePix.TipoConta
import br.com.zup.validators.ValidPixKey
import br.com.zup.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(
    @ValidUUID @field:NotBlank val idCliente: String?,
    @field:NotNull val tipoChave: TipoChave?,
    @field:Size(max = 77) var chave: String?,
    @field:NotNull val tipoConta: TipoConta?
) {
    fun paraChavePix(contaBancaria: ContaBancaria): ChavePix {
        return ChavePix(
            chave = this.chave!!,
            tipoChave = TipoChave.valueOf(this.tipoChave!!.name),
            idCliente = this.idCliente!!,
            tipoConta = TipoConta.valueOf(this.tipoConta!!.name),
            contaAssociada = contaBancaria
        )
    }

    fun paraCreatePixKeyRequest(contaBancaria: ContaBancaria): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = KeyType.by(tipoChave!!)!!,
            key = chave!!,
            bankAccount = BankAccount(
                contaBancaria.ispbInstituicao,
                contaBancaria.agencia,
                contaBancaria.numero,
                AccountType.by(tipoConta!!)!!
            ),
            owner = Owner(
                contaBancaria.nome,
                contaBancaria.cpf
            )
        )
    }
}