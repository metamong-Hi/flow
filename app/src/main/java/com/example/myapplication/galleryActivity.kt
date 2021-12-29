package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TabHost
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class galleryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "galleryActivity"
    }

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraAnimationListener: Animation.AnimationListener

    private var savedUri: Uri? = null


    lateinit var previewView : androidx.camera.view.PreviewView
    lateinit var imageViewPhoto : ImageView
    lateinit var imageViewPreview : ImageView
    lateinit var frameLayoutPreview : FrameLayout
    lateinit var frameLayoutShutter : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val tabHost: TabHost = findViewById<TabHost>(R.id.tabHost1)
        tabHost.setup()

        val tab1 = tabHost.newTabSpec("Tab 1")  // 태그(Tag)는 탭 버튼을 식별할 때 사용되는 값
        tab1.setIndicator("앨범")    // 탭에 표시될 문자열 지정.
        tab1.setContent(R.id.tab1)  //  매핑할 컨텐츠(Layout) 지정.
        tabHost.addTab(tab1)    // TabHost에 탭 추가

        val tab2 = tabHost.newTabSpec("Tab 2")
        tab2.setIndicator("쵤영")
        tab2.setContent(R.id.tab2)
        tabHost.addTab(tab2)

        //최초 선택 탭 지정
        tabHost.currentTab = 1

        findView()
        permissionCheck()
        setListener()
        setCameraAnimationListener()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }
    private fun findView() {
        previewView = findViewById(R.id.previewView)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)
        imageViewPreview = findViewById(R.id.imageViewPreview)
        frameLayoutPreview = findViewById(R.id.frameLayoutPreview)
        frameLayoutShutter = findViewById(R.id.frameLayoutShutter)
    }
    private fun permissionCheck() {

        var permissionList =
            listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!PermissionUtil.checkPermission(this, permissionList)) {
            PermissionUtil.requestPermission(this, permissionList)
        } else {
            openCamera()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            onBackPressed()
        }
    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun openCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                Log.d("TAG", "바인딩 성공")

            } catch (e: Exception) {
                Log.d("TAG", "바인딩 실패 $e")
            }
        }, ContextCompat.getMainExecutor(this))

    }
    private fun setListener() {
        findViewById<ImageView>(R.id.imageViewPhoto).setOnClickListener {
            savePhoto()
        }
        findViewById<ImageView>(R.id.closePic).setOnClickListener {
            closePicture()
        }

    }
    private fun setCameraAnimationListener() {
        cameraAnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                frameLayoutShutter.visibility = View.GONE
                showCaptureImage()
            }
            override fun onAnimationRepeat(animation: Animation?) { }

        }
    }


    private fun savePhoto() {
        imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yy-mm-dd", Locale.US).format(System.currentTimeMillis()) + ".png"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)

                    val animation = AnimationUtils.loadAnimation(this@galleryActivity, R.anim.camera_shutter)
                    animation.setAnimationListener(cameraAnimationListener)
                    frameLayoutShutter.animation = animation
                    frameLayoutShutter.visibility = View.VISIBLE
                    frameLayoutShutter.startAnimation(animation)

                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onBackPressed()
                }

            })

    }

    private fun showCaptureImage(): Boolean {
        if (frameLayoutPreview.visibility == View.GONE) {
            frameLayoutPreview.visibility = View.VISIBLE
            imageViewPreview.setImageURI(savedUri)
            return false
        }

        return true

    }

    private fun hideCaptureImage() {
        imageViewPreview.setImageURI(null)
        frameLayoutPreview.visibility = View.GONE

    }

    override fun onBackPressed() {
        if (showCaptureImage()) {
            hideCaptureImage()
        } else {
            onBackPressed()

        }
    }
    private fun closePicture()
    {
        hideCaptureImage()
    }
}