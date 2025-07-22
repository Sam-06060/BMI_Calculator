package com.example.composefirst

import android.os.Bundle
import android.media.AudioManager
import android.view.SoundEffectConstants
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import android.media.MediaPlayer
import com.example.composefirst.R // ensure correct package
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import com.example.composefirst.ui.theme.ComposefirstTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposefirstTheme {
                BMICalculatorApp()
            }
        }
    }
}

enum class Gender {
    Male,
    Female
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMICalculatorApp() {
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var height by remember { mutableStateOf(172) }
    var weight by remember { mutableStateOf(58) }
    var age by remember { mutableStateOf(22) }
    var showSheet by remember { mutableStateOf(false) }
    var bmiResult by remember { mutableStateOf(0f) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BMI Calculator") })
        },
        floatingActionButton = {
            Box(modifier = Modifier.padding(bottom = 50.dp)) {
                FloatingActionButton(onClick = {
                    bmiResult = weight / ((height / 100f) * (height / 100f))
                    scope.launch {
                    showSheet = true
                    delay(200) // slight delay to allow recomposition
                    sheetState.show()
                }
            },
                    containerColor = Color.Black
                    ) {
                    Text("BMI",color = Color.White)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GenderCard(Gender.Male, selectedGender == Gender.Male, {
                    selectedGender = Gender.Male
                })
                GenderCard(Gender.Female, selectedGender == Gender.Female, {
                    selectedGender = Gender.Female
                })
            }
            Spacer(modifier = Modifier.height(12.dp))
            FancyHeightPicker(height) { height = it }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeightPickerCard(
                    selectedWeight = weight,
                    onWeightChange = { weight = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                )
                AgeSelector(
                    age = age,
                    onAgeChange = { age = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                )
            }

        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1976D2)
            ) {
                AnimatedVisibility(
                    visible = showSheet,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your BMI is", color = Color.White)
                        Text(
                            String.format("%.1f kg/m²", bmiResult),
                            fontSize = 32.sp,
                            color = Color.White
                        )
                        Text(getBMICategory(bmiResult), color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "A BMI of 18.5 - 24.9 indicates that you are at a healthy weight...",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }
    }
}

fun getBMICategory(bmi: Float): String = when {
    bmi < 18.5 -> "(Underweight)"
    bmi < 24.9 -> "(Normal)"
    bmi < 29.9 -> "(Overweight)"
    else -> "(Obese)"
}

@Composable
fun GenderCard(gender: Gender, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val borderColor = when (gender) {
        Gender.Male -> if (isSelected) Color(0xFF2196F3) else Color.LightGray
        Gender.Female -> if (isSelected) Color(0xFFE91E63) else Color.LightGray
    }
    val imageRes = if (gender == Gender.Male) R.drawable.mars else R.drawable.venus
    val label = if (gender == Gender.Male) "Male" else "Female"

    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
            .border(3.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = imageRes), contentDescription = label, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label)
    }
}

@Composable
fun FancyHeightPicker(height: Int, onHeightChange: (Int) -> Unit) {
    val heightRange = (135..300).toList()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = heightRange.indexOf(height))

    val currentIndex by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex +
                    if (listState.firstVisibleItemScrollOffset > 30) 1 else 0
            index.coerceIn(heightRange.indices)
        }
    }

    val context = LocalContext.current
    val view = LocalView.current
    val haptics = LocalHapticFeedback.current
    var previousHeight by remember { mutableStateOf(height) }
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.tick) }

    LaunchedEffect(currentIndex) {
        val newHeight = heightRange[currentIndex]
        if (newHeight != previousHeight) {
            previousHeight = newHeight
            onHeightChange(newHeight)

            // ✅ Haptic Feedback
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)

            // ✅ Play custom tick.wav sound
            try {
                mediaPlayer.seekTo(0) // restart from beginning
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 16.dp)) {
            Text("Height (in cm)", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(height.toString(), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(60.dp)
            ) {
                items(heightRange.size) { index ->
                    val value = heightRange[index]
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(if (value % 5 == 0) 24.dp else 16.dp)
                                .background(
                                    if (value == height) Color.Black else Color.LightGray,
                                    shape = RoundedCornerShape(1.dp)
                                )
                        )
                        if (value % 5 == 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (value == height) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WeightPickerCard(
    selectedWeight: Int,
    onWeightChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val weights = (30..150).toList()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.tick) }

    var previousWeight by remember { mutableStateOf(selectedWeight) }

    // Scroll to selectedWeight initially
    LaunchedEffect(selectedWeight) {
        listState.scrollToItem(selectedWeight - 30)
    }

    // Detect scroll + play tick + haptic
    val currentIndex by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex +
                    if (listState.firstVisibleItemScrollOffset > 30) 1 else 0
            index.coerceIn(weights.indices)
        }
    }

    LaunchedEffect(currentIndex) {
        val newWeight = weights[currentIndex]
        if (newWeight != previousWeight) {
            previousWeight = newWeight
            onWeightChange(newWeight)

            // ✅ Haptic feedback
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)

            // ✅ Tick sound
            try {
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Weight (in kg)", color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.height(60.dp)
                ) {
                    items(weights.size) { index ->
                        val weight = weights[index]
                        val isSelected = weight == selectedWeight
                        Text(
                            text = weight.toString(),
                            color = if (isSelected) Color.Black else Color.Gray,
                            fontSize = if (isSelected) 28.sp else 20.sp,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clickable { onWeightChange(weight) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AgeSelector(age: Int, onAgeChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val haptics = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .padding(8.dp)
            .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Age", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (age > 1) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAgeChange(age - 1)
                    }
                }) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Age")
                }
                Text(
                    text = age.toString(),
                    fontSize = 28.sp
                )
                IconButton(onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAgeChange(age + 1)
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Age")
                }
            }
        }
    }
}

