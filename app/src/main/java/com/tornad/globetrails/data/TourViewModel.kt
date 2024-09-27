package com.tornad.globetrails.data

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.tornad.globetrails.models.Tour
import com.tornad.globetrails.navigation.ROUTE_VIEW_TOUR
import java.util.UUID


class TourViewModel(
    var navController: NavController,
    var context: android.content.Context
) {

    fun saveTour(
        filePath: Uri,
        tourName: String,
        hotel: String,
        days: Int,
        nights: Int,
        charges: Double,
        includedInPackage: String,
        excludedFromPackage: String
    ) {
        val id = System.currentTimeMillis().toString()
        val storageReference = FirebaseStorage.getInstance().reference.child("TourImages/$id")

        storageReference.putFile(filePath).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val tourData = Tour(
                        id = id,
                        imageUrl = imageUrl,
                        tourName = tourName,
                        hotel = hotel,
                        days = days,
                        nights = nights,
                        charges = charges,
                        includedInPackage = includedInPackage,
                        excludedFromPackage = excludedFromPackage
                    )

                    val dbRef = FirebaseDatabase.getInstance().getReference("Tours/$id")
                    dbRef.setValue(tourData).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Tour saved successfully", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_VIEW_TOUR)
                        } else {
                            Toast.makeText(context, "Failed to save tour", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Image upload failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun viewTour(tour: MutableState<Tour>, tours: MutableList<Tour>) {
        val ref = FirebaseDatabase.getInstance().getReference("Tours")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tours.clear()
                for (snap in snapshot.children) {
                    val value = snap.getValue(Tour::class.java)
                    value?.let { tours.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getTourById(tourId: String, tour: (Tour) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Tours/$tourId")
        ref.get().addOnSuccessListener { snapshot ->
            val tourData = snapshot.getValue(Tour::class.java)
            if (tourData != null) {
                tour(tourData)
            } else {
                Toast.makeText(context, "Tour not found", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to retrieve tour data", Toast.LENGTH_LONG).show()
        }
    }

    fun updateTour(
        filePath: Uri?,
        tourName: String,
        hotel: String,
        days: Int,
        nights: Int,
        charges: Double,
        includedInPackage: String,
        excludedFromPackage: String,
        id: String,
        currentImageUrl: String
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Tours/$id")

        // Retrieve the current tour data
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            val currentTour = dataSnapshot.getValue(Tour::class.java)

            if (currentTour != null) {
                // Prepare updated tour data using non-null input or keep current data
                val updatedTour = Tour(
                    imageUrl = if (filePath != null && filePath != Uri.EMPTY) "" else currentTour.imageUrl,
                    tourName = tourName,
                    hotel = hotel,
                    days = days,
                    nights = nights,
                    charges = charges,
                    includedInPackage = includedInPackage,
                    excludedFromPackage = excludedFromPackage,
                    id = id
                )

                // If a new image is provided, upload it, then update the tour record
                if (filePath != null && filePath != Uri.EMPTY) {
                    val storageReference = FirebaseStorage.getInstance().reference
                    val imageRef = storageReference.child("TourImages/${UUID.randomUUID()}.jpg")

                    imageRef.putFile(filePath)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                updatedTour.imageUrl = uri.toString() // Update the image URL
                                databaseReference.setValue(updatedTour)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Tour updated successfully", Toast.LENGTH_SHORT).show()
                                            navController.navigate(ROUTE_VIEW_TOUR)
                                        } else {
                                            Toast.makeText(context, "Failed to update tour: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // No new image, just update other fields
                    databaseReference.setValue(updatedTour)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Tour updated successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(ROUTE_VIEW_TOUR)
                            } else {
                                Toast.makeText(context, "Failed to update tour: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(context, "Tour not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to retrieve tour data", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTour(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Tours/$id")

        dbRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Tour deleted successfully", Toast.LENGTH_LONG).show()
                this.navController.navigate(ROUTE_VIEW_TOUR)
            } else {
                Toast.makeText(context, task.exception?.message ?: "Failed to delete tour", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun bookTour(
        tourId: String,
        userId: String,
        bookingDate: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Bookings/$tourId/$userId")

        // Create a map of booking data
        val bookingData = mapOf(
            "userId" to userId,
            "tourId" to tourId,
            "bookingDate" to bookingDate
        )

        // Save booking information to the database
        dbRef.setValue(bookingData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Tour booked successfully!", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_VIEW_TOUR)
            } else {
                Toast.makeText(context, "Failed to book the tour: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


