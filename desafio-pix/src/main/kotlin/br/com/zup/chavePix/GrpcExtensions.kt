package br.com.zup.chavePix.cadastra

import br.com.zup.NovaChavePixRequest
import br.com.zup.RemoveChavePixRequest
import br.com.zup.chavePix.TipoChave
import br.com.zup.chavePix.TipoConta
import br.com.zup.chavePix.remove.RemoveChavePix

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