package br.com.fiap.foodtech.foodtech.service;

import br.com.fiap.foodtech.foodtech.dto.EnderecoDTO;
import br.com.fiap.foodtech.foodtech.dto.RestauranteDTO;
import br.com.fiap.foodtech.foodtech.entities.Endereco;
import br.com.fiap.foodtech.foodtech.entities.Gestor;
import br.com.fiap.foodtech.foodtech.entities.Restaurante;
import br.com.fiap.foodtech.foodtech.repositories.GestorRepository;
import br.com.fiap.foodtech.foodtech.repositories.RestauranteRepository;
import br.com.fiap.foodtech.foodtech.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestauranteServiceTest {

    private RestauranteRepository restauranteRepository;
    private GestorRepository gestorRepository;
    private RestauranteService restauranteService;

    @BeforeEach
    void setUp() {
        restauranteRepository = mock(RestauranteRepository.class);
        gestorRepository = mock(GestorRepository.class);
        restauranteService = new RestauranteService(restauranteRepository, gestorRepository);
    }

    @AfterEach
    void tearDown() {
        restauranteRepository = null;
        gestorRepository = null;
        restauranteService = null;
    }


    @Test
    @DisplayName("Deve retornar lista paginada de restaurantes")
    void deveBuscarTodosOsRestaurantes() {
        Pageable paginacao = PageRequest.of(0, 5);
        Restaurante restaurante = new Restaurante();
        restaurante.setEndereco(new Endereco());
        restaurante.setGestor(new Gestor());

        when(restauranteRepository.findAll(paginacao)).thenReturn(new PageImpl<>(List.of(restaurante)));

        Page<RestauranteDTO> resultado = restauranteService.findAllRestaurantes(0, 5);

        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve retornar restaurante por ID existente")
    void deveBuscarRestaurantePorIdExistente() {
        Restaurante restaurante = new Restaurante();
        restaurante.setEndereco(new Endereco());
        restaurante.setGestor(new Gestor());
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        RestauranteDTO dto = restauranteService.findRestaurante(1L);

        assertNotNull(dto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar restaurante com ID inexistente")
    void deveLancarExcecaoQuandoRestauranteNaoEncontrado() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restauranteService.findRestaurante(1L));
    }

    @Test
    @DisplayName("Deve salvar novo restaurante com gestor válido")
    void deveSalvarNovoRestaurante() {
        Gestor gestor = new Gestor();
        when(gestorRepository.findById(1L)).thenReturn(Optional.of(gestor));

        RestauranteDTO dto = new RestauranteDTO(
                null,
                "Restaurante Exemplo",
                "Japonesa",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                1L,
                new EnderecoDTO("Rua A", "123", "Bairro", "Cidade", "Estado", "12345-678")
        );

        restauranteService.saveRestaurante(dto);

        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar restaurante com gestor inexistente")
    void deveLancarExcecaoAoSalvarSemGestor() {
        when(gestorRepository.findById(99L)).thenReturn(Optional.empty());

        RestauranteDTO dto = new RestauranteDTO(
                null,
                "Restaurante Exemplo",
                "Italiana",
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                99L,
                new EnderecoDTO("Rua X", "123", "Bairro", "Cidade", "Estado", "00000-000")
        );

        assertThrows(ResourceNotFoundException.class, () -> restauranteService.saveRestaurante(dto));
    }

    @Test
    @DisplayName("Deve atualizar restaurante existente")
    void deveAtualizarRestaurante() {
        Restaurante existente = new Restaurante();
        existente.setEndereco(new Endereco());
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(existente));

        RestauranteDTO dto = new RestauranteDTO(
                1L,
                "Atualizado",
                "Mexicana",
                LocalTime.of(9, 0),
                LocalTime.of(21, 0),
                1L,
                new EnderecoDTO("Rua B", "456", "Centro", "Cidade", "UF", "22222-222")
        );

        restauranteService.updateRestaurante(1L, dto);

        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar restaurante inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(restauranteRepository.findById(2L)).thenReturn(Optional.empty());

        RestauranteDTO dto = new RestauranteDTO(
                2L,
                "Qualquer",
                "Fusion",
                LocalTime.of(10, 0),
                LocalTime.of(23, 0),
                1L,
                new EnderecoDTO("Rua Y", "789", "Bairro", "Cidade", "UF", "33333-333")
        );

        assertThrows(ResourceNotFoundException.class, () -> restauranteService.updateRestaurante(2L, dto));
    }

    @Test
    @DisplayName("Deve deletar restaurante existente")
    void deveDeletarRestaurante() {
        Restaurante restaurante = new Restaurante();
        when(restauranteRepository.findById(3L)).thenReturn(Optional.of(restaurante));

        restauranteService.deleteRestaurante(3L);

        verify(restauranteRepository).delete(restaurante);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar restaurante inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(restauranteRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restauranteService.deleteRestaurante(4L));
    }
}
