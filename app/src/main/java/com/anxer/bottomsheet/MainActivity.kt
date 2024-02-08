package com.anxer.bottomsheet

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_CACHED
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
import android.content.pm.PackageManager
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
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
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val appsList = packM.queryIntentActivities(intent, 0)
    val apps = appsList.map {
        val packageName = it.activityInfo.packageName
        val appName = it.loadLabel(packM).toString()
        val appIcon = it.loadIcon(packM).toBitmap()

        AppInfo(packageName, appName, appIcon)
    }

    val launcherApps: LauncherApps by lazy { myContext.getSystemService(LauncherApps::class.java) }
    // val shortcutAssist: ShortcutManager by lazy { myContext.getSystemService(ShortcutManager::class.java) }
    val badgeNumber = remember { mutableStateMapOf<String, Int>() }
    val longClickShortcutApp = remember { mutableStateMapOf<String, Boolean>() }
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
                LazyGridToDisplayApps(
                    apps,
                    badgeNumber,
                    toShowBadge,
                    packM,
                    myContext,
                    longClickShortcutApp,
                    launcherApps
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
private fun LazyGridToDisplayApps(
    apps: List<AppInfo>,
    badgeNumber: SnapshotStateMap<String, Int>,
    toShowBadge: MutableState<Boolean>,
    packM: PackageManager,
    myContext: Context,
    longClickShortcutApp: SnapshotStateMap<String, Boolean>,
    launcherApps: LauncherApps
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(85.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        items(
            count = apps.size,
            itemContent = { index ->
                val appsInOrder = apps.sortedBy {
                    it.appName
                }
                when (appsInOrder[index].appPackage) {
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
                        .combinedClickable(
                            onClick = {
                                val intentToLaunch =
                                    packM.getLaunchIntentForPackage(appsInOrder[index].appPackage)
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
                                    "ClickHappenedOn", appsInOrder[index].appName
                                )
                            },
                            enabled = true,
                            onLongClick = {
                                longClickShortcutApp[appsInOrder[index].appPackage] =
                                    true
                            }
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        if (longClickShortcutApp[appsInOrder[index].appPackage] == true) {
                            DropdownMenu(
                                expanded = true,
                                properties = PopupProperties(
                                    dismissOnClickOutside = true,
                                    dismissOnBackPress = true
                                ),
                                onDismissRequest = {
                                    longClickShortcutApp[apps[index].appPackage] =
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
                                    shortcutQuery.setQueryFlags(FLAG_MATCH_PINNED or FLAG_MATCH_CACHED or FLAG_MATCH_MANIFEST)
                                        .setPackage(appsInOrder[index].appPackage)
                                    Log.d(
                                        "CurrentShortcuts", "${
                                            launcherApps.getShortcuts(
                                                shortcutQuery,
                                                Process.myUserHandle()
                                            )
                                        }"
                                    )
                                }
                            }
                        }
                        Image(
                            bitmap = (appsInOrder[index].appIcon).asImageBitmap(),
                            contentDescription = ""
                        )
                        if (toShowBadge.value) BadgedBox(badge = { Text(text = "${badgeNumber[appsInOrder[index].appPackage]}", fontSize = 11.sp, textAlign = TextAlign.Left) }) {}
                    }
                    Row {
                        Text(
                            text = appsInOrder[index].appName,
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

fun checkNotificationListenerPermission(
    context: Context,
    listenerComponent: ComponentName
): Boolean {
    val enabledListeners =
        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return enabledListeners?.split(":")
        ?.contains(listenerComponent.flattenToString()) == true
}
