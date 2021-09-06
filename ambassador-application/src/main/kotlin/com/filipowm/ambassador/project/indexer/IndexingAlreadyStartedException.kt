package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.exceptions.Exceptions

class IndexingAlreadyStartedException(message: String?) : Exceptions.IndexingException(message)
