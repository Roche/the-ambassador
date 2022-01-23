package com.roche.ambassador.model.project

data class ProtectedBranch(val name: String,
                           val canDeveloperMerge: Boolean,
                           val canSomeoneMerge: Boolean,
                           val canDeveloperPush: Boolean,
                           val canAdminPush: Boolean,
                           val canForcePush: Boolean,
                           val codeReviewRequired: Boolean)
