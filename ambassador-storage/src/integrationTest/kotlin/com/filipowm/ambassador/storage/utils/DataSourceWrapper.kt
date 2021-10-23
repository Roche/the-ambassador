package com.filipowm.ambassador.storage.utils

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.Callback
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

@Configuration
class DataSourceWrapper : BeanPostProcessor {

    companion object {
        private const val LOGGER_NAME = "com.filipowm.ambassador.storage.QueryLogger"
    }

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is DataSource) {
            val originalDataSource: DataSource = bean
            val proxyDataSource = ProxyDataSourceBuilder
                .create(originalDataSource)
                .name("DS-Proxy")
                .countQuery()
                .logQueryBySlf4j(SLF4JLogLevel.INFO, LOGGER_NAME)
                .logSlowQueryBySlf4j(100, TimeUnit.MILLISECONDS, SLF4JLogLevel.WARN, LOGGER_NAME)
                .build()
            return createProxy(originalDataSource, proxyDataSource)
        }
        return bean
    }

    private class ProxyDataSourceConnectionInterceptor(val target: DataSource, val interceptor: DataSource) : MethodInterceptor {

        override fun intercept(obj: Any, method: Method, args: Array<out Any>, proxy: MethodProxy): Any {
            return try {
                /**
                This is a workaround for checking if dataSource in context is also used in platform transaction manager.
                Actually, despite they are the same the equals returns false cause it's checking proxies
                see: SqlScriptsTestExecutionListener.executeSqlScripts
                 */
                if (method.name == "equals") {
                    return true
                }
                val dsMethod = DataSource::class.java.getMethod(method.name, *method.parameterTypes)
                dsMethod.invoke(interceptor, *args)
            } catch (e: Exception) {
                method.invoke(target, *args)
            }
        }

    }

    private fun createProxy(target: DataSource, interceptor: DataSource): Any {
        val enhancer = Enhancer()
        enhancer.setSuperclass(target::class.java)
        enhancer.setCallback(ProxyDataSourceConnectionInterceptor(target, interceptor) as Callback)
        return enhancer.create()
    }
}
