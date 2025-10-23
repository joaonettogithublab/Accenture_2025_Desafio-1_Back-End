// src/test/java/com/desafio/runner/RunCucumberTest.java
package com.desafio.runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features", // Local dos arquivos .feature
        glue = "com.desafio.api.steps", // Local das Step Definitions
        tags = "@Desafio", // Executa a tag @Desafio
        plugin = {
                "pretty", // Imprime a saída no console de forma mais legível
                "html:target/cucumber-reports/cucumber-html-report.html", // Relatório HTML do Cucumber
                "json:target/cucumber-reports/cucumber.json" // JSON para outros plugins
        },
        monochrome = true, // Saída do console limpa
        dryRun = false // false para execução real, true para verificar steps faltantes
)
public class RunCucumberTest {
    // Esta classe usa o @RunWith(Cucumber.class) do JUnit para executar o BDD.
}