package kiwr

import spark.Spark.*
import spark.Request
import spark.Response

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream;

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

fun main() {
    port(8080)
    get("/qr", info)
    get("/qr/", info)
    get("/qr/:str", ::qr_generate)
    get("/qr/:str/:size", ::qr_generate)
}

// QR

val info: (Request, Response) -> String = {_, _ -> 
    "Generate QR -> /qr/www.example.com"
}

fun qr_generate(req: Request, res: Response) {
    val str = req.params("str")
    val size = parse_int(req.params("size"))
    res.type("image/png")
    res.header("Content-Disposition", "inline; filename=${str}.png")
    res write (str qr_encode size) 
}

fun parse_int(size: String?) : Int {
    return size?.let { it.runCatching { toInt() }
        .map { if (it < 1000) it else 1000 }
        .getOrDefault(350)
    } ?: 350
}

infix fun Response.write(image: ByteArray) {
    raw().getOutputStream().runCatching {
        write(image)
    }.onFailure {
        status(501)
        body("Error: ${it.message}")
    }
}

infix fun String.qr_encode(size: Int) : ByteArray {
    val code = QRCodeWriter().encode(this, BarcodeFormat.QR_CODE, size, size)
    val image = MatrixToImageWriter.toBufferedImage(code)
    return ByteArrayOutputStream().use { stream ->
        ImageIO.write(image, "png", stream)
        stream.toByteArray()
    }
}

