package br.com.fiap.foodtech.foodtech.service;

import br.com.fiap.foodtech.foodtech.dto.EnderecoDTO;
import br.com.fiap.foodtech.foodtech.dto.RestauranteDTO;
import br.com.fiap.foodtech.foodtech.entities.Gestor;
import br.com.fiap.foodtech.foodtech.repositories.GestorRepository;
import br.com.fiap.foodtech.foodtech.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RestauranteServiceIntegrationTest {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private GestorRepository gestorRepository;

    private Long gestorId;

    @BeforeEach
    void setup() {
        var gestor = new Gestor();
        gestor.setNome("Amanda Ribeiro");
        gestor = gestorRepository.save(gestor);
        gestorId = gestor.getId();
    }

    @Test
    @DisplayName("Deve salvar restaurante válido e retornar DTO com ID não nulo")
    void deveSalvarRestauranteComSucesso() {
        var dto = new RestauranteDTO(
                null,
                "Sushi da Vila",
                "Japonesa",
                LocalTime.of(18, 0),
                LocalTime.of(23, 0),
                gestorId,
                new EnderecoDTO("Rua Japão", "123", "Liberdade", "São Paulo", "SP", "01503-001")
        );

        var salvo = restauranteService.saveRestaurante(dto);
        assertNotNull(salvo.id());
        assertEquals("Sushi da Vila", salvo.nome());
    }

    @Test
    @DisplayName("Deve buscar restaurante salvo anteriormente pelo ID")
    void deveBuscarRestaurantePorId() {
        var dto = new RestauranteDTO(
                null,
                "Cantina Itália",
                "Italiana",
                LocalTime.of(11, 0),
                LocalTime.of(22, 0),
                gestorId,
                new EnderecoDTO("Rua Roma", "45", "Centro", "Campinas", "SP", "13010-000")
        );

        var salvo = restauranteService.saveRestaurante(dto);
        var encontrado = restauranteService.findRestaurante(salvo.id());

        assertEquals("Cantina Itália", encontrado.nome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar buscar restaurante inexistente")
    void deveLancarExcecaoAoBuscarRestauranteInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> restauranteService.findRestaurante(999L));
    }

    @Test
    @DisplayName("Deve atualizar nome e tipo de culinária de restaurante existente")
    void deveAtualizarRestaurante() {
        var dtoOriginal = new RestauranteDTO(
                null,
                "Bistrô Original",
                "Francesa",
                LocalTime.of(12, 0),
                LocalTime.of(20, 0),
                gestorId,
                new EnderecoDTO("Alameda Paris", "88", "Jardins", "São Paulo", "SP", "01423-001")
        );

        var salvo = restauranteService.saveRestaurante(dtoOriginal);

        var dtoAtualizado = new RestauranteDTO(
                salvo.id(),
                "Bistrô Atualizado",
                "Contemporânea",
                LocalTime.of(13, 0),
                LocalTime.of(21, 0),
                gestorId,
                dtoOriginal.endereco()
        );

        restauranteService.updateRestaurante(salvo.id(), dtoAtualizado);
        var atualizado = restauranteService.findRestaurante(salvo.id());

        assertEquals("Bistrô Atualizado", atualizado.nome());
        assertEquals("Contemporânea", atualizado.tipoCozinha());
    }

    @Test
    @DisplayName("Deve deletar restaurante existente")
    void deveDeletarRestaurante() {
        var dto = new RestauranteDTO(
                null,
                "Hamburgueria Central",
                "Americana",
                LocalTime.of(17, 0),
                LocalTime.of(23, 30),
                gestorId,
                new EnderecoDTO("Rua Texas", "500", "Centro", "Bauru", "SP", "17000-000")
        );

        var salvo = restauranteService.saveRestaurante(dto);
        restauranteService.deleteRestaurante(salvo.id());

        assertThrows(ResourceNotFoundException.class, () -> restauranteService.findRestaurante(salvo.id()));
    }
}
