package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.data.Transaction
import com.example.data.TransactionItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptGenerator {

    fun generate(
        context: Context,
        transaction: Transaction,
        items: List<TransactionItem>,
        customerName: String
    ): Bitmap {
        val width = 600
        val padding = 40f
        var y = padding
        val lineHeight = 35f

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }
        val titlePaint = Paint(paint).apply {
            textSize = 40f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val height = (padding * 2 + 200 + (items.size * lineHeight * 2) + 250).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        y += 20f
        canvas.drawText("WARUNGKU", width / 2f, y, titlePaint)
        y += 60f

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Tanggal: ${dateFormat.format(Date(transaction.timestamp))}", padding, y, paint)
        y += lineHeight

        canvas.drawText("Pembeli: $customerName", padding, y, paint)
        y += lineHeight

        val status = if (transaction.isDebt && !transaction.isDebtPaid) "HUTANG" else "CASH / LUNAS"
        canvas.drawText("Status : $status", padding, y, paint)
        y += lineHeight * 2

        canvas.drawText("Daftar Barang:", padding, y, paint)
        y += lineHeight

        for (item in items) {
            canvas.drawText("- ${item.itemName}", padding, y, paint)
            y += lineHeight
            canvas.drawText("  ${item.qty}x @ Rp${item.sellPrice.toLong()} = Rp${(item.qty * item.sellPrice).toLong()}", padding, y, paint)
            y += lineHeight
        }

        y += lineHeight
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        canvas.drawText("Total   : Rp${transaction.total.toLong()}", padding, y, paint)
        y += lineHeight
        
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        if (!transaction.isDebt) {
            canvas.drawText("Tunai   : Rp${transaction.cash.toLong()}", padding, y, paint)
            y += lineHeight
            canvas.drawText("Kembali : Rp${transaction.change.toLong()}", padding, y, paint)
        } else if (transaction.isDebtPaid) {
            canvas.drawText("Tunai   : Rp${transaction.total.toLong()}", padding, y, paint)
        } else {
            canvas.drawText("Hutang  : Rp${transaction.total.toLong()}", padding, y, paint)
        }

        return bitmap
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Warungku")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }
        }
    }
}
