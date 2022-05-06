package com.example.composemail.ui.home.toptoolbar

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.composemail.R


@Composable
fun ProfileButton(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_no_profile_pic),
        contentDescription = "Profile button"
    )
}