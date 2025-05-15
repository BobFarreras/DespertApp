package com.deixebledenkaito.despertapp.data.repository.firestore

import com.deixebledenkaito.despertapp.domain.model.Alarm
import com.deixebledenkaito.despertapp.domain.repository.firestore.AlarmsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlarmsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AlarmsRepository {

    override fun getAlarms(): Flow<List<Alarm>> = callbackFlow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@callbackFlow
        val listenerRegistration = firestore
            .collection("usuaris")
            .document(userId)
            .collection("alarmes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val alarms = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Alarm::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(alarms)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun toggleAlarm(alarmId: String, enabled: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore
            .collection("usuaris")
            .document(userId)
            .collection("alarmes")
            .document(alarmId)
            .update("enabled", enabled)
            .await()
    }

    override suspend fun createAlarm(alarm: Alarm) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val docRef = firestore
            .collection("usuaris")
            .document(userId)
            .collection("alarmes")
            .document()

        val alarmWithId = alarm.copy(id = docRef.id)
        docRef.set(alarmWithId).await()
    }
}