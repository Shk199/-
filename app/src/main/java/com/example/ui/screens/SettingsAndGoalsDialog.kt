package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.UserProfileGoalEntity
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary

@Composable
fun SettingsAndGoalsDialog(
    currentProfile: UserProfileGoalEntity?,
    onDismiss: () -> Unit,
    onSaveGoals: (primaryGoal: String, secondaryGoal: String) -> Unit
) {
    val predefinedGoals = listOf(
        "الانتظام في صلاة الفجر في وقتها",
        "المحافظة على السنن الرواتب والنوافل",
        "حفظ اللسان من الغيبة وفضول الكلام",
        "ترك عادة سيئة والتوبة النصوح منها",
        "ورد قرآني يومي وتدبر الآيات",
        "صلة الرحم والإحسان للأهل"
    )

    var selectedPrimaryGoal by remember {
        mutableStateOf(currentProfile?.primaryGoal ?: predefinedGoals.first())
    }

    var customNote by remember {
        mutableStateOf(currentProfile?.secondaryGoal ?: "الاستغفار اليومي وتزكية النفس")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = GoldPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تخصيص هدف التزكية الذكي",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "اختر هدفك الأساسي ليركز التطبيق عليه في التحديات والمتابعة اليومية:",
                    style = MaterialTheme.typography.bodySmall
                )

                predefinedGoals.forEach { goal ->
                    val isSelected = selectedPrimaryGoal == goal
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPrimaryGoal = goal },
                        color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPrimaryGoal = goal },
                                colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = customNote,
                    onValueChange = { customNote = it },
                    label = { Text("هدف ثانوي أو نية خاصة") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveGoals(selectedPrimaryGoal, customNote)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("حفظ الهدف", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
