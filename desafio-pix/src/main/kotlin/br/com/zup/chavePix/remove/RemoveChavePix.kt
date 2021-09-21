package br.com.zup.chavePix.remove

import br.com.zup.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class RemoveChavePix(
    @ValidUUID @field:NotBlank val idCliente: String?,
    @ValidUUID @field:Size(max = 77) @field:NotBlank val idChave: String?
) {

}