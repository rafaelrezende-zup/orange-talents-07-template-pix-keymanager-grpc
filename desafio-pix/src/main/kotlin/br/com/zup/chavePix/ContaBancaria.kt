package br.com.zup.chavePix

import javax.persistence.Embeddable

@Embeddable
class ContaBancaria(
    val nome: String,
    val cpf: String,
    val agencia: String,
    val numero: String,
    val nomeInstituicao:String,
    val ispbInstituicao:String
){}