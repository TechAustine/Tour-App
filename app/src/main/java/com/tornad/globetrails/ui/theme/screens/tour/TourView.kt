package com.example.mobileapp.ui.theme.screens.tour

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.tornad.globetrails.data.TourViewModel
import com.tornad.globetrails.models.Tour
import com.tornad.globetrails.navigation.ROUTE_ADD_TOUR
import com.tornad.globetrails.navigation.ROUTE_UPDATE_TOUR
import com.tornad.globetrails.navigation.ROUTE_VIEW_TOUR

@Composable
fun TourView(navController: NavController, tourViewModel: TourViewModel) {
    LocalContext.current
    val selectedTour = remember { mutableStateOf(Tour()) }
    val tours = remember { mutableStateListOf<Tour>() }

    // Fetch tours using the ViewModel
    LaunchedEffect(Unit) {
        tourViewModel.viewTour(selectedTour, tours)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Available Tours",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Button(onClick = {
                navController.navigate(ROUTE_VIEW_TOUR)
            }) {
                Text(text = "View Tours")
            }
            Button(onClick = {
                navController.navigate(ROUTE_ADD_TOUR)
            }) {
                Text(text = "Add Tour")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tours) { tour ->
                TourItem(
                    tour = tour,
                    navController = navController,
                    tourViewModel = tourViewModel
                )
            }
        }
    }
}

@Composable
fun TourItem(
    tour: Tour,
    navController: NavController,
    tourViewModel: TourViewModel
) {
    val showFullText by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(150.dp)
                .animateContentSize(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = Color.LightGray,
            )
        ) {
            Row {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(tour.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(10.dp),
                        contentScale = ContentScale.Crop
                    )

                    Row {
                        Button(
                            modifier = Modifier.padding(2.dp),
                            onClick = {
                                tourViewModel.deleteTour(tour.id)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(Color.Red)
                        ) {
                            Text(
                                text = "DELETE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                                                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = {
                                navController.navigate(ROUTE_UPDATE_TOUR + "/${tour.id}")
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(Color.Green)
                        ) {
                            Text(
                                text = "EDIT",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Tour Name: ${tour.tourName}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hotel: ${tour.hotel}",
                        color = Color.White,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "${tour.days} Days / ${tour.nights} Nights",
                        color = Color.White,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "Charges: \$${tour.charges}",
                        color = Color.White,
                        fontSize = 9.sp
                    )

                    Text(
                        text = "Included",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tour.includedInPackage.joinToString(", "),
                        color = Color.Black,
                        fontSize = 9.sp,
                        maxLines = if (showFullText) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Excluded",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tour.excludedFromPackage.joinToString(", "),
                        color = Color.White,
                        fontSize = 9.sp,
                        maxLines = if (showFullText) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Button(onClick = {
                            navController.navigate(ROUTE_VIEW_TOUR)
                        }
                        ) {
                            Text(
                                text = "BOOK",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewTourPreview() {
    val navController = rememberNavController()
    val tourViewModel = TourViewModel(navController, LocalContext.current)
   TourView(navController, tourViewModel)
}
