package com.example.gestionsolicitudes.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun QrScanner(
    alDetectar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val callbackActual = rememberUpdatedState(alDetectar)

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(context, lifecycleOwner, previewView) {
        val cameraExecutor = Executors.newSingleThreadExecutor()

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val barcodeScanner = BarcodeScanning.getClient(options)

        val disposed = AtomicBoolean(false)
        val detected = AtomicBoolean(false)

        val preview = Preview.Builder()
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            if (disposed.get() || detected.get()) {
                imageProxy.close()
            } else {
                val mediaImage = imageProxy.image
                if (mediaImage == null) {
                    imageProxy.close()
                } else {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    try {
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                val qr = barcodes.firstOrNull {
                                    it.format == Barcode.FORMAT_QR_CODE && it.rawValue != null
                                }

                                if (
                                    qr != null &&
                                    !disposed.get() &&
                                    detected.compareAndSet(false, true)
                                ) {
                                    imageAnalysis.clearAnalyzer()
                                    callbackActual.value(qr.rawValue!!)
                                }
                            }
                            .addOnFailureListener { error ->
                                Log.e("QrScanner", "Error al analizar código QR", error)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } catch (error: Exception) {
                        Log.e("QrScanner", "Error al procesar imagen", error)
                        imageProxy.close()
                    }
                }
            }
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var boundProvider: ProcessCameraProvider? = null

        cameraProviderFuture.addListener({
            if (!disposed.get()) {
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    if (!disposed.get()) {
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        boundProvider = cameraProvider
                    }
                } catch (error: Exception) {
                    Log.e("QrScanner", "Error al iniciar la cámara", error)
                }
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            disposed.set(true)
            imageAnalysis.clearAnalyzer()
            boundProvider?.unbind(preview, imageAnalysis)
            barcodeScanner.close()
            cameraExecutor.shutdown()
        }
    }
}
