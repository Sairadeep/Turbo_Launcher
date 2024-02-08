package com.anxer.bottomsheet

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BottomSheetDemo() {
    val myContext = LocalContext.current
    val toDisplayBS = remember { mutableStateOf(false) }
    val app = applicationsDetails()

    val launcherApps: LauncherApps by lazy { myContext.getSystemService(LauncherApps::class.java) }
    // val shortcutAssist: ShortcutManager by lazy { myContext.getSystemService(ShortcutManager::class.java) }
    val badgeNumber = remember { mutableStateMapOf<String, Int>() }
    val longClickShortcutApp = remember { mutableStateMapOf<String, Boolean>() }
    val toShowBadge = remember { mutableStateOf(false) }
    val bottomBarStatus = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Scaffold(
            bottomBar = {
                if (bottomBarStatus.value) {
                    BottomAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(15.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {
                                    toDisplayBS.value = true
                                    bottomBarStatus.value = false
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
                }
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg),
                        contentDescription = "Background Image",
                        alignment = Alignment.Center,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                    }
                }

            }
        )
        if (toDisplayBS.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    toDisplayBS.value = false
                    bottomBarStatus.value = true
                },
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(30.dp),
                scrimColor = Color.Transparent,
                sheetState = SheetState(
                    skipPartiallyExpanded = true,
                    skipHiddenState = false
                ),
                contentColor = Color.White,
                containerColor = Color.Transparent
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(85.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(
                        count = app.size,
                        itemContent = { index ->
                            if (BadgeCount.getWhatsappBadgeCount(app[index].appPackage) != 0) {
                                toShowBadge.value = true
                                badgeNumber[app[index].appPackage] =
                                    BadgeCount.getWhatsappBadgeCount(app[index].appPackage)
                            } else {
                                toShowBadge.value = false
                            }

                            Column(
                                modifier = Modifier
                                    .height(110.dp)
                                    .padding(10.dp)
                                    .combinedClickable(
                                        onClick = {
                                            val intentToLaunch =
                                                myContext.packageManager.getLaunchIntentForPackage(
                                                    app[index].appPackage
                                                )
                                            if (intentToLaunch != null) myContext.startActivity(
                                                intentToLaunch
                                            ) else Toast
                                                .makeText(
                                                    myContext,
                                                    "Unable to open app",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            Log.d(
                                                "ClickHappenedOn", app[index].appName
                                            )
                                        },
                                        enabled = true,
                                        onLongClick = {
                                            longClickShortcutApp[app[index].appPackage] =
                                                true
                                        }
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row {
                                    if (longClickShortcutApp[app[index].appPackage] == true) {
                                        DropdownMenu(
                                            expanded = true,
                                            properties = PopupProperties(
                                                dismissOnClickOutside = true,
                                                dismissOnBackPress = true
                                            ),
                                            onDismissRequest = {
                                                longClickShortcutApp[app[index].appPackage] =
                                                    false
                                            },
                                            offset = DpOffset((-10).dp, (-160).dp),
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .size(200.dp, 75.dp)
                                                    .clip(
                                                        RoundedCornerShape(10.dp)
                                                    )
                                            ) {
                                                val shortcutQuery = LauncherApps.ShortcutQuery()
                                                shortcutQuery.setQueryFlags(FLAG_MATCH_DYNAMIC)
                                                    .setPackage(app[index].appPackage)
                                                val shortcuts = launcherApps.getShortcuts(
                                                    shortcutQuery,
                                                    Process.myUserHandle()
                                                )
                                                if (shortcuts != null) {
                                                    for (shortcut in shortcuts) {
                                                        Log.d(
                                                            "CurrentShortcuts",
                                                            "${app[index].appName} : $shortcuts"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Image(
                                        bitmap = (app[index].appIcon).asImageBitmap(),
                                        contentDescription = ""
                                    )
                                    if (toShowBadge.value) BadgedBox(badge = {
                                        Text(
                                            text = "${badgeNumber[app[index].appPackage]}",
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Left
                                        )
                                    }) {}
                                }
                                Row {
                                    Text(
                                        text = app[index].appName,
                                        fontSize = 14.sp,
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
