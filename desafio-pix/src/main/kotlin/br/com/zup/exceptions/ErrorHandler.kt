package br.com.zup.exceptions

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type
import kotlin.annotation.AnnotationTarget.*

@Target(CLASS, FILE, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Around
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Type(ExceptionHandlerInterceptor::class)
annotation class ErrorHandler()
