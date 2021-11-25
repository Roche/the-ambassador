package com.roche.ambassador.project.indexer

import com.roche.ambassador.events.Event
import com.roche.ambassador.storage.indexing.Indexing

class IndexingFinishedEvent(data: Indexing) : Event<Indexing>(data = data)