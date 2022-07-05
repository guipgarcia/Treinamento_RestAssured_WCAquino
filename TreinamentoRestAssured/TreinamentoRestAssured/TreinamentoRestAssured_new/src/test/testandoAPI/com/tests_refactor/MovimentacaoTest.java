package com.tests_refactor;

import com.core.BaseTest;
import com.core.Movimentacao;
import com.utils.DataUtils;
import org.junit.Test;
import static com.utils.Generics.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {
    private static final String CONTA_MOVIMENTACOES = "Conta para movimentacoes";

    @Test
    public void deveInserirMovimentacaoValida(){
        given()
                .body(inserirMovimentacaoValida(CONTA_MOVIMENTACOES))
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .body("descricao",is("Descricao movimentacao Futura"))
                .body("tipo", is("REC"))
        ;
    }

    @Test
    public void deveValidarCamposObrigatoriosNaMovimentacao(){
        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    public void naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao movimentacao = inserirMovimentacaoValida(CONTA_MOVIMENTACOES);
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDeDias(10));
        given()
                .body(movimentacao)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao(){
        given()
                .pathParam("id", getIdContaPeloNome(CONTA_MOVIMENTACOES))
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
        ;
    }

    @Test
    public void deveRemoverMovimentacao(){
        given()
                .pathParam("id", getIdMovimentacaoPelaDescricao("Movimentacao para exclusao"))
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;
    }

}
