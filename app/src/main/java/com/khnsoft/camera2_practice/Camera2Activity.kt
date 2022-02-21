package com.khnsoft.camera2_practice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.khnsoft.camera2_practice.databinding.ActivityCamera2Binding

class Camera2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCamera2Binding

    private lateinit var surfaceViewHolder: SurfaceHolder
    private lateinit var cameraDevice: CameraDevice
    private lateinit var imageReader: ImageReader
    private lateinit var previewBuilder: CaptureRequest.Builder
    private lateinit var session: CameraCaptureSession

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamera2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        surfaceViewHolder = binding.surfaceViewCamera2.holder
        surfaceViewHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initCameraAndPreview()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraDevice.close()
            }
        })
    }

    private fun initCameraAndPreview() {
        val handlerThread = HandlerThread("CAMERA2")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        openCamera()
    }

    private fun openCamera() {
        try {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = cameraManager.getCameraCharacteristics(CAMERA_BACK)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val largestPreviewSize = map?.getOutputSizes(ImageFormat.JPEG)?.get(0) ?: return

            imageReader = ImageReader.newInstance(
                largestPreviewSize.width,
                largestPreviewSize.height,
                ImageFormat.JPEG,
                7
            )

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) return

            cameraManager.openCamera(CAMERA_BACK, deviceStateCallback, handler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val deviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(device: CameraDevice) {
            cameraDevice = device

            try {
                takePreview()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onDisconnected(device: CameraDevice) {
            device.close()
        }

        override fun onError(device: CameraDevice, error: Int) {
            Toast.makeText(
                this@Camera2Activity,
                "카메라를 열지 못했습니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun takePreview() {
        previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewBuilder.addTarget(surfaceViewHolder.surface)
        cameraDevice.createCaptureSession(
            listOf(surfaceViewHolder.surface, imageReader.surface), sessionPreviewStateCallback, handler
        )
    }

    private val sessionPreviewStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            this@Camera2Activity.session = session
            try {
                previewBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                this@Camera2Activity.session.setRepeatingRequest(previewBuilder.build(), null, handler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onConfigureFailed(p0: CameraCaptureSession) {
            Toast.makeText(
                this@Camera2Activity,
                "카메라 구성 실패",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        private const val CAMERA_BACK = "0"
        private const val CAMERA_FRONT = "1"
    }
}