package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
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
import android.widget.AdapterView.OnItemClickListener
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException




class galleryActivity : AppCompatActivity() {

    private val REQUEST_WRITE = 43
    private var pfd: ParcelFileDescriptor? = null
    private var fileOutputStream: FileOutputStream? = null

    companion object {
        private const val TAG = "galleryActivity"
    }
    private lateinit var currentPhotoPath: String

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraAnimationListener: Animation.AnimationListener

    private var savedUri: Uri? = null

    lateinit var previewView : androidx.camera.view.PreviewView
    lateinit var imageViewPhoto : ImageView
    lateinit var imageViewPreview : ImageView
    lateinit var frameLayoutPreview : FrameLayout
    lateinit var frameLayoutShutter : FrameLayout

    lateinit var gridView : GridView
    lateinit var pictureAdapter : PictureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val tabHost: TabHost = findViewById<TabHost>(R.id.tabHost1)
        tabHost.setup()

        val tab1 = tabHost.newTabSpec("Tab 1")  // ??????(Tag)??? ??? ????????? ????????? ??? ???????????? ???
        tab1.setIndicator("??????")    // ?????? ????????? ????????? ??????.
        tab1.setContent(R.id.tab1)  //  ????????? ?????????(Layout) ??????.
        tabHost.addTab(tab1)    // TabHost??? ??? ??????

        val tab2 = tabHost.newTabSpec("Tab 2")
        tab2.setIndicator("??????")
        tab2.setContent(R.id.tab2)
        tabHost.addTab(tab2)

        //?????? ?????? ??? ??????
        tabHost.currentTab = 1

        //???,?????? ??? ????????? ??????
        findView()
        permissionCheck()
        setListener()
        setCameraAnimationListener()

        //????????? ?????? ??????
        cameraExecutor = Executors.newSingleThreadExecutor()

        //gridview ??????
        pictureAdapter = PictureAdapter()
        getAllShownImagesPath()
        gridView.setAdapter(pictureAdapter);
        gridView.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l ->
            //??? ?????? ?????? ?????????
            Log.d(TAG,i.toString())
            savedUri = pictureAdapter.getItem(i).image
            showCaptureImage()
        })
        gridView.invalidateViews()
    }

    //??? ??????
    private fun findView() {
        previewView = findViewById(R.id.viewFinder)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)
        imageViewPreview = findViewById(R.id.imageViewPreview)
        frameLayoutPreview = findViewById(R.id.frameLayoutPreview)
        frameLayoutShutter = findViewById(R.id.frameLayoutShutter)
        gridView = findViewById(R.id.gridView)
    }

    //????????? ?????? ??????
    private fun permissionCheck() {

        var permissionList =
            listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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

    //???????????? ?????? image path -> uri ???????????? ??????
    private fun getAllShownImagesPath() {
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var columnIndexID: Int
        var imageId: Long
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // ?????? ?????? Uri ????????????
                columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    imageId = cursor.getLong(columnIndexID)
                    val uriImage = Uri.withAppendedPath(uriExternal, "" + imageId)
                    pictureAdapter.addItem(PictureItem(uriImage))
                }
            }
            cursor.close()
        }
    }

    //????????? ????????? ?????????
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
                Log.d("TAG", "????????? ??????")

            } catch (e: Exception) {
                Log.d("TAG", "????????? ?????? $e")
            }
        }, ContextCompat.getMainExecutor(this))

    }

    // ?????? ?????? ????????? ???
    private fun setListener() {
        findViewById<ImageView>(R.id.imageViewPhoto).setOnClickListener {
            CameraCapture()
        }
        findViewById<ImageView>(R.id.closePic).setOnClickListener {
            closePicture()
        }

        findViewById<ImageButton>(R.id.btn_back1).setOnClickListener {
            onBackPressed()
        }

    }

    //??????????????? ????????? ??????(????????????????????? ?????? ??????)
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

    // ????????? ?????? ??????
    // ?????? ??????????????? ??????
    //saveFile(????????? ?????? ??????)??????
    private fun CameraCapture() {
        imageCapture = imageCapture ?: return
        val fileName:String = "CS496_" + System.currentTimeMillis().toString() + ".png"
        //?????? ?????? ??????
        val photoFile = File(
            cacheDir,
            fileName
        )
        Log.d(TAG,"photoFile : ${photoFile.toString()}")
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    val ei = ExifInterface(cacheDir.toString() + File.separator + fileName)
                    val rotation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED)
                    val angle = when(rotation) {
                        ExifInterface.ORIENTATION_ROTATE_90->90f
                        ExifInterface.ORIENTATION_ROTATE_180->180f
                        ExifInterface.ORIENTATION_ROTATE_270->270f
                        else ->0f
                    }
                    Log.d("????????? ?????????", rotation.toString())

                    saveFile()
                    pictureAdapter.addItem(PictureItem(savedUri))
                    gridView.invalidateViews()
                    Log.d(TAG,savedUri.toString())
                    Log.d(TAG,pictureAdapter.items.size.toString())
                    val animation = AnimationUtils.loadAnimation(this@galleryActivity, R.anim.camera_shutter)
                    animation.setAnimationListener(cameraAnimationListener)
                    frameLayoutShutter.animation = animation
                    frameLayoutShutter.visibility = View.VISIBLE
                    frameLayoutShutter.startAnimation(animation)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d(TAG,"??????")
                    exception.printStackTrace()
                    onBackPressed()
                }

            })

    }


    // ?????????(???????????????)??? ?????? -> ?????? ?????????????????? cache?????? ?????????????????? ?????? ??????
    private fun getBytes(image_uri: Uri?): ByteArray {
        val iStream = contentResolver.openInputStream(image_uri!!)
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024 // ?????? ??????
        val buffer = ByteArray(bufferSize) // ?????? ??????
        var len = 0
        // InputStream?????? ????????? ??? ?????? ????????? ????????? ????????? ??????.
        while (iStream!!.read(buffer).also { len = it } != -1) byteBuffer.write(buffer, 0, len)
        return byteBuffer.toByteArray()
    }
    private fun saveFile() {
        val values = ContentValues()
        val fileName = "cs496" + System.currentTimeMillis() + ".png"
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val contentResolver = contentResolver
        val item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try {
            val pdf = contentResolver.openFileDescriptor(item!!, "w", null)
            if (pdf == null) {
                Log.d("Woongs", "null")
            } else {
                val inputData: ByteArray = getBytes(savedUri)
                val fos = FileOutputStream(pdf.fileDescriptor)
                fos.write(inputData)
                fos.close()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(item, values, null, null)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d("Woongs", "FileNotFoundException  : " + e.localizedMessage)
        } catch (e: Exception) {
            Log.d("Woongs", "FileOutputStream = : " + e.message)
        }
    }

    // ????????????????????? ?????? ??????
    private fun showCaptureImage(): Boolean {
        if (frameLayoutPreview.visibility == View.GONE) {
            frameLayoutPreview.visibility = View.VISIBLE
            Glide.with(this).load(savedUri).into(imageViewPreview)
            return false
        }
        return true
    }
    private fun hideCaptureImage() {
        imageViewPreview.setImageURI(null)
        frameLayoutPreview.visibility = View.GONE
    }

    private fun closePicture() {
        hideCaptureImage()
    }

    //gridView ??????
    inner class PictureAdapter : BaseAdapter() {
        var items = ArrayList<PictureItem>()
        override fun getCount(): Int {
            return items.size
        }

        fun addItem(pictureItem: PictureItem) {
            items.add(pictureItem)
        }

        override fun getItem(i: Int): PictureItem {
            return items[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
            val pictureViewer = PictureViewer(getApplicationContext())
            pictureViewer.setItem(items[i].image)
            return pictureViewer
        }
    }

}