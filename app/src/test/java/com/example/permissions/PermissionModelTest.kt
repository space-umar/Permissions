package com.example.permissions

import com.example.permissions.model.PermissionModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionModelTest {
    @Test
    fun testDenialStates() {
        val model = PermissionModel()
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertTrue(model.dialogShows)

        model.request(false)
        assertFalse(model.granted)
        assertTrue(model.rationale)
        assertTrue(model.dialogShows)

        model.request(false)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)

        model.request(true)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)
    }
}
