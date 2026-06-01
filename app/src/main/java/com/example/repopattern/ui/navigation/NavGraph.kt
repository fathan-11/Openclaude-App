package com.example.repopattern.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.repopattern.ui.screens.userdetail.UserDetailScreen
import com.example.repopattern.ui.screens.userlist.UserListScreen

object Routes {
    const val USER_LIST = "user_list"
    const val USER_DETAIL = "user_detail/{userId}"
    fun userDetail(userId: Int) = "user_detail/$userId"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.USER_LIST) {
        composable(Routes.USER_LIST) {
            UserListScreen(onUserClick = { userId -> navController.navigate(Routes.userDetail(userId)) })
        }
        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) {
            UserDetailScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
