package br.com.zup.chavePix

import br.com.zup.validators.ValidPixKey
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class ChavePix(
    @ValidPixKey @field:Size(max = 77) @field:NotBlank @field:Column(unique = true, length = 77) val chave: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:NotBlank @field:Column(nullable = false) val idCliente: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @field:Embedded val contaAssociada: ContaBancaria
) {

    @Id
    val id: String = UUID.randomUUID().toString()

    @field:CreationTimestamp
    var criadoEm: LocalDateTime = LocalDateTime.now()

}