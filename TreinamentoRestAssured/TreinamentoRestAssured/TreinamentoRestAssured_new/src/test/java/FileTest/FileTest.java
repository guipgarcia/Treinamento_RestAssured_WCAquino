package FileTest;

import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FileTest {

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me/";
    }

    @Test
    public void deveObrigarEnvio(){
        given()
                .log().all()
        .when()
                .post("upload")
        .then()
                .log().all()
                .statusCode(404)// deveria ser 400, mas a api não foi configurada corretamente
                .body("error", is("Arquivo não enviado"))
        ;
    }

    @Test
    public void deveFazerUploadDoArquivo(){
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/users.pdf"))
        .when()
                .post("upload")
        .then()
                .log().all()
                .statusCode(200)// deveria ser 400, mas a api não foi configurada corretamente
                .body("name", is("users.pdf"))
        ;
    }

    @Test
    public void deveFazerUploadDoArquivoNoTempoDeterminado(){
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/users.pdf"))
        .when()
                .post("upload")
        .then()
                .log().all()
                .time(greaterThan(1000L))
                .time(lessThan(3000L))
                .statusCode(200)// deveria ser 400, mas a api não foi configurada corretamente
                .body("name", is("users.pdf"))
        ;
    }

    @Test
    public void deveFazerDownload() throws IOException {
        byte[] image =
                given()
                    .log().all()
                .when()
                    .get("download")
                .then()
//                    .log().all()
                    .statusCode(200)// deveria ser 400, mas a api não foi configurada corretamente
                    .extract().asByteArray();

        File imagem = new File("src/main/resources/imagem.jpg");
        OutputStream outputStream = new FileOutputStream(imagem);
        outputStream.write(image);
        outputStream.close();

        System.out.println(imagem.length());
        assertThat(imagem.length(), lessThan(100000L));
    }
}
