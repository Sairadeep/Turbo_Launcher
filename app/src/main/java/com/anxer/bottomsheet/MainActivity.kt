package com.anxer.bottomsheet

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.anxer.bottomsheet.ui.theme.BottomSheetTheme

class MainActivity : ComponentActivity() {
    private val listenerComponent =
        ComponentName(
            "com.anxer.bottomsheet",
            "com.anxer.bottomsheet.AppNotificationListenerService"
        )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomSheetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomSheetDemo()
                }
            }
        }
        val isPermissionGranted = checkNotificationListenerPermission(this, listenerComponent)
        if (!isPermissionGranted) {
            val permissionIntentLaunch =
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            this.startActivity(permissionIntentLaunch)
        } else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemo() {
    val myContext = LocalContext.current
    val toDisplayBS = remember { mutableStateOf(false) }
    val textValue = remember { mutableStateOf("Name") }
    val buttonText = remember { mutableStateOf("Open BS") }
    val packM = myContext.packageManager
    val listOfApps = packM.getInstalledApplications(PackageManager.GET_META_DATA)
    val requiredApps = listOfApps.filterNot {
        (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }
    val badgeNumber = remember { mutableStateMapOf<String, Int>() }
    val toShowBadge = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .padding(15.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                toDisplayBS.value = true
                            }
                        ) {
                            Icon(
                                Icons.TwoTone.Home,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Home Screen",
                        fontSize = 25.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
        if (toDisplayBS.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    toDisplayBS.value = false
                    buttonText.value = textValue.value
                },
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(30.dp),
                scrimColor = Color.DarkGray,
                sheetState = SheetState(
                    skipPartiallyExpanded = true,
                    skipHiddenState = false
                ),
                contentColor = Color.Black
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(
                        count = requiredApps.size,
                        itemContent = { index ->

                            when (requiredApps[index].packageName) {
                                "com.whatsapp" -> {
                                    badgeNumber["com.whatsapp"] =
                                        BadgeCount.getWhatsappBadgeCount()
                                    if (badgeNumber["com.whatsapp"] != 0) {
                                        toShowBadge.value = true
                                    }
                                }

                                "com.instagram.android" -> {
                                    badgeNumber["com.instagram.android"] =
                                        BadgeCount.getInstaBadgeCount()
                                    if (badgeNumber["com.instagram.android"] != 0) {
                                        toShowBadge.value = true
                                    }
                                }

                                else -> {
                                    toShowBadge.value = false
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .height(110.dp)
                                    .padding(10.dp)
                                    .clickable {
                                        Toast
                                            .makeText(
                                                myContext, "Clicked: ${
                                                    packM.getApplicationLabel(
                                                        packM.getApplicationInfo(
                                                            requiredApps[index].packageName,
                                                            PackageManager.GET_META_DATA
                                                        )
                                                    )
                                                }", Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row {
                                    val appInfo = packM.getApplicationInfo(
                                        requiredApps[index].packageName,
                                        PackageManager.GET_META_DATA
                                    )
                                    val appIcon = packM.getApplicationIcon(appInfo)
                                    Log.d(
                                        "AppPackageNames",
                                        requiredApps[index].packageName
                                    )
                                    if (appIcon is AdaptiveIconDrawable) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                            contentDescription = ""
                                        )
                                        if (toShowBadge.value) BadgedBox(badge = { Text(text = "${badgeNumber[requiredApps[index].packageName]}") }) {}

                                    } else {
                                        Icon(
                                            bitmap = (appIcon as BitmapDrawable).toBitmap()
                                                .asImageBitmap(), contentDescription = ""
                                        )
                                        if (toShowBadge.value) BadgedBox(badge = { Text(text = "${badgeNumber[requiredApps[index].packageName]}") }) {}
                                    }
                                }
                                Row {
                                    Text(
                                        text = "${
                                            packM.getApplicationLabel(
                                                packM.getApplicationInfo(
                                                    requiredApps[index].packageName,
                                                    PackageManager.GET_META_DATA
                                                )
                                            )
                                        }",
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        softWrap = true,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

fun checkNotificationListenerPermission(
    context: Context,
    listenerComponent: ComponentName
): Boolean {
    val enabledListeners =
        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return enabledListeners?.split(":")
        ?.contains(listenerComponent.flattenToString()) == true
}
