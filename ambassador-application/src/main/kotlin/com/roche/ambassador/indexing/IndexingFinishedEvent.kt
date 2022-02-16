package com.roche.ambassador.indexing

import com.roche.ambassador.model.events.Event
import com.roche.ambassador.storage.indexing.Indexing

open class IndexingFinishedEvent(data: Indexing) : Event<Indexing>(data = data)
