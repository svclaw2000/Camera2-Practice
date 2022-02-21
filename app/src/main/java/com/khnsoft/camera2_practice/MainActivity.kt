package com.khnsoft.camera2_practice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.khnsoft.camera2_practice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setListeners()
            } else {
                showNoPermissionToast()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListenersWithoutPermission()
        startRequestPermission()
    }

    private fun setListenersWithoutPermission() {
        binding.textViewCamera2.setOnClickListener {
            showNoPermissionToast()
            startRequestPermission()
        }
        binding.textViewCameraX.setOnClickListener {
            showNoPermissionToast()
            startRequestPermission()
        }
    }

    private fun showNoPermissionToast() {
        Toast.makeText(
            this,
            "카메라 권한을 승인해주셔야 어플을 사용할 수 있습니다.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun startRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setListeners()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setListeners() {
        binding.textViewCamera2.setOnClickListener {
            startActivity(Intent(this, Camera2Activity::class.java))
        }
        binding.textViewCameraX.setOnClickListener {
            startActivity(Intent(this, CameraXActivity::class.java))
        }
    }
}