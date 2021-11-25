package com.roche.ambassador.indexing

import java.util.concurrent.CancellationException

class IndexingForciblyStoppedException(message: String?) : CancellationException(message)
