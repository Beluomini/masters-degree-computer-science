package com.biocluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Testes de Mutação para BioClusterManager.processarClusters()
 *
 * Objetivo: matar mutantes gerados por operadores de mutação comuns.
 * Cada teste é projetado para detectar uma mutação específica no código.
 */
public class BioClusterManagerMutacaoTest {

    private final BioClusterManager manager = new BioClusterManager();

    // =========================================================================
    // Mutante 1: Trocar operador relacional dist < raio → dist <= raio
    // Se dist == raio, o original NÃO forma cluster. O mutante formaria.
    // =========================================================================
    @Test
    @DisplayName("Mutante 1 - dist == raio não deve formar cluster (mata < → <=)")
    void mutante1_distIgualRaio() {
        // dist = sqrt((5-0)^2 + 0^2) = 5.0, raio = 5.0 → dist < raio é false
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 5, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 2: Trocar operador relacional dist < raio → dist > raio
    // Se dist < raio, o original FORMA cluster. O mutante não formaria.
    // =========================================================================
    @Test
    @DisplayName("Mutante 2 - dist < raio deve formar cluster (mata < → >)")
    void mutante2_distMenorQueRaio() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
    }

    // =========================================================================
    // Mutante 3: Trocar == por != em o1.getEspecieId() == o2.getEspecieId()
    // Mesma espécie sem modoInter: original forma cluster, mutante não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 3 - mesma espécie deve formar cluster (mata == → !=)")
    void mutante3_mesmaEspecie() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 4: Trocar || por && em (especieId == || modoInter)
    // Espécie diferente com modoInter=true: original forma cluster, mutante não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 4 - espécie diferente com modoInter=true (mata || → &&)")
    void mutante4_modoInterComEspecieDiferente() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 2, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 5: Negar modoInter (modoInter → !modoInter)
    // modoInter=true com espécie diferente: original forma, mutante não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 5 - modoInter=true permite inter-espécies (mata negação de modoInter)")
    void mutante5_negarModoInter() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 2, 1, 0, 0.9, false)
        );
        // modoInter = true, espécies diferentes → deve formar cluster
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, true, 10);
        assertFalse(resultado.isEmpty());

        // modoInter = false, espécies diferentes → não deve formar cluster
        List<String> resultado2 = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado2.isEmpty());
    }

    // =========================================================================
    // Mutante 6: Trocar > por >= em o1.getSaude() > threshold
    // saude == threshold: original NÃO forma cluster, mutante formaria.
    // =========================================================================
    @Test
    @DisplayName("Mutante 6 - saúde == threshold não deve formar cluster (mata > → >=)")
    void mutante6_saudeIgualThreshold() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.5, false),
                new Observation(2, 1, 1, 0, 0.5, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 7: Trocar > por < em o1.getSaude() > threshold
    // saude > threshold: original FORMA cluster, mutante não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 7 - saúde > threshold deve formar cluster (mata > → <)")
    void mutante7_saudeMaiorQueThreshold() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
    }

    // =========================================================================
    // Mutante 8: Remover negação !(o1.isInvasora() && o2.isInvasora())
    // Ambas invasoras: original NÃO forma, mutante formaria.
    // Nenhuma invasora: original FORMA, mutante não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 8a - ambas invasoras não devem formar cluster (mata remoção de !)")
    void mutante8a_ambasInvasoras() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, true),
                new Observation(2, 1, 1, 0, 0.9, true)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Mutante 8b - nenhuma invasora deve formar cluster (mata remoção de !)")
    void mutante8b_nenhumaInvasora() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
    }

    // =========================================================================
    // Mutante 9: Trocar && por || em (o1.isInvasora() && o2.isInvasora())
    // Uma invasora e outra não: original FORMA cluster (&&=false → !(false)=true).
    // Mutante: (true || false)=true → !(true)=false → não forma.
    // =========================================================================
    @Test
    @DisplayName("Mutante 9 - uma invasora deve formar cluster (mata && → ||)")
    void mutante9_umaInvasora() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, true),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 10: Trocar >= por > em conexoes.size() >= limiteSeguranca
    // limiteSeguranca=1 com 1 par válido: original retorna 1 e para.
    // Mutante (>): size()==1 > 1 é false → continua processando.
    // =========================================================================
    @Test
    @DisplayName("Mutante 10 - limiteSeguranca=1 deve parar após 1 conexão (mata >= → >)")
    void mutante10_limiteSeguranca() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false),
                new Observation(3, 1, 2, 0, 0.7, false)
        );
        // 3 pares possíveis, mas limite = 1
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 1);
        assertEquals(1, resultado.size());
    }

    // =========================================================================
    // Mutante 11: Trocar < por <= em obs.size() < 2
    // Lista com exatamente 2 elementos: original processa normalmente.
    // Mutante (<=): obs.size()==2 <= 2 é true → retorna vazio.
    // =========================================================================
    @Test
    @DisplayName("Mutante 11 - lista com 2 elementos deve ser processada (mata < → <=)")
    void mutante11_listaCom2Elementos() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertFalse(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 12: Remover instrução conexoes.add(...)
    // Condições todas satisfeitas: original adiciona cluster, mutante não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 12 - conexão deve ser adicionada à lista (mata remoção de add)")
    void mutante12_removerAdd() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 13: Trocar i + 1 por i no loop interno (j = i → j = i + 1)
    // Se j começa em i, o1 e o2 seriam o mesmo objeto → dist=0, mesma espécie.
    // Mutante geraria cluster consigo mesmo. Original não.
    // =========================================================================
    @Test
    @DisplayName("Mutante 13 - não deve formar cluster consigo mesmo (mata j=i+1 → j=i)")
    void mutante13_loopInterno() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 100, 0, 0.9, false)
        );
        // dist=100 > raio=5 → sem cluster entre 1 e 2
        // Se mutante faz j=i, geraria "Cluster:1-1" e "Cluster:2-2"
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 14: Substituir return conexoes por return new ArrayList<>()
    // no retorno final (após os loops)
    // =========================================================================
    @Test
    @DisplayName("Mutante 14 - resultado deve conter conexões formadas (mata return vazio)")
    void mutante14_retornoVazio() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false),
                new Observation(3, 1, 2, 0, 0.7, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(3, resultado.size());
    }

    // =========================================================================
    // Mutante 15: Trocar || por && em (o1.saude > threshold || o2.saude > threshold)
    // o1.saude > threshold e o2.saude <= threshold → || dá true, && dá false.
    // =========================================================================
    @Test
    @DisplayName("Mutante 15 - pelo menos uma saúde acima do threshold basta (mata || → &&)")
    void mutante15_saudeOr() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),  // saude > 0.5
                new Observation(2, 1, 1, 0, 0.3, false)   // saude <= 0.5
        );
        // o1.saude=0.8 > 0.5 → true, o2.saude=0.3 <= 0.5 → false
        // || → true (cluster formado), && → false (mutante morre)
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 16: Apenas o2 com saúde > threshold (mata remoção da checagem de o2)
    // Se mutante remove o2.getSaude() > threshold, esse caso falha.
    // =========================================================================
    @Test
    @DisplayName("Mutante 16 - apenas o2 saudável deve formar cluster")
    void mutante16_apenasO2Saudavel() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.3, false),  // saude <= 0.5
                new Observation(2, 1, 1, 0, 0.8, false)   // saude > 0.5
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 17: Lista vazia → retorna lista vazia (mata mutação no guard clause)
    // =========================================================================
    @Test
    @DisplayName("Mutante 17 - lista vazia retorna vazio")
    void mutante17_listaVazia() {
        List<Observation> obs = new ArrayList<>();
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 18: Lista com 1 elemento → retorna lista vazia
    // Mata mutação de constante: obs.size() < 2 → obs.size() < 1
    // =========================================================================
    @Test
    @DisplayName("Mutante 18 - lista com 1 elemento retorna vazio")
    void mutante18_listaComUmElemento() {
        List<Observation> obs = Collections.singletonList(
                new Observation(1, 1, 0, 0, 0.8, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 19: Coordenadas não-zero e assimétricas
    // Mata mutação de operador matemático: - → + no cálculo de distância
    // Com (3,4) e (1,1): dist = sqrt(4+9) = sqrt(13) ≈ 3.6 < 5 → cluster
    // Se - vira +: sqrt((3+1)^2 + (4+1)^2) = sqrt(16+25) = sqrt(41) ≈ 6.4 > 5 → sem cluster
    // =========================================================================
    @Test
    @DisplayName("Mutante 19 - coordenadas assimétricas (mata - → + na distância)")
    void mutante19_coordenadasAssimetricas() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 3, 4, 0.8, false),
                new Observation(2, 1, 1, 1, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 20: limiteSeguranca = 0 → retorna vazio imediatamente
    // Mata mutação >= → > (size()==0 > 0 é false, não retornaria cedo)
    // E mata negação de condicional: >= → <
    // =========================================================================
    @Test
    @DisplayName("Mutante 20 - limiteSeguranca=0 retorna vazio")
    void mutante20_limiteZero() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 0);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 21: Apenas o2 invasora (reverso do teste 9)
    // Mata mutação que troca o1.isInvasora() por true/false
    // =========================================================================
    @Test
    @DisplayName("Mutante 21 - apenas o2 invasora deve formar cluster")
    void mutante21_apenasO2Invasora() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, true)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:1-2", resultado.get(0));
    }

    // =========================================================================
    // Mutante 22: Verifica que o loop externo processa todos os elementos
    // 3 observações com apenas o último par válido
    // Mata mutações no incremento do loop externo (i++ → i--)
    // e no limite do loop (i < obs.size() → i <= 0)
    // =========================================================================
    @Test
    @DisplayName("Mutante 22 - loop externo percorre todos os elementos")
    void mutante22_loopExternoCompleto() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 2, 100, 0, 0.9, false),  // espécie diferente e longe de 1
                new Observation(3, 2, 101, 0, 0.7, false)   // mesma espécie que 2, perto de 2
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:2-3", resultado.get(0));
    }

    // =========================================================================
    // Mutante 23: Mesma espécie com modoInter=true (redundante mas necessário)
    // Mata mutação que nega == em (especieId == especieId) quando modoInter=true
    // Com ==→!= e modoInter=true, || ainda passa. Precisamos de ==→!= com modoInter=false.
    // Aqui testamos espécie diferente com modoInter=false → sem cluster
    // =========================================================================
    @Test
    @DisplayName("Mutante 23 - espécie diferente sem modoInter não forma cluster")
    void mutante23_especieDiferenteSemModoInter() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 2, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 24: Nenhuma saúde > threshold → sem cluster
    // Mata mutação que nega > → <= nos dois lados do ||
    // =========================================================================
    @Test
    @DisplayName("Mutante 24 - ambas saúdes abaixo do threshold não forma cluster")
    void mutante24_ambasSaudesBaixas() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.3, false),
                new Observation(2, 1, 1, 0, 0.4, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 25: Coordenadas com Y não-zero
    // Mata mutação que remove o segundo Math.pow (componente Y da distância)
    // Com (0,0) e (0,6): dist = 6 > 5 → sem cluster
    // Se Y é ignorado: dist = 0 < 5 → cluster (mutante morre)
    // =========================================================================
    @Test
    @DisplayName("Mutante 25 - distância no eixo Y conta (mata remoção de pow Y)")
    void mutante25_distanciaEixoY() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 0, 6, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // Mutante 26: Verifica formato exato da string do cluster
    // Mata mutação que altera a concatenação (remove "-" ou troca ids)
    // =========================================================================
    @Test
    @DisplayName("Mutante 26 - formato exato Cluster:id1-id2")
    void mutante26_formatoExato() {
        List<Observation> obs = Arrays.asList(
                new Observation(5, 1, 0, 0, 0.8, false),
                new Observation(9, 1, 1, 0, 0.9, false)
        );
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 10);
        assertEquals(1, resultado.size());
        assertEquals("Cluster:5-9", resultado.get(0));
    }

    // =========================================================================
    // Mutante 27: limiteSeguranca exato — 2 pares possíveis com limite=2
    // Mata mutação >= → > quando size==limiteSeguranca
    // =========================================================================
    @Test
    @DisplayName("Mutante 27 - limiteSeguranca=2 para exatamente no limite")
    void mutante27_limiteExato() {
        List<Observation> obs = Arrays.asList(
                new Observation(1, 1, 0, 0, 0.8, false),
                new Observation(2, 1, 1, 0, 0.9, false),
                new Observation(3, 1, 2, 0, 0.7, false),
                new Observation(4, 1, 3, 0, 0.6, false)
        );
        // 6 pares possíveis, limite=2
        List<String> resultado = manager.processarClusters(obs, 5.0, 0.5, false, 2);
        assertEquals(2, resultado.size());
    }
}
