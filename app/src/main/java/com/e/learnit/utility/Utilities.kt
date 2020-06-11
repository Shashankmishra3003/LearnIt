package com.e.learnit.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.view.Gravity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.e.learnit.R
import com.e.learnit.ui.MainActivity
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog

class Utilities {

     fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                //It will check for both wifi and cellular network
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
            return false
        } else {
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }

    fun showNoDataDialog(context: Context)
    {
        MaterialStyledDialog.Builder(context)
            .setTitle("No Notes!!")
            .setDescription("Please Add Notes.")
            .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
            .setPositiveText("Ok")
            .onPositive{dialog, which ->
                context.startActivity(Intent(context,MainActivity::class.java))
            }
            .show()
    }

    fun showMaterialDialogNoInternet(context: Context)
    {
        MaterialStyledDialog.Builder(context)
            .setTitle("No Internet ?")
            .setDescription("Please Check your Internet connection and Try Again.")
            .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
            .setPositiveText("Ok")
            .onPositive{dialog, which ->
                finishAffinity(context as Activity)
            }
            .show()
    }
}