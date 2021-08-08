package pl.filipowm.opensource.ambassador.model

data class ProtectedBranch(val name: String, val canSomeoneMerge: Boolean, val canSomeonePush: Boolean)
