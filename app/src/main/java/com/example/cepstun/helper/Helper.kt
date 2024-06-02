package com.example.cepstun.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.example.cepstun.R
import com.example.cepstun.data.remote.retrofit.ApiConfigCurrency
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// for currency format
fun String.withNumberingFormat(): String {
    return NumberFormat.getNumberInstance().format(this.toDouble())
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}

suspend fun String.withCurrencyFormat(): String {
    val rupiahExchangeRate = getRupiahExchangeRate()
    var priceOnDollar = this.toDouble() / rupiahExchangeRate

    var mCurrencyFormat = NumberFormat.getCurrencyInstance()
    val deviceLocale = Locale.getDefault().country
    when {
        deviceLocale.equals("ID") -> {
            priceOnDollar *= rupiahExchangeRate
        }
        else -> {
            mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        }
    }
    return mCurrencyFormat.format(priceOnDollar)
}

suspend fun getRupiahExchangeRate(): Float {
    val service = ApiConfigCurrency.getApiService()
    val response = service.getLatestCurrency()
    return response.usd?.idr ?: 0.0F
}

// use this to convert IDR
//lifecycleScope.launch {
//    val formattedCurrency = exampleStringPriceRupiah.withCurrencyFormat()
//    binding.TVPrice.text = formattedCurrency
//}




private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun Int.convertColorToHue(): Float {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    return hsv[0]
}



// for dp to pixel bottom sheet behavior
fun convertDpToPixel(dp: Float, context: Context): Int {
    return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}



// for get image from uri to file and reduce image
private const val MAXIMAL_SIZE = 1000000 //1 MB

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}


fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}


fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}



// to get address from latitude and longitude
fun LatLng.getStringAddress(context: Context): String {
    var fullAddress = "No Location"

    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(this.latitude, this.longitude, 1)
        if (addresses != null && addresses.size > 0) {
                val subLocality = addresses[0].subLocality
                val locality = addresses[0].locality
            fullAddress = if (subLocality != null && subLocality.isNotEmpty()) {
                ("$subLocality, $locality")
            } else {
                context.getString(R.string.remote_place)
            }
        }
    } catch (e: Exception) {
        fullAddress = context.getString(R.string.error_location_MenuHome, e.printStackTrace())
    }

    return fullAddress
}