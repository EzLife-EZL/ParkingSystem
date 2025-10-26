package com.parkingSystem.parkingSystem.admin

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import com.parkingSystem.parkingSystem.viewmodel.ParkingViewModel
@Composable
fun AdminParkingScreen(
    sharedPreferences: SharedPreferences,
) {
    val context = LocalContext.current

    var park_name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var type_vehicle by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var price by remember { mutableStateOf<Double?>(null) }

    var colsInput by remember { mutableStateOf("") }
    var rowsInput by remember { mutableStateOf("") }
    var slots by remember { mutableStateOf(listOf<Slot>()) }

    var selectedKeys by remember { mutableStateOf(setOf<String>()) }
    fun slotKey(x: String, y: String) = "${x}_${y}"

    val parkingViewModel: ParkingViewModel = viewModel(factory = viewModelFactory {
        initializer { ParkingViewModel(sharedPreferences) }
    })

    val parks by parkingViewModel.parks.collectAsState()

    LaunchedEffect(Unit) {
        parkingViewModel.fetchAllParksAvailable()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

    ) {
        item(key = "create_form") {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Create a parking lot", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = park_name,
                    onValueChange = { park_name = it },
                    label = { Text("Parking lot name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Parking lot address") },
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                val vehicleTypes = listOf("Car", "Bike")

                Box {
                    OutlinedTextField(
                        value = type_vehicle,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Type of vehicle") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                "contentDescription",
                                Modifier.clickable { expanded = true })
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        vehicleTypes.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = { type_vehicle = selectionOption; expanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = price?.toString() ?: "",
                    onValueChange = { input ->
                        price = input.toDoubleOrNull()
                    },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(16.dp))
                Text("Create parking lot Map", style = MaterialTheme.typography.titleMedium)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = colsInput,
                        onValueChange = { colsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Columns (X)") },
                        singleLine = true,
                        modifier = Modifier.size(100.dp)
                    )
                    OutlinedTextField(
                        value = rowsInput,
                        onValueChange = { rowsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Rows(Y)") },
                        singleLine = true,
                        modifier = Modifier.size(100.dp)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF002E5D),
                            contentColor = Color.White
                        ),
                        onClick = {
                            val c = colsInput.toIntOrNull() ?: 0
                            val r = rowsInput.toIntOrNull() ?: 0
                            if (c <= 0 || r <= 0) {
                                Toast.makeText(context, "Enter the number of columns/rows > 0", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val list = mutableListOf<Slot>()
                            var n = 1
                            for (y in 1..r) {
                                for (x in 1..c) {
                                    list += Slot(
                                        pos_X = x,
                                        pos_Y = y,
                                        slot_id = (n - 1).toString(),
                                        slotName = n.toString(),
                                        isBooked = false
                                    )
                                    n++
                                }
                            }
                            slots = list
                            selectedKeys = emptySet()
                        }) {
                        Text("Show Map")
                    }
                }

                if (slots.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    // FIX: Use non-lazy grid to avoid nested scrolling issues
                    MinimalParkingGridStatic(
                        slots = slots,
                        selectedKeys = selectedKeys,
                        onToggle = { x, y ->
                            val k = slotKey(x.toString(), y.toString())
                            selectedKeys = if (k in selectedKeys) selectedKeys - k else selectedKeys + k
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF4F4F),
                                contentColor = Color.White
                            ),
                            onClick = {
                                if (selectedKeys.isEmpty()) return@Button
                                slots = slots.filter { slotKey(it.pos_X.toString(),
                                    it.pos_Y.toString()
                                ) !in selectedKeys }
                                selectedKeys = emptySet()
                            },
                            enabled = selectedKeys.isNotEmpty()
                        ) { Text("Delete selected slot") }

                        OutlinedButton(
                            onClick = { selectedKeys = emptySet() },
                            enabled = selectedKeys.isNotEmpty(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF002E5D)
                            )
                        ) { Text("Uncheck") }
                    }
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002E5D),
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (park_name.isBlank() || type_vehicle.isBlank() || price == null || address.isBlank()) {
                            Toast.makeText(context, "Please enter complete information", Toast.LENGTH_SHORT).show()
                        } else {
                            parkingViewModel.createParkingLot(
                                context = context,
                                parkName = park_name,
                                address = address,
                                typeVehicleInput = type_vehicle,
                                priceNumber = price!!,
                                slotsInternal = slots,
                            )

                            park_name = ""
                            type_vehicle = ""
                            price = null
                            imageUri = null
                            colsInput = ""; rowsInput = ""; slots = emptyList()
                            selectedKeys = emptySet()

                            parkingViewModel.fetchAllParksAvailable()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create a parking lot")
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }

        item(key = "parks_header") {
            Text("List of parking lots", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        // FIX: Use stable keys and derivedStateOf
        items(
            items = parks,
            key = { park -> park.park_id }
        ) { park ->
            ParkCard(
                park = park,
                parkingViewModel = parkingViewModel
            )
        }

        item(key = "bottom_spacer") {
            Spacer(Modifier.height(80.dp))
        }
    }
}

// FIX: Replace LazyRow with static Row + horizontalScroll
@Composable
private fun MinimalParkingGridStatic(
    slots: List<Slot>,
    selectedKeys: Set<String>,
    onToggle: (Int, Int) -> Unit
) {
    if (slots.isEmpty()) return

    val maxX = slots.maxOfOrNull { it.pos_X ?: 0 } ?: 0
    val maxY = slots.maxOfOrNull { it.pos_Y ?: 0 } ?: 0
    if (maxX <= 0 || maxY <= 0) return

    val byPos = remember(slots) {
        slots.associateBy { it.pos_X to it.pos_Y }
    }

    Text("Parking lot map", style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(8.dp))

    // FIX: Use Row with horizontalScroll instead of LazyRow
    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = (maxY * 52).dp)
            .horizontalScroll(rememberScrollState())
    ) {
        for (colIndex in 0 until maxX) {
            val x = colIndex + 1
            Column {
                for (y in 1..maxY) {
                    val slot = byPos[x to y]
                    val selected = slot != null && ("${x}_${y}" in selectedKeys)

                    val baseModifier = Modifier
                        .size(44.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                slot == null -> Color.Transparent
                                slot.isBooked -> Color(0xFFFF0000)
                                selected -> Color(0xFF0083FF)
                                else -> Color(0xFFE5E7EB)
                            }
                        )
                        .border(
                            width = when {
                                slot == null -> 0.dp
                                selected -> 2.dp
                                else -> 2.dp
                            },
                            color = when {
                                slot == null -> Color.Transparent
                                selected -> Color(0xFF002E5D)
                                else -> Color(0xFF9CA3AF)
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                    Box(
                        modifier = if (slot != null)
                            baseModifier.clickable { onToggle(x, y) }
                        else baseModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        if (slot != null) {
                            Text(
                                slot.slotName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParkCard(
    park: Park,
    parkingViewModel: ParkingViewModel
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(park.park_name) }
    var editAddress by remember { mutableStateOf(park.address) }
    var editType by remember { mutableStateOf(park.type_vehicle) }
    var editPrice by remember { mutableStateOf(park.price.toString()) }

    // FIX: Use derivedStateOf for computed values
    var selectedKey by remember { mutableStateOf<String?>(null) }

    val byPos = remember(park.slots) {
        park.slots.associateBy { it.pos_X to it.pos_Y }
    }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(park.park_name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text("Address: ${park.address}", style = MaterialTheme.typography.bodySmall)
            Text("Vehicle: ${park.type_vehicle} • Price: ${park.price}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        editName = park.park_name
                        editAddress = park.address
                        editType = park.type_vehicle
                        editPrice = park.price.toString()
                        showEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002E5D),
                        contentColor = Color.White
                    )
                ) { Text("Edit") }

                OutlinedButton(
                    onClick = {
                        parkingViewModel.deleteParkById(park.park_id) {
                            Toast.makeText(context, "Park deleted", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF002E5D)
                    )
                ) { Text("Delete Park") }
            }

            Spacer(Modifier.height(8.dp))

            // FIX: Use static grid instead of lazy
            MinimalParkingGridStatic(
                slots = park.slots,
                selectedKeys = selectedKey?.let { setOf(it) } ?: emptySet(),
                onToggle = { x, y ->
                    val k = "${x}_${y}"
                    selectedKey = if (selectedKey == k) null else k
                }
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    enabled = selectedKey != null,
                    onClick = {
                        val key = selectedKey ?: return@OutlinedButton
                        Log.d("DeleteSlot", "Selected key: $key")

                        val (x, y) = key.split("_").map { it.toInt() }
                        val slot = byPos[x to y]

                        if (slot == null) {
                            Toast.makeText(context, "Choose slots in map", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        parkingViewModel.deleteSlotById(parkId = park.park_id, slotId = slot.slot_id) {
                            Toast.makeText(context, "Deleted slot", Toast.LENGTH_SHORT).show()
                            selectedKey = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002E5D),
                        contentColor = Color.White
                    )
                ) { Text("Delete Selected Slot") }

                OutlinedButton(
                    enabled = selectedKey != null,
                    onClick = { selectedKey = null },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF002E5D)
                    )
                ) { Text("Uncheck") }
            }

            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                val priceVal = editPrice.toDoubleOrNull()
                                if (editName.isBlank() || editAddress.isBlank() || editType.isBlank() || priceVal == null) {
                                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val updated = park.copy(
                                    park_name = editName,
                                    address = editAddress,
                                    type_vehicle = editType,
                                    price = priceVal
                                )
                                parkingViewModel.updateParkById(park.park_id, updated) {
                                    Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show()
                                    showEditDialog = false
                                }
                            }
                        ) { Text("Save") }
                    },
                    dismissButton = { OutlinedButton(onClick = { showEditDialog = false }) { Text("Cancel") } },
                    title = { Text("Modify park") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(editName, { editName = it }, label = { Text("Parking name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(editAddress, { editAddress = it }, label = { Text("Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(editType, { editType = it }, label = { Text("Type vehicle (Car/Bike)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(
                                value = editPrice,
                                onValueChange = { s -> editPrice = s.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Price") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    }
}