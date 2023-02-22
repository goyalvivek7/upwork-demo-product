package com.example.products.activity
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.products.R
import com.example.products.adapter.ImageProductAdapter
import com.example.products.baseActivity.BaseActivity
import com.example.products.model.ProductAdd_DataModel
import com.example.products.utils.Utils
import com.example.products.viewmodel.AddProduct_ViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Add_Product_Activity : BaseActivity(),ImageProductAdapter.EventListener{
    private val CHOOSE_FILE_REQUEST_CODE = 101
    private lateinit var imageList: ArrayList<String>
    private lateinit var imageListUri: ArrayList<Uri>
    var old_image:String=""
    private lateinit var addProduct_ViewModel: AddProduct_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_product)
        addProduct_ViewModel= AddProduct_ViewModel()
    viewSetup()
    findViewById<ImageView>(R.id.toolbar_main_icon).setOnClickListener {
        onBackPressed()
    }
}
 private fun viewSetup() {
     imageList = ArrayList()
     imageListUri = ArrayList()
     findViewById<RecyclerView>(R.id.recycler_view).layoutManager =
         LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
     findViewById<RecyclerView>(R.id.recycler_view).isNestedScrollingEnabled = false
     findViewById<RecyclerView>(R.id.recycler_view).setHasFixedSize(true)
     findViewById<RecyclerView>(R.id.recycler_view).adapter =
         ImageProductAdapter(this, imageListUri, findViewById<ImageView>(R.id.iv_big_image_display), false,this)
     findViewById<ImageView>(R.id.iv_get_image_file).setOnClickListener {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if  (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else if  (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.CAMERA);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                   // getFile()
                    showFileChooser()
                }
            }
            else{
                //system OS is < Marshmallow
               // getFile()
                showFileChooser(

                )
            }*/

         if (ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.READ_EXTERNAL_STORAGE
             ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE
             ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.CAMERA
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                 ActivityCompat.requestPermissions(
                     this,
                     arrayOf(
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                         Manifest.permission.WRITE_EXTERNAL_STORAGE,
                         Manifest.permission.CAMERA
                     ),
                     109
                 )
             } else {
                 ActivityCompat.requestPermissions(
                     this,
                     arrayOf(
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                         Manifest.permission.WRITE_EXTERNAL_STORAGE,
                         Manifest.permission.CAMERA
                     ),
                     109
                 )
             }
             return@setOnClickListener
         } else {
             showFileChooser()
         }
     }

     findViewById<EditText>(R.id.et_product_title).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if(charSequence.toString().isEmpty()){
                }
                else{
                }
            }
            override fun afterTextChanged(editable: Editable) {
            }
        })

     findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
         if (Utils.getInstance().isNetworkConnected(applicationContext)) {
             val hashMap = HashMap<String, String>()
             hashMap["title"] =findViewById<EditText>(R.id.et_product_title).toString()
             hashMap["description"] = findViewById<EditText>(R.id.et_desrciption).toString()
             hashMap["images"] = imageListUri.toString()
             hashMap["thumbnail"] = imageListUri[0].toString()
             hashMap["price"] =findViewById<EditText>(R.id.et_price).toString()
             hashMap["discountPercentage"] = findViewById<EditText>(R.id.et_discount_price).toString()
             hashMap["status"] = "success"
             hashMap["message"] = "Success"
             addProduct_ViewModel.getAddProductData(
                 hashMap,
                 applicationContext
             )
             send_AddProduct_Data()
         }
        }
  }

    private fun send_AddProduct_Data() {
        addProduct_ViewModel.addProductLiveData!!.observe(this, { tokenResponse ->
            try {
                val gson = Gson()
                val json = gson.toJson(tokenResponse)
                val jsonResponse = JSONObject(json)

                if (jsonResponse.has("body")) {
                    val body = jsonResponse.getJSONObject("body")
                    Log.d("support", body.toString())
                    val response = gson.fromJson(body.toString(), ProductAdd_DataModel::class.java)
                    if (response.title.isNotEmpty()) {
                        Toast.makeText(this, "successfully add", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun getFiles(uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(this, uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return null
    }
    fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
    private fun createRequestBody(s: String?): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), s!!)
    }
    private fun showFileChooser() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload documents!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                if  (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) ==
                    PackageManager.PERMISSION_DENIED) {
                    requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        200)
                } else {
                    /*val cameraIntent = Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                    )
                    resultLauncher.launch(cameraIntent)*/

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, 101)

                }
            } else if (options[item] == "Choose from Gallery") {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        200
                    )
                } else {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    resultLauncher.launch(pickPhoto)
                }
            }
        }
        builder.show()
    }

    private fun getFile() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(pickPhoto)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent = result.data!!
            findViewById<ImageView>(R.id.iv_big_image_display).setImageURI(data.data)
            findViewById<ImageView>(R.id.iv_small_image_display).setImageURI(data.data)
            try {
                imageList.add(data.data!!.toString())
            }catch (e:Exception){
            }
            //imageListUri.add(data.data!!)
            if (data.clipData != null) {
                val mClipData = data.clipData
                val cout = data.clipData!!.itemCount
                for (i in 0 until cout) {
                    // adding imageuri in array
                    val imageurl = data.clipData!!.getItemAt(i).uri
                    imageListUri.add(imageurl)
                    Log.e("listdata", imageListUri.size.toString())

                    findViewById<RecyclerView>(R.id.recycler_view).adapter =
                        ImageProductAdapter(
                            this,
                            imageListUri,
                            findViewById<ImageView>(R.id.iv_big_image_display),
                            true,
                            this
                        )
                }
            }
            else{
                try {
                    val imageurl = data.data!!
                    imageListUri.add(imageurl)
                    Log.e("listdata", imageListUri.size.toString())

                    if(imageListUri.size<=5){
                    findViewById<RecyclerView>(R.id.recycler_view).adapter =
                        ImageProductAdapter(
                            this,
                            imageListUri,
                            findViewById<ImageView>(R.id.iv_big_image_display),
                            true,
                            this
                        )
                    }
                    else{
                        Toast.makeText(applicationContext, "Not More then 5", Toast.LENGTH_LONG).show()

                    }


                }catch (e:Exception){}
            }
        }
    }

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_FILE_REQUEST_CODE -> {
                    // for the set image resourse
                    findViewById<ImageView>(R.id.iv_big_image_display).setImageURI(data?.data)
                    findViewById<ImageView>(R.id.iv_small_image_display).setImageURI(data?.data) // handle chosen image
                    imageList.add(data?.data!!.toString())
                    findViewById<RecyclerView>(R.id.recycler_view).adapter =
                        ImageProductAdapter(this, imageListUri, findViewById<ImageView>(R.id.iv_big_image_display), true,this)
                }
            }
        }
    }*/

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                val bitmap = data?.extras?.get("data") as Bitmap
                findViewById<ImageView>(R.id.iv_big_image_display).setImageBitmap(bitmap)
                findViewById<ImageView>(R.id.iv_small_image_display).setImageBitmap(bitmap)
                try {
                    imageList.add(bitmap.toString())
                }catch (e:Exception){
                }

                val file: File? = createImageFile(applicationContext)
                if (file != null) {
                    val fout: FileOutputStream
                    try {
                        fout = FileOutputStream(file)
                        //currentImage.compress(Bitmap.CompressFormat.PNG, 70, fout)
                        fout.flush()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    val uri = Uri.fromFile(file)


                    val imageurl = getImageUri(applicationContext,bitmap)!!
                    imageListUri.add(imageurl)
                    Log.e("listdata", imageListUri.size.toString())

                    if(imageListUri.size<=5){
                        findViewById<RecyclerView>(R.id.recycler_view).adapter =
                            ImageProductAdapter(
                                this,
                                imageListUri,
                                findViewById<ImageView>(R.id.iv_big_image_display),
                                true,
                                this
                            )
                    }
                }

            }

        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun createImageFile(applicationContext: Context): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        var mFileTemp: File? = null
        val root: String = applicationContext.getDir("my_sub_dir", MODE_PRIVATE).getAbsolutePath()
        val myDir = File("$root/Img")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        try {
            mFileTemp = File.createTempFile(imageFileName, ".jpg", myDir.absoluteFile)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return mFileTemp
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    /*override fun onEvent(data: String, position: Int) {
        TODO("Not yet implemented")
    }*/

    override fun onEvent(position: String,positionact: kotlin.Int) {
        if(old_image.isEmpty() ||  old_image == "[]"){
            imageListUri.removeAt(positionact)
            if(imageListUri.size!=0){
                Glide.with(this)
                    .load(Uri.parse(imageListUri[0].toString()))
                    .apply(
                        RequestOptions().placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image))
                    .into(findViewById<ImageView>(R.id.iv_big_image_display))
            }
            else{
                Glide.with(this)
                    .load(R.drawable.no_image)
                    .apply(
                        RequestOptions().placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image))
                    .into(findViewById<ImageView>(R.id.iv_big_image_display))
            }
            findViewById<RecyclerView>(R.id.recycler_view).adapter =
                ImageProductAdapter(
                    this,
                    imageListUri,
                    findViewById<ImageView>(R.id.iv_big_image_display),
                    true,
                    this
                )
        }
        else { }
    }
}