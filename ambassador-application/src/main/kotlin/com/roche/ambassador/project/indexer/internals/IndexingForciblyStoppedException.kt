package com.roche.ambassador.project.indexer.internals

import java.util.concurrent.CancellationException

internal class IndexingForciblyStoppedException(message: String?) : CancellationException(message)
