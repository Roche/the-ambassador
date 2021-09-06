package com.filipowm.ambassador.model.project

data class ProtectedBranch(val name: String, val canSomeoneMerge: Boolean, val canSomeonePush: Boolean)
