package br.com.zup.chavePix

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixRequest.FiltroConsultaCase.*
import br.com.zup.NovaChavePixRequest
import br.com.zup.RemoveChavePixRequest
import br.com.zup.chavePix.cadastra.NovaChavePix
import br.com.zup.chavePix.consulta.Filtro
import br.com.zup.chavePix.remove.RemoveChavePix
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun NovaChavePixRequest.paraNovaChavePix() : NovaChavePix {
    return NovaChavePix(
        idCliente = idCliente,
        tipoChave = TipoChave.valueOf(tipoChave.name),
        chave = chave,
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )
}

fun RemoveChavePixRequest.paraRemoveChavePix() : RemoveChavePix {
    return RemoveChavePix(
        idCliente = idCliente,
        idChave = idChave
    )
}

fun ConsultaChavePixRequest.paraConsultaChavePix(validator: Validator) : Filtro {

    val filtroConsulta = when(filtroConsultaCase) {
        PIXID -> pixId.let {
            Filtro.PorPixId(idCliente = pixId.idCliente, idChave = pixId.idChave)
        }
        CHAVEPIX -> Filtro.PorChavePix(chavePix)
        FILTROCONSULTA_NOT_SET -> Filtro.Unknown()
    }

    val validate = validator.validate(filtroConsulta)
    if (validate.isNotEmpty()) {
        throw ConstraintViolationException(validate)
    }

    return filtroConsulta
}

