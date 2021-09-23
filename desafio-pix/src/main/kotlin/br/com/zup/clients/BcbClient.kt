package br.com.zup.clients

import br.com.zup.chavePix.cadastra.CreatePixKeyRequest
import br.com.zup.chavePix.cadastra.PixKeyResponse
import br.com.zup.chavePix.cadastra.RemovePixKeyRequest
import br.com.zup.chavePix.cadastra.RemovePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${clients.url.bcb}")
interface BcbClient {

    @Post
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun createPixKey(@Body request: CreatePixKeyRequest) : HttpResponse<PixKeyResponse>

    @Delete("/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun removePixKey(@PathVariable key: String, @Body request: RemovePixKeyRequest) : HttpResponse<RemovePixKeyResponse>

    @Get("/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun consultaPixKey(@PathVariable key: String) : HttpResponse<PixKeyResponse>

}