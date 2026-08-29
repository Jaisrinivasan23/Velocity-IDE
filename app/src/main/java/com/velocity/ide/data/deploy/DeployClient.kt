package com.velocity.ide.data.deploy

import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class DeployResult(
    val success: Boolean,
    val message: String,
    val url: String? = null
)

/**
 * Real deployment clients for Vercel and Netlify using their HTTP APIs.
 * Results reflect the actual HTTP response; failures are surfaced verbatim.
 */
class DeployClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .build()
) {

    fun vercelDeploy(projectDir: File, token: String): DeployResult {
        val dist = File(projectDir, "dist")
        if (!dist.isDirectory) return DeployResult(false, "dist/ not found. Run a build first.")
        return try {
            val files = dist.walkTopDown().filter { it.isFile }.toList()
            if (files.isEmpty()) return DeployResult(false, "dist/ is empty. Run a build first.")

            val uploadBody = JSONObject().put(
                "files",
                files.map { f ->
                    JSONObject()
                        .put("file", f.relativeTo(dist).path.replace('\\', '/'))
                        .put("data", sha256Hex(f.readBytes()))
                }
            )
            val uploadReq = Request.Builder()
                .url("https://api.vercel.com/v13/upload?version=2")
                .header("Authorization", "Bearer $token")
                .post(uploadBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            client.newCall(uploadReq).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return DeployResult(false, "Vercel upload failed (${resp.code}): ${resp.body?.string()?.take(300)}")
                }
                val job = JSONObject(resp.body!!.string()).getString("job")

                // Upload each file's content at its deployment path
                files.forEach { f ->
                    val rel = f.relativeTo(dist).path.replace('\\', '/')
                    val data = Base64.encodeToString(f.readBytes(), Base64.NO_WRAP)
                    val putReq = Request.Builder()
                        .url("https://api.vercel.com/v2/upload/$job/$rel")
                        .header("Authorization", "Bearer $token")
                        .put(data.toRequestBody("application/octet-stream".toMediaType()))
                        .build()
                    client.newCall(putReq).execute().use { pr ->
                        if (!pr.isSuccessful) {
                            return DeployResult(false, "Upload of $rel failed (${pr.code}).")
                        }
                    }
                }

                val createBody = JSONObject().put("name", "velocity-ide-deploy").put("job", job).toString()
                val createReq = Request.Builder()
                    .url("https://api.vercel.com/v13/deployments")
                    .header("Authorization", "Bearer $token")
                    .post(createBody.toRequestBody("application/json".toMediaType()))
                    .build()
                client.newCall(createReq).execute().use { cr ->
                    if (!cr.isSuccessful) {
                        return DeployResult(false, "Vercel deployment failed (${cr.code}): ${cr.body?.string()?.take(300)}")
                    }
                    val created = JSONObject(cr.body!!.string())
                    return DeployResult(true, "Deployed to Vercel.", created.optString("url").ifBlank { null })
                }
            }
        } catch (e: Exception) {
            DeployResult(false, "Vercel error: ${e.message}")
        }
    }

    fun netlifyDeploy(projectDir: File, token: String): DeployResult {
        val dist = File(projectDir, "dist")
        if (!dist.isDirectory) return DeployResult(false, "dist/ not found. Run a build first.")
        return try {
            val zipBytes = zipDirectory(dist)
            val req = Request.Builder()
                .url("https://api.netlify.com/api/v1/sites")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/zip")
                .post(zipBytes.toRequestBody("application/zip".toMediaType()))
                .build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return DeployResult(false, "Netlify upload failed (${resp.code}): ${resp.body?.string()?.take(300)}")
                }
                val body = JSONObject(resp.body!!.string())
                val subdomain = body.optString("subdomain", "")
                return DeployResult(
                    true,
                    "Deployed to Netlify.",
                    if (subdomain.isNotBlank()) "https://$subdomain.netlify.app" else null
                )
            }
        } catch (e: Exception) {
            DeployResult(false, "Netlify error: ${e.message}")
        }
    }

    private fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun zipDirectory(dir: File): ByteArray {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zip ->
            dir.walkTopDown().filter { it.isFile }.forEach { f ->
                val rel = f.relativeTo(dir).path.replace('\\', '/')
                zip.putNextEntry(ZipEntry(rel))
                zip.write(f.readBytes())
                zip.closeEntry()
            }
        }
        return baos.toByteArray()
    }
}