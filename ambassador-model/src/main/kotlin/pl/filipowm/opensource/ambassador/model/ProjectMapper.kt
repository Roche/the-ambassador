package pl.filipowm.opensource.ambassador.model

typealias ProjectMapper<T> = suspend (T) -> Project
