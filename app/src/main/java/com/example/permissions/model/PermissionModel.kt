package com.example.permissions.model

class PermissionModel {
    var granted: Boolean = false
        private set
    var rationale: Boolean = false
        private set
    var dialogShows: Boolean = true
        private set
    private var denialCount: Int = 0

    fun request(userAllows: Boolean) {
        if (!dialogShows) return
        if (userAllows) {
            granted = true
            rationale = false
        } else {
            denialCount++
            granted = false
            if (denialCount == 1) {
                rationale = true
            } else if (denialCount >= 2) {
                rationale = false
                dialogShows = false
            }
        }
    }
}
