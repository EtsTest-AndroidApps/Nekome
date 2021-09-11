package com.chesire.nekome.datasource.auth.local

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private const val ACCESS_TOKEN = "KEY_KITSU_ACCESS_TOKEN_V2"
private const val REFRESH_TOKEN = "KEY_KITSU_REFRESH_TOKEN_v2"

class LocalAuthV2Tests {

    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceProvider: PreferenceProvider
    private lateinit var localAuthV2: LocalAuthV2

    @Before
    fun setup() {
        sharedPreferencesEditor = mockk(relaxed = true)
        sharedPreferences = mockk(relaxed = true) {
            every { edit() } returns sharedPreferencesEditor
        }
        preferenceProvider = mockk {
            every { retrievePreferences(any(), any()) } returns sharedPreferences
        }
        localAuthV2 = LocalAuthV2(preferenceProvider)
    }

    @Test
    fun `hasCredentials returns true if access token exists`() {
        every { sharedPreferences.contains(ACCESS_TOKEN) } returns true
        every { sharedPreferences.contains(REFRESH_TOKEN) } returns false

        assertTrue(localAuthV2.hasCredentials)
    }

    @Test
    fun `hasCredentials returns true if refresh token exists`() {
        every { sharedPreferences.contains(ACCESS_TOKEN) } returns false
        every { sharedPreferences.contains(REFRESH_TOKEN) } returns true

        assertTrue(localAuthV2.hasCredentials)
    }

    @Test
    fun `hasCredentials returns true if both tokens exist`() {
        every { sharedPreferences.contains(ACCESS_TOKEN) } returns true
        every { sharedPreferences.contains(REFRESH_TOKEN) } returns true

        assertTrue(localAuthV2.hasCredentials)
    }

    @Test
    fun `hasCredentials returns false if neither tokens exist`() {
        every { sharedPreferences.contains(ACCESS_TOKEN) } returns false
        every { sharedPreferences.contains(REFRESH_TOKEN) } returns false

        assertFalse(localAuthV2.hasCredentials)
    }

    @Test
    fun `accessToken#get returns value from the shared preferences`() {
        val expected = "0123456789"
        every { sharedPreferences.getString(ACCESS_TOKEN, "") } returns expected

        val actual = localAuthV2.accessToken

        assertEquals(expected, actual)
    }

    @Test
    fun `accessToken#set sets value into shared preferences`() {
        val expected = "0123456789"

        localAuthV2.accessToken = expected

        verify { sharedPreferencesEditor.putString(ACCESS_TOKEN, expected) }
    }

    @Test
    fun `refreshToken#get returns value from the shared preferences`() {
        val expected = "0123456789"
        every { sharedPreferences.getString(REFRESH_TOKEN, "") } returns expected

        val actual = localAuthV2.refreshToken

        assertEquals(expected, actual)
    }

    @Test
    fun `refreshToken#set sets value into shared preferences`() {
        val expected = "0123456789"

        localAuthV2.refreshToken = expected

        verify { sharedPreferencesEditor.putString(REFRESH_TOKEN, expected) }
    }

    @Test
    fun `clear removes currently stored auth values`() {
        localAuthV2.clear()

        verify {
            sharedPreferencesEditor.remove(ACCESS_TOKEN)
            sharedPreferencesEditor.remove(REFRESH_TOKEN)
        }
    }
}
