package com.filipowm.ambassador.model.project

typealias ProjectMapper<T> = suspend (T) -> Project?
