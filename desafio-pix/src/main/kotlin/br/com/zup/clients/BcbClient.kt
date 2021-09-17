package br.com.zup.clients

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.annotation.Client

@Client("\${clients.url.bcb}")
interface BcbClient {

    @Post
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun createPixKey()

}