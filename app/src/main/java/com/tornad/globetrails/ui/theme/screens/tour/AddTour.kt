package com.tornad.globetrails.ui.theme.screens.tour

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.tornad.globetrails.R
import com.tornad.globetrails.data.TourViewModel
import com.tornad.globetrails.navigation.ROUTE_VIEW_TOUR

@Composable
fun AddTour(navController: NavController) {
    val context = LocalContext.current
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val painter = rememberAsyncImagePainter(model = imageUri ?: R.drawable.ic_person)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    var tourName by remember { mutableStateOf("") }
    var hotel by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var nights by remember { mutableStateOf("") }
    var charges by remember { mutableStateOf("") }
    var includedInPackage by remember { mutableStateOf("") }
    var excludedFromPackage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add New Tour",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image Picker with Elevated Card
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
//                .size(180.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { launcher.launch("image/*") }
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(180.dp)
            )
        }

        Text(
            text = "Attach Tour Image",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Text Input Fields with Padding
        TourInputField(value = tourName, label = "Tour Name") { tourName = it }
        TourInputField(value = hotel, label = "Hotel") { hotel = it }
        TourInputField(value = days, label = "Days") { days = it }
        TourInputField(value = nights, label = "Nights") { nights = it }
        TourInputField(value = charges, label = "Charges") { charges = it }
        TourInputField(value = includedInPackage, label = "Included in Package") { includedInPackage = it }
        TourInputField(value = excludedFromPackage, label = "Excluded from Package") { excludedFromPackage = it }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Button(
                onClick = {
                    val tourViewModel = TourViewModel(navController, context)
                    if (imageUri != null) {
                        tourViewModel.saveTour(
                            filePath = imageUri!!,
                            tourName = tourName,
                            hotel = hotel,
                            days = days.toIntOrNull() ?: 0,
                            nights = nights.toIntOrNull() ?: 0,
                            charges = charges.toDoubleOrNull() ?: 0.0,
                            includedInPackage = includedInPackage,
                            excludedFromPackage = excludedFromPackage
                        )
                        navController.navigate(ROUTE_VIEW_TOUR)
                    } else {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_LONG).show()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
//                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Tour", fontSize = 9.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // View All Tours Button
            Button(
                onClick = { navController.navigate(ROUTE_VIEW_TOUR) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "View Tours", fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun TourInputField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 9.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp)
    )
}

@Preview(showBackground = true)
@Composable
fun AddTourPreview() {
    AddTour(rememberNavController())
}
