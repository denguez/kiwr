package kiwr

import spark.Spark.*
import spark.Request
import spark.Response
import spark.template.velocity.VelocityTemplateEngine

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream;

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

fun main() {
    port(8080)
    get("/qr", home)
    get("/qr/", home)
    post("/qr/encode", ::qr_generate)
}

// QR

val home: (Request, Response) -> Any = {_, _ -> 
    VelocityTemplateEngine().render(
        modelAndView(HashMap<String, Any>(), "model.vm")
    )
}

fun qr_generate(req: Request, res: Response) {
    val txt: String = req.queryParams("txt")
    val size = parse_int(req.queryParams("size"))
    res.type("image/png")
    res.header("Content-Disposition", "inline; filename=${txt}.png")
    res write (txt qr_encode size) 
}


fun parse_int(size: String?) : Int {
    return size?.let { 
        it.runCatching { toInt() }
            .map { 
                if (it < 10) 350 
                else if (it > 1000) 1000 
                else it 
            }.getOrDefault(350)
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
    return ByteArrayOutputStream().use { 
        ImageIO.write(image, "png", it)
        it.toByteArray()
    }
}

