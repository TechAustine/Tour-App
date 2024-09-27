package com.tornad.globetrails.ui.theme.screens.tour

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.tornad.globetrails.R
import com.tornad.globetrails.data.TourViewModel
import com.tornad.globetrails.models.Tour
import com.tornad.globetrails.navigation.ROUTE_VIEW_TOUR

@Composable
fun UpdateTour(navController: NavController, tourId: String) {
    val context = LocalContext.current
    val tourViewModel = TourViewModel(navController, context)

    // MutableState for image URI and painter for image preview
    val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val painter = rememberImagePainter(data = imageUri.value ?: R.drawable.ic_person, builder = {
        crossfade(true)
    })
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }


    val tour = remember { mutableStateOf(Tour()) }


    LaunchedEffect(tourId) {
        tourViewModel.getTourById(tourId) { fetchedTour: Tour ->
            tour.value = fetchedTour
            imageUri.value = Uri.parse(fetchedTour.imageUrl)
        }
    }

    // State variables bound to the tour properties
    var tourName by remember { mutableStateOf(tour.value.tourName) }
    var hotel by remember { mutableStateOf(tour.value.hotel) }
    var days by remember { mutableStateOf(tour.value.days.toString()) }
    var nights by remember { mutableStateOf(tour.value.nights.toString()) }
    var charges by remember { mutableStateOf(tour.value.charges.toString()) }
    var includedInPackage by remember { mutableStateOf(tour.value.includedInPackage.joinToString(", ")) }
    var excludedFromPackage by remember { mutableStateOf(tour.value.excludedFromPackage.joinToString(", ")) }

    // Update state variables when tour object changes
    LaunchedEffect(tour.value) {
        tourName = tour.value.tourName
        hotel = tour.value.hotel
        days = tour.value.days.toString()
        nights = tour.value.nights.toString()
        charges = tour.value.charges.toString()
        includedInPackage = tour.value.includedInPackage.joinToString(", ")
        excludedFromPackage = tour.value.excludedFromPackage.joinToString(", ")
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Update Tour",
            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Image preview and image selector
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .size(300.dp)
                .clickable { launcher.launch("image/*") }
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tour name field
        OutlinedTextField(
            value = tourName,
            onValueChange = { tourName = it },
            label = { Text("Tour Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hotel field
        OutlinedTextField(
            value = hotel,
            onValueChange = { hotel = it },
            label = { Text("Hotel") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Days field
        OutlinedTextField(
            value = days,
            onValueChange = { days = it },
            label = { Text("Days") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nights field
        OutlinedTextField(
            value = nights,
            onValueChange = { nights = it },
            label = { Text("Nights") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Charges field
        OutlinedTextField(
            value = charges,
            onValueChange = { charges = it },
            label = { Text("Charges") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Included in Package field
        OutlinedTextField(
            value = includedInPackage,
            onValueChange = { includedInPackage = it },
            label = { Text("Included in Package") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Excluded from Package field
        OutlinedTextField(
            value = excludedFromPackage,
            onValueChange = { excludedFromPackage = it },
            label = { Text("Excluded from Package") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Update button
        Button(
            onClick = {
                // Validate form fields
                if (tourName.isNotEmpty() && hotel.isNotEmpty()) {
                    tourViewModel.updateTour(
                        filePath = imageUri.value,
                        tourName = tourName,
                        hotel = hotel,
                        days = days.toIntOrNull() ?: 0,
                        nights = nights.toIntOrNull() ?: 0,
                        charges = charges.toDoubleOrNull() ?: 0.0,
                        includedInPackage = includedInPackage.split(", ").toString(),
                        excludedFromPackage = excludedFromPackage.split(", ").toString(),
                        id = tourId,
                        currentImageUrl = tour.value.imageUrl
                    )
                    navController.navigate(ROUTE_VIEW_TOUR)
                } else {
                    Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Tour")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UpdateTourPreview() {
    UpdateTour(rememberNavController(), "tourId")
}