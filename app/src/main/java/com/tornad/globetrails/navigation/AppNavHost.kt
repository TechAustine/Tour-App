package com.tornad.globetrails.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileapp.ui.theme.screens.tour.TourView
import com.tornad.globetrails.data.TourViewModel
import com.tornad.globetrails.ui.theme.screens.login.LoginScreen
import com.tornad.globetrails.ui.theme.screens.register.RegistrationScreen
import com.tornad.globetrails.ui.theme.screens.tour.AddTour
import com.tornad.globetrails.ui.theme.screens.tour.UpdateTour

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_REGISTER,
    tourViewModel: TourViewModel
){
    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_REGISTER) { RegistrationScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_ADD_TOUR) { AddTour(navController) }
        composable(ROUTE_VIEW_TOUR) { TourView(navController, tourViewModel) }
        composable("$ROUTE_UPDATE_TOUR/{id}") { passedData ->
            UpdateTour(navController, passedData.arguments?.getString("id")!!) }

        }
    }
