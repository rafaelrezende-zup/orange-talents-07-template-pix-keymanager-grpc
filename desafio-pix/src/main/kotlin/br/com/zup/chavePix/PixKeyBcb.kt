package br.com.zup.chavePix.cadastra

import br.com.zup.chavePix.TipoChave
import br.com.zup.chavePix.TipoConta
import java.time.LocalDateTime

data class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
) {
}

data class PixKeyResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
}

enum class KeyType {
    CPF,
    PHONE,
    EMAIL,
    RANDOM;

    companion object {
        fun by(domainType: TipoChave): KeyType? {
            return when (domainType) {
                TipoChave.CPF -> CPF
                TipoChave.EMAIL -> EMAIL
                TipoChave.TELEFONE -> PHONE
                TipoChave.ALEATORIA -> RANDOM
                else -> null
            }
        }
        fun from(doaminType: KeyType) : TipoChave {
            return when (doaminType) {
                CPF -> TipoChave.CPF
                EMAIL -> TipoChave.EMAIL
                PHONE -> TipoChave.TELEFONE
                RANDOM -> TipoChave.ALEATORIA
            }
        }
    }
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {
}

enum class AccountType {
    CACC,
    SVGS;

    companion object {
        fun by(domainType: TipoConta): AccountType? {
            return when (domainType) {
                TipoConta.CONTA_CORRENTE -> CACC
                TipoConta.CONTA_POUPANCA -> SVGS
                else -> null
            }
        }
        fun from(doaminType: AccountType): TipoConta {
            return when (doaminType) {
                CACC -> TipoConta.CONTA_CORRENTE
                SVGS -> TipoConta.CONTA_POUPANCA
            }
        }
    }
}

data class Owner(
    val name: String,
    val taxIdNumber: String
) {
    val type: String = "NATURAL_PERSON"
}

data class RemovePixKeyRequest(
    val key: String,
    val participant: String
) {
}

data class RemovePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
) {
}