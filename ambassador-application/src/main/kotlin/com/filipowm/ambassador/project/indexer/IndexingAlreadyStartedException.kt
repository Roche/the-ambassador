package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.storage.indexing.Indexing

class IndexingAlreadyStartedException(message: String?, val indexing: Indexing) : Exceptions.IndexingException(message)
