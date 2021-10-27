package com.roche.ambassador.model.project

typealias ProjectMapper<T> = suspend (T) -> Project?
