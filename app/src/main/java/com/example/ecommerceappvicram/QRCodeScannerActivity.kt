package com.example.ecommerceappvicram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerceappvicram.api.RetrofitInstance
import com.example.ecommerceappvicram.model.Product
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QRCodeScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_scanner)

        val btnStartScan = findViewById<Button>(R.id.btnStartScan)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnStartScan.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scannez un QR Code d'article")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setOrientationLocked(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.setCaptureActivity(ScannerWithCancelActivity::class.java)
            integrator.initiateScan()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val productId = result.contents
                Toast.makeText(this, "ID scanné : $productId", Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val product: Product = RetrofitInstance.api.getProductById(productId.toInt())
                        val intent = Intent(this@QRCodeScannerActivity, ProductDetailActivity::class.java)
                        intent.putExtra("productId", product.id)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@QRCodeScannerActivity, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("QRScan", "Erreur : ", e)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
