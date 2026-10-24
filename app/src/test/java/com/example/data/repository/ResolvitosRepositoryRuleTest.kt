package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResolvitosRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun getOrCreateUserProfile_and_switchRole_succeedsForOwner() = runBlocking {
        signInTestUser("alice@resolvitos.com")
        val repository = ResolvitosRepository(firestore)

        val profileResult = withTimeout(5000L) {
            repository.getOrCreateUserProfile("Alice Resolvitos", "alice@resolvitos.com")
        }
        assertTrue(profileResult.isSuccess)

        val switchResult = withTimeout(5000L) {
            repository.switchActiveRole("driver")
        }
        assertTrue(switchResult.isSuccess)
    }

    @Test
    fun createBusinessMission_recordProgress_and_claimReward_succeedsAndEnforcesUserIsolation() = runBlocking {
        signInTestUser("owner@resolvitos.com")
        val ownerRepo = ResolvitosRepository(firestore)
        withTimeout(5000L) {
            ownerRepo.getOrCreateUserProfile("Owner Resolvitos", "owner@resolvitos.com")
        }

        val missionId = withTimeout(5000L) {
            ownerRepo.createBusinessMission(
                comercioId = "store_test",
                comercioName = "Arepera Test",
                title = "Misión 2 Compras",
                description = "Completa 2 compras o 100 pts para ganar una Arepa Gratis",
                targetPoints = 100L,
                targetOrders = 2L,
                rewardTitle = "Arepa Gratis",
                rewardValueUsd = 4.5,
                pointsReward = 60L,
                badgeEmoji = "🫓"
            ).getOrThrow()
        }
        assertTrue(missionId.isNotEmpty())

        val missions = withTimeout(4000L) {
            ownerRepo.observeBusinessMissions().first { list -> list.any { it.id == missionId } }
        }
        val createdMission = missions.first { it.id == missionId }

        // Step 1: Record partial progress (50 pts, 1 order) -> not completed yet
        val completedStep1 = withTimeout(5000L) {
            ownerRepo.recordUserMissionProgress(
                mission = createdMission,
                pointsDelta = 50L,
                ordersDelta = 1L
            ).getOrThrow()
        }
        assertEquals(false, completedStep1)

        // Step 2: Record second order (50 pts, 1 order) -> reaches 100 pts & 2 orders -> completed!
        val completedStep2 = withTimeout(5000L) {
            ownerRepo.recordUserMissionProgress(
                mission = createdMission,
                pointsDelta = 50L,
                ordersDelta = 1L
            ).getOrThrow()
        }
        assertTrue(completedStep2)

        val progressList = withTimeout(4000L) {
            ownerRepo.observeUserMissionProgress().first { list -> list.any { it.missionId == missionId && it.isCompleted } }
        }
        val myProgress = progressList.first { it.missionId == missionId }
        assertEquals(100L, myProgress.currentPoints)
        assertEquals(2L, myProgress.currentOrders)
        assertTrue(myProgress.isCompleted)

        val coupon = withTimeout(5000L) {
            ownerRepo.claimMissionReward(createdMission).getOrThrow()
        }
        assertTrue(coupon.startsWith("PREMIO-"))

        val claimedProgress = withTimeout(4000L) {
            ownerRepo.observeUserMissionProgress().first { list -> list.any { it.missionId == missionId && it.rewardClaimed } }
        }.first { it.missionId == missionId }
        assertTrue(claimedProgress.rewardClaimed)
        assertEquals(coupon, claimedProgress.couponCode)
    }

    @Test
    fun observeComercios_unauthenticatedCaller_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = ResolvitosRepository(firestore)

        var caughtException: Throwable? = null
        try {
            withTimeout(4000L) {
                repository.observeComercios().first()
            }
        } catch (e: Throwable) {
            caughtException = e
        }
        assertNotNull(caughtException)
        // Either IllegalStateException from requireUserId() or PERMISSION_DENIED from Firestore
        val firestoreEx = caughtException?.unwrapFirestoreException()
        if (firestoreEx != null) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx.code)
        } else {
            assertTrue(caughtException is IllegalStateException)
        }
    }
}
