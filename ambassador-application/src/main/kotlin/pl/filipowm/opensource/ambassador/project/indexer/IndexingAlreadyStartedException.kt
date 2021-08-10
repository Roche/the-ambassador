package pl.filipowm.opensource.ambassador.project.indexer

import pl.filipowm.opensource.ambassador.exceptions.Exceptions

class IndexingAlreadyStartedException(message: String?) : Exceptions.IndexingException(message)