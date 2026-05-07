package com.biocluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Testes Estruturais — MC/DC (Modified Condition/Decision Coverage)
 *
 * Condição composta (linha 22-24):
 *   (dist < raio && (o1.getEspecieId() == o2.getEspecieId() || modoInter)) &&
 *   (o1.getSaude() > threshold || o2.getSaude() > threshold) &&
 *   !(o1.isInvasora() && o2.isInvasora())
 *
 * Condições atômicas:
 *   T1: dist < raio
 *   T2: o1.getEspecieId() == o2.getEspecieId()
 *   T3: modoInter
 *   T4: o1.getSaude() > threshold
 *   T5: o1.isInvasora()
 *   T6: o2.isInvasora()
 */
public class BioClusterManagerEstruturalTest {

    private final BioClusterManager manager = new BioClusterManager();

    // MC1 (Linha 1) — T1=T, T2=T, T3=T, T4=T, T5=F, T6=F → Decisão: T
    // Par MC/DC para T1 (com MC2), T4 (com MC6)
    @Test
    @DisplayName("MC1 - Todas as condições favoráveis → conexão formada")
    void mc1_todasCondicoesFavoraveis() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // MC2 (Linha 2) — T1=F, T2=T, T3=T, T4=T, T5=F, T6=F → Decisão: F
    // Par MC/DC para T1 (com MC1)
    @Test
    @DisplayName("MC2 - T1=F (distância fora do raio) → sem conexão")
    void mc2_distanciaForaDoRaio() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 10, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertTrue(resultado.isEmpty());
    }

    // MC3 (Linha 3) — T1=T, T2=F, T3=F, T4=T, T5=F, T6=F → Decisão: F
    // Par MC/DC para T2 (com MC4), T3 (com MC5)
    @Test
    @DisplayName("MC3 - T2=F e T3=F (espécie diferente, sem modoInter) → sem conexão")
    void mc3_especieDiferenteSemModoInter() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 2, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // MC4 (Linha 4) — T1=T, T2=T, T3=F, T4=T, T5=F, T6=F → Decisão: T
    // Par MC/DC para T2 (com MC3)
    @Test
    @DisplayName("MC4 - T2=T, T3=F (mesma espécie, sem modoInter) → conexão formada")
    void mc4_mesmaEspecieSemModoInter() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // MC5 (Linha 5) — T1=T, T2=F, T3=T, T4=T, T5=F, T6=F → Decisão: T
    // Par MC/DC para T3 (com MC3)
    @Test
    @DisplayName("MC5 - T2=F, T3=T (espécie diferente, com modoInter) → conexão formada")
    void mc5_especieDiferenteComModoInter() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 2, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // MC6 (Linha 6) — T1=T, T2=T, T3=T, T4=F, T5=F, T6=F → Decisão: F
    // Par MC/DC para T4 (com MC1)
    @Test
    @DisplayName("MC6 - T4=F (saúde abaixo do threshold) → sem conexão")
    void mc6_saudeAbaixoDoThreshold() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.3, false),
                new Observation(2, 1, 1, 0, 0.2, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertTrue(resultado.isEmpty());
    }

    // MC7 (Linha 7) — T1=T, T2=T, T3=T, T4=T, T5=T, T6=T → Decisão: F
    // Par MC/DC para T5 (com MC9), T6 (com MC8)
    @Test
    @DisplayName("MC7 - T5=T e T6=T (ambas invasoras) → sem conexão")
    void mc7_ambasInvasoras() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, true),
                new Observation(2, 1, 1, 0, 0.9, true)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertTrue(resultado.isEmpty());
    }

    // MC8 (Linha 8) — T1=T, T2=T, T3=T, T4=T, T5=T, T6=F → Decisão: T
    // Par MC/DC para T6 (com MC7)
    @Test
    @DisplayName("MC8 - T5=T, T6=F (apenas o1 invasora) → conexão formada")
    void mc8_apenasO1Invasora() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, true),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // MC9 (Linha 9) — T1=T, T2=T, T3=T, T4=T, T5=F, T6=T → Decisão: T
    // Par MC/DC para T5 (com MC7)
    @Test
    @DisplayName("MC9 - T5=F, T6=T (apenas o2 invasora) → conexão formada")
    void mc9_apenasO2Invasora() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, true)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }
}
