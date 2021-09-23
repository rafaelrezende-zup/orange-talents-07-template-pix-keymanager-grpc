package br.com.zup.chavePix.consulta

import br.com.zup.ConsultaChavePixResponse
import br.com.zup.chavePix.ChavePix
import br.com.zup.chavePix.cadastra.AccountType
import br.com.zup.chavePix.cadastra.CreatePixKeyResponse
import br.com.zup.chavePix.cadastra.KeyType
import com.google.protobuf.Timestamp
import java.time.ZoneId

class ConsultaChavePixResponseConverter {

    fun deChavePix(chave: ChavePix) : ConsultaChavePixResponse {
        return ConsultaChavePixResponse
            .newBuilder()
            .setIdCliente(chave.idCliente)
            .setIdChave(chave.idCliente)
            .setChavePix(ConsultaChavePixResponse.ChavePix
                .newBuilder()
                .setTipoChave(chave.tipoChave.toString())
                .setChave(chave.chave)
                .setContaBancaria(ConsultaChavePixResponse.ChavePix.BankAccount
                    .newBuilder()
                    .setNomeTitular(chave.contaAssociada.nome)
                    .setCpfTitular(chave.contaAssociada.cpf)
                    .setNomeInstituicao(chave.contaAssociada.nomeInstituicao)
                    .setAgencia(chave.contaAssociada.agencia)
                    .setConta(chave.contaAssociada.numero)
                    .setTipoConta(chave.tipoConta.toString()))
                .setDataCriacao(chave.criadoEm.let {
                    val dataCriacao = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(dataCriacao.epochSecond)
                        .setNanos(dataCriacao.nano)
                        .build()
                }))
            .build()
    }

    fun deResponseBcb(body: CreatePixKeyResponse?): ConsultaChavePixResponse {

        return ConsultaChavePixResponse
            .newBuilder()
            .setIdCliente("")
            .setIdChave("")
            .setChavePix(ConsultaChavePixResponse.ChavePix
                .newBuilder()
                .setTipoChave(KeyType
                    .from(KeyType
                        .valueOf(body!!.keyType)).toString())
                .setChave(body.key)
                .setContaBancaria(ConsultaChavePixResponse.ChavePix.BankAccount
                    .newBuilder()
                    .setNomeTitular(body.owner.name)
                    .setCpfTitular(body.owner.taxIdNumber)
                    .setNomeInstituicao(Instituicoes.nome(body.bankAccount.participant))
                    .setAgencia(body.bankAccount.branch)
                    .setConta(body.bankAccount.accountNumber)
                    .setTipoConta(AccountType
                        .from(body.bankAccount.accountType).toString()))
                .setDataCriacao(body.createdAt.let {
                    val dataCriacao = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(dataCriacao.epochSecond)
                        .setNanos(dataCriacao.nano)
                        .build()
                }))
            .build()
    }

}
