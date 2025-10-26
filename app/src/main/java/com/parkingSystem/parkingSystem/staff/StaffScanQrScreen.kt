package com.parkingSystem.parkingSystem.staff

import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.json.JSONObject
import java.util.concurrent.Executors

@Composable
fun StaffScanQrScreen(
    onBookingFound: (String) -> Unit, // Callback khi mã hợp lệ
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var qrText by remember { mutableStateOf<String?>(null) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    // Khởi tạo scanner MLKit
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val scanner = remember { BarcodeScanning.getClient(options) }

    // Giao diện camera preview + xử lý mã QR
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "QR SCANNER",
            style = MaterialTheme.typography.titleLarge
        )

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = cameraProviderFuture.get()

                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val preview = androidx.camera.core.Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder().build().also {
                        it.setAnalyzer(executor) { imageProxy ->
                            processImageProxy(scanner, imageProxy) { result ->
                                if (qrText == null) {
                                    qrText = result
                                }
                            }
                        }
                    }

                    val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, analyzer
                    )
                }
                previewView
            }
        )

        // Xử lý kết quả QR code (không dùng try/catch trong composable)
        val bookingId = remember(qrText) {
            qrText?.let {
                runCatching {
                    val json = JSONObject(it)
                    if (json.optString("type") == "parking-reservation") {
                        json.getString("bookingId")
                    } else null
                }.getOrNull()
            }
        }

        // Hiển thị kết quả
        when {
            qrText == null -> {
                Text("Đang chờ quét mã QR...", style = MaterialTheme.typography.bodyLarge)
            }

            bookingId != null -> {
                Column {
                    Text("✅ Mã hợp lệ", color = MaterialTheme.colorScheme.primary)
                    Text("Booking ID: $bookingId")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { onBookingFound(bookingId) }) {
                        Text("Kiểm tra thông tin đặt chỗ")
                    }
                }
            }

            else -> {
                Text("❌ Mã QR không hợp lệ hoặc không đọc được")
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onQrDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let(onQrDetected)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}