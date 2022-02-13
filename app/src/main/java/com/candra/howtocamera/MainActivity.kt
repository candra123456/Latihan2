package com.candra.howtocamera

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import coil.load
import com.candra.howtocamera.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        const val REQUEST_CODE = 1
        const val REQUEST_CODE_STORAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            camera.setOnClickListener {
                showPermission()
            }
            storageGallery.setOnClickListener {
                showPermissionStorage()
            }
        }

        supportActionBar?.title = "Candra Julius Sinaga"
        supportActionBar?.subtitle = "Membuat Camera dan Storage"
    }


    private fun showPermission(){
        Dexter.withContext(this)
            .withPermission(
                android.Manifest.permission.CAMERA
            ).withListener(object: PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                   Toast.makeText(this@MainActivity,"Permission diizinkan",Toast.LENGTH_SHORT).show()
                    setCameraAction()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                   showDialogDenied()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showDialogDenied()
                }

            }).onSameThread().check()
    }

    private fun showPermissionStorage(){
        Dexter.withContext(this)
            .withPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object: PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    storageGallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                   showDialogDenied()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showDialogDenied()
                }

            }).onSameThread().check()
    }



    private fun setCameraAction(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun showDialogDenied(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Warning")
            .setMessage("Aplikasi ini membutuhkan izin, Silahakn cek Setting anda untuk membiarkan izin")
            .setPositiveButton("Pergi Ke Setting"){_,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun storageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_STORAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_CODE){
                val bitmap = data?.extras?.get("data") as Bitmap
                binding.gambar.load(bitmap){
                    crossfade(true)
                    crossfade(1000)
                }
            }else if (requestCode == REQUEST_CODE_STORAGE){
                binding.gambar.load(data?.data){
                    crossfade(true)
                    crossfade(1000)
                }
            }
        }
    }

}