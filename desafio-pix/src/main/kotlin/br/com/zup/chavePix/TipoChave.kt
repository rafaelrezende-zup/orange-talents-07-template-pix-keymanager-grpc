package br.com.zup.chavePix

import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {
    UNKNOWN_TIPOCHAVE {
        override fun valida(chave: String?): Boolean {
            return false
        }
    },
    CPF {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) return false
            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    TELEFONE {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) return false
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) return false
            return chave.matches("^[A-Za-z0-9+_.-]+@(.+)\$".toRegex())
        }
    },
    ALEATORIA {
        override fun valida(chave: String?) = chave.isNullOrBlank()
    };

    abstract fun valida(chave : String?) : Boolean

}