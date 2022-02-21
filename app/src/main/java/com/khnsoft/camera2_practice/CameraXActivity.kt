package com.khnsoft.camera2_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.khnsoft.camera2_practice.databinding.ActivityCameraXBinding

class CameraXActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraXBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}