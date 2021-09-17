package br.com.zup.exceptions

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class ExceptionHandlerInterceptor(private val resolver: ExceptionHandlerResolver) : MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        return try {
            context.proceed()
        } catch (e : Exception) {

            logger.error("Exception Handler: ${e.javaClass.name} while processing the call ${context.targetMethod}", e.message)

            @Suppress("UNCHECKED_CAST")
            val handler = resolver.resolve(e) as ExceptionHandler<Exception>
            val status = handler.handle(e)

            GrpcEndpointArguments(context).response()
                .onError(status.asRuntimeException())

            null
        }
    }

    private class GrpcEndpointArguments(val context : MethodInvocationContext<BindableService, Any?>) {

        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }

    }

}