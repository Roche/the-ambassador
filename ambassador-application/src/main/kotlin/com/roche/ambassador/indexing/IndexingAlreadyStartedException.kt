package com.roche.ambassador.indexing

import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.storage.indexing.Indexing

class IndexingAlreadyStartedException(message: String?, val indexing: Indexing) : Exceptions.IndexingException(message)
