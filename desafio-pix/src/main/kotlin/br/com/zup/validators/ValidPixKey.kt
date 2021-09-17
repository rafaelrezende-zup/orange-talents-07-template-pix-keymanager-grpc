package br.com.zup.validators

import br.com.zup.chavePix.cadastra.NovaChavePix
import jakarta.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValidPixKeyValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
annotation class ValidPixKey(
    val message: String = "Formato da chave PIX inválido.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePix> {
    override fun isValid(value: NovaChavePix?, context: ConstraintValidatorContext?): Boolean {
        if (value?.tipoChave == null) {
            return false
        }

        return value.tipoChave.valida(value.chave)
    }
}