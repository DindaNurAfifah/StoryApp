package com.dicoding.storyapplast.ui.home.upload

import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapplast.R
import com.dicoding.storyapplast.databinding.ActivityUpStoriesBinding
import com.dicoding.storyapplast.ui.ViewModelFactory
import com.dicoding.storyapplast.ui.home.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UpStoriesActivity : AppCompatActivity() {

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
    private lateinit var binding: ActivityUpStoriesBinding
    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permission ->
            when {
                permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    myLastLocation()
                }
                permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    myLastLocation()
                }
                else -> {
                    binding.switchlocation.isChecked = false
                }
            }
        }

    private val requestPermissionLauncherCamera =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncherCamera.launch(REQUIRED_PERMISSION)
        }

        binding.btngallery.setOnClickListener {
            startGallery()
        }
        binding.btncamera.setOnClickListener {
            startCamera()
        }
        binding.switchlocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this@UpStoriesActivity, "Location enabled", Toast.LENGTH_SHORT).show()
                if (!isLocationEnabled()) {
                    Toast.makeText(this@UpStoriesActivity, "Location enabled", Toast.LENGTH_SHORT).show()
                }
                lifecycleScope.launch {
                    myLastLocation()
                }
            } else {
                Toast.makeText(this@UpStoriesActivity, "Location disabled", Toast.LENGTH_SHORT).show()
                currentLocation = null
            }

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        binding.btnupload.setOnClickListener {
            if(binding.updesc.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill the description", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    showLoading(true)
                    uploadImage()
                    startActivity(Intent(HomeActivity.getLaunchService(this@UpStoriesActivity)))
                }
            }
        }

    }

    suspend fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            val description = binding.updesc.text.toString()
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestBodyGet = description.toRequestBody("text/plain".toMediaType()).toString()
            Log.e("requestBody:", requestBodyGet)

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)
            val multipartBodyGet = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile).toString()
            Log.e("requestBody:", multipartBodyGet)

            var lat: RequestBody? = null
            var lon: RequestBody? = null
            if (currentLocation != null) {
                lat = currentLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
                lon = currentLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())
            } else {
                lat = null
                lon = null
            }

            Log.e("lat", lat.toString())
            Log.e("lon", lon.toString())
            Log.e("Location", currentLocation.toString())
            uploadViewModel.getToken().observe(this) { shared ->
                showLoading(false)
                val token = shared.token
                Log.e("token", token)

                CoroutineScope(Dispatchers.Main).launch {
                    uploadViewModel.setStories(token, multipartBody, requestBody, lat, lon)
                    showToast(getString(R.string.upload_success))
                }
            }
        }?: showToast(getString(R.string.empty_image_warning))
        showLoading(false)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.upstoriesimg.setImageURI(it)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkPermission(accessFineLocation: String): Boolean {
        return  ContextCompat.checkSelfPermission(
            this, accessFineLocation
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("MissingPermission")
    private fun myLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)&&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null){
                    currentLocation = location
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}