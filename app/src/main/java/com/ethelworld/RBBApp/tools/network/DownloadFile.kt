package com.ethelworld.RBBApp.tools.network

import android.os.AsyncTask
import android.util.Log
import com.ethelworld.RBBApp.Item.UserContact
import com.fbs.app.Contact
import com.fbs.app.ContactList
import com.google.flatbuffers.FlatBufferBuilder
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DownloadFile(private val listener: OnDownloadContactListener, private val url: String?, private val token: String?, private val file: File?): AsyncTask<String, Int, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    @ExperimentalUnsignedTypes
    override fun doInBackground(vararg params: String?): String? {
        try {
            val u = URL(url)
            val connection = u.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.connect()

            val lenFile = connection.contentLength
            val input = connection.inputStream

            val buffer = ByteArray(1024)

            val path = File(file, "recovery")
            if(!path.exists()) {
                path.mkdirs()
            }

            val pathRecovery = File(path, "recovery_contact.dfb")
            val tempFile = FileOutputStream(pathRecovery)
            var len: Int
            var total: Long = 0

            while(input.read(buffer).also { len = it } > 0) {
                total += len
                publishProgress(((total*100)/lenFile).toInt(), 0)
                tempFile.write(buffer, 0, len)
            }

            tempFile.close()

            val finput = FileInputStream(pathRecovery)
            val zis = ZipInputStream(finput)
            var ze: ZipEntry

            publishProgress(100, 1)

            val fbb = FlatBufferBuilder()
            var contactList: ContactList = ContactList()
            while (zis.nextEntry.also { ze = it } != null) {
//                Log.i("CONTACT", "size = ${ze.size.toInt()}")
                val bufferRec = ByteArray(ze.size.toInt())
                while (zis.read(bufferRec) != -1);

//                Log.i("CONTACT", String(bufferRec))
                val bfb = ByteBuffer.wrap(bufferRec)
//                Log.i("CONTACT", bfb.toString())

                contactList = ContactList.getRootAsContactList(bfb, contactList)
//                Log.i("CONTACT", "len = ${contactList.listLength}")
                for (i in 0 until contactList.listLength) {
                    val contact: Contact? = contactList.list(i)
                    if (contact != null) {
//                        Log.i("CONTACT", "id = ${contact.id}")
                        listener.addContact(UserContact(
                            contact.id,
                            contact.name,
                            contact.occupation,
                            contact.company,
                            contact.province,
                            contact.city,
                            contact.number
                        ))
                    }
                }
                contactList.__reset()
                publishProgress(100, 2)
                zis.closeEntry()
            }
            zis.close()
        } catch (e: Exception) {
//            Log.e("ERRORCONTACT", e.message)
            e.printStackTrace()
            publishProgress(100, 2)
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        listener.updateProgress(values[0], values[1])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        listener.onSuccess(result)
    }
}