package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Store
import com.example.myapplication.data.Wish
import com.example.myapplication.ui.components.BackHeader
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.components.TipPill
import com.example.myapplication.ui.theme.CardCream
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Ink
import kotlinx.coroutines.delay

@Composable
fun WishesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var wishes by remember { mutableStateOf(Store.wishes(context)) }
    var showInput by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf<Wish?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2400)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize()) {
        SkyBackground(modifier = Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize()) {
            BackHeader("🍾 愿望漂流瓶", onBack)

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "这个暑假我想……",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "+ 写一个",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Coral, RoundedCornerShape(50))
                        .clickable { showInput = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }

            Spacer(Modifier.height(12.dp))

            if (wishes.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("🌊", fontSize = 56.sp)
                    Spacer(Modifier.height(14.dp))
                    Text("还没有漂流瓶", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("写下这个夏天最想做的事，把它投进海里。", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(wishes, key = { it.id }) { w ->
                        WishBottle(wish = w, onClick = { detail = w })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        AnimatedVisibility(
            visible = toast != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
        ) {
            TipPill(toast ?: "")
        }
    }

    if (showInput) {
        AlertDialog(
            onDismissRequest = { showInput = false },
            containerColor = CardCream,
            title = { Text("这个暑假我想：", color = Ink, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= 40) input = it },
                    placeholder = { Text("比如：学 Python / 去海边 / 骑车", color = Ink.copy(alpha = 0.4f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (input.isNotBlank()) {
                            wishes = Store.addWish(context, input)
                            input = ""
                            showInput = false
                            toast = "🍾 已投入夏日漂流瓶"
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Coral,
                        focusedTextColor = Ink,
                        unfocusedTextColor = Ink,
                        cursorColor = Coral,
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            wishes = Store.addWish(context, input)
                            input = ""
                            showInput = false
                            toast = "🍾 已投入夏日漂流瓶"
                        }
                    },
                ) { Text("投入漂流瓶 🌊", color = Coral, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showInput = false }) { Text("再想想", color = Ink.copy(alpha = 0.6f)) }
            },
        )
    }

    if (detail != null) {
        val w = detail!!
        AlertDialog(
            onDismissRequest = { detail = null },
            containerColor = CardCream,
            title = { Text(w.text, color = Ink, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (w.done) "✅ 已完成\n\n「这个夏天没有白过。」" else "还没有完成。\n夏天结束前，把它变成现实吧。",
                    color = Ink.copy(alpha = 0.8f),
                    lineHeight = 22.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    wishes = Store.toggleWish(context, w.id)
                    if (!w.done) toast = "🎉 「${w.text}」已完成！"
                    detail = null
                }) { Text(if (w.done) "标记未完成" else "✓ 标记完成", color = Coral, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    wishes = Store.deleteWish(context, w.id)
                    detail = null
                }) { Text("丢掉它", color = Ink.copy(alpha = 0.5f)) }
            },
        )
    }
}

@Composable
private fun WishBottle(wish: Wish, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🍾", fontSize = 26.sp)
        Column(
            Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                wish.text,
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (wish.done) TextDecoration.LineThrough else TextDecoration.None,
            )
            Spacer(Modifier.height(2.dp))
            Text(if (wish.done) "已完成" else "漂流中…", color = if (wish.done) Color(0xFF2FA36B) else Coral, fontSize = 12.sp)
        }
        if (wish.done) {
            Text("✓", color = Color(0xFF2FA36B), fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}
