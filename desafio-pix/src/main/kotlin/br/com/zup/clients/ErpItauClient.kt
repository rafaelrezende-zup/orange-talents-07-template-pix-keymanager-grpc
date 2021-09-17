package br.com.zup.clients

import br.com.zup.chavePix.ContaBancaria
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${clients.url.erp-itau}")
interface ErpItauClient {

    @Get("clientes/{clienteId}/contas{?tipo}")
    fun findBankAccount(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<BankAccountResponse>

    data class BankAccountResponse(
        val tipo: String,
        val agencia: String,
        val numero: String,
        val titular: Titular,
        val instituicao: Instituicao
    ) {

        fun paraContaBancaria(): ContaBancaria {
            return ContaBancaria(
                nome = titular.nome,
                cpf = titular.cpf,
                agencia = agencia,
                numero = numero,
                nomeInstituicao = instituicao.nome,
                ispbInstituicao = instituicao.ispb
            )
        }

    }

    data class Titular(
        val id: String,
        val nome: String,
        val cpf: String
    ) {

    }

    data class Instituicao(
        val nome: String,
        val ispb: String
    ) {

    }

}