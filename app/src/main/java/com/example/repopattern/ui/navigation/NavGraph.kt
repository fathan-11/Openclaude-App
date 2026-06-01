package com.example.repopattern.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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

private const val ANIM_DURATION = 300

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.USER_LIST
    ) {
        composable(
            route = Routes.USER_LIST,
            enterTransition = { fadeIn(tween(ANIM_DURATION)) },
            exitTransition = { fadeOut(tween(ANIM_DURATION)) }
        ) {
            UserListScreen(
                onUserClick = { userId ->
                    navController.navigate(Routes.userDetail(userId))
                }
            )
        }

        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(ANIM_DURATION)
                )
            }
        ) {
            UserDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
