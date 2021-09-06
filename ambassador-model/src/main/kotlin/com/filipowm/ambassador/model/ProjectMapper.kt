package com.filipowm.ambassador.model

typealias ProjectMapper<T> = suspend (T) -> Project?
