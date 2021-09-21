package com.filipowm.ambassador.project.indexer.internals

import java.util.concurrent.CancellationException

internal class IndexingForciblyStoppedException(message: String?) : CancellationException(message)
