package com.filipowm.ambassador.configuration

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

class StringToEnumConverter : ConverterFactory<String, Enum<*>?> {
    override fun <T : Enum<*>?> getConverter(targetType: Class<T>): Converter<String, T> {
        val enumType: Class<T> = getEnumType(targetType)
        return StringToEnum(enumType)
    }

    private class StringToEnum<T : Enum<*>?>(private val enumType: Class<T>) : Converter<String, T> {
        override fun convert(source: String): T? {
            return if (source.isEmpty()) {
                // It's an empty enum identifier: reset the enum value to null.
                null
            } else java.lang.Enum.valueOf(enumType, source.trim { it <= ' ' }.toUpperCase()) as T
        }
    }

    companion object {
        private fun <T : Enum<*>?> getEnumType(targetType: Class<T>): Class<T> {
            var enumType: Class<T>? = targetType
            while (enumType != null && !enumType.isEnum) {
                enumType = enumType.superclass as Class<T>?
            }
            requireNotNull(enumType) { "The target type " + targetType.name + " does not refer to an enum" }
            return enumType
        }
    }
}
