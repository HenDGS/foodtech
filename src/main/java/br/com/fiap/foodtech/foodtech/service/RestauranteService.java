package br.com.fiap.foodtech.foodtech.service;

import br.com.fiap.foodtech.foodtech.dto.EnderecoDTO;
import br.com.fiap.foodtech.foodtech.dto.RestauranteDTO;
import br.com.fiap.foodtech.foodtech.entities.Endereco;
import br.com.fiap.foodtech.foodtech.entities.Restaurante;
import br.com.fiap.foodtech.foodtech.repositories.GestorRepository;
import br.com.fiap.foodtech.foodtech.repositories.RestauranteRepository;
import br.com.fiap.foodtech.foodtech.service.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RestauranteService {
    private final RestauranteRepository restauranteRepository;
    private final GestorRepository gestorRepository;

    public RestauranteService(RestauranteRepository restauranteRepository, GestorRepository gestorRepository) {
        this.restauranteRepository = restauranteRepository;
        this.gestorRepository = gestorRepository;
    }

    public Page<RestauranteDTO> findAllRestaurantes(int page, int size) {
        var pageable = PageRequest.of(page, size);

        var restaurantes =  this.restauranteRepository.findAll(pageable);

        return restaurantes.map(RestauranteService::mapFromRestauranteEntity);
    }

    public RestauranteDTO findRestaurante(Long id) {
        var restaurante = this.restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado. ID: " + id));

        return mapFromRestauranteEntity(restaurante);
    }

    public RestauranteDTO saveRestaurante(RestauranteDTO restaurante) {
        var gestor = gestorRepository.findById(restaurante.idDonoRestaurante())
                .orElseThrow(() -> new ResourceNotFoundException("Gestor não encontrado. ID: " + restaurante.idDonoRestaurante()));

        var endereco = new Endereco(
                restaurante.endereco().logradouro(),
                restaurante.endereco().numero(),
                restaurante.endereco().bairro(),
                restaurante.endereco().cidade(),
                restaurante.endereco().estado(),
                restaurante.endereco().cep()
        );

        var restauranteEntity = new Restaurante(
                restaurante.nome(),
                restaurante.tipoCozinha(),
                restaurante.horarioAbertura(),
                restaurante.horarioFechamento(),
                gestor,
                endereco
        );

        this.restauranteRepository.save(restauranteEntity);

        return mapFromRestauranteEntity(restauranteEntity);
    }

    private static RestauranteDTO mapFromRestauranteEntity(Restaurante restaurante) {
        return new RestauranteDTO(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTipoCozinha(),
                restaurante.getHorarioAbertura(),
                restaurante.getHorarioFechamento(),
                restaurante.getGestor().getId(),
                new EnderecoDTO(
                        restaurante.getEndereco().getLogradouro(),
                        restaurante.getEndereco().getNumero(),
                        restaurante.getEndereco().getBairro(),
                        restaurante.getEndereco().getCidade(),
                        restaurante.getEndereco().getEstado(),
                        restaurante.getEndereco().getCep()
                )
        );
    }

    public void updateRestaurante(Long id, RestauranteDTO restaurante) {
        var restauranteEntity = this.restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado. ID: " + id));

        restauranteEntity.setNome(restaurante.nome());
        restauranteEntity.setTipoCozinha(restaurante.tipoCozinha());
        restauranteEntity.setHorarioAbertura(restaurante.horarioAbertura());
        restauranteEntity.setHorarioFechamento(restaurante.horarioFechamento());
        restauranteEntity.setDataUltimaAlteracao(LocalDateTime.now());

        restauranteEntity.setEndereco(new Endereco(
                restaurante.endereco().logradouro(),
                restaurante.endereco().numero(),
                restaurante.endereco().bairro(),
                restaurante.endereco().cidade(),
                restaurante.endereco().estado(),
                restaurante.endereco().cep()
        ));

        this.restauranteRepository.save(restauranteEntity);
    }

    public void deleteRestaurante(Long id) {
        Restaurante restaurante = this.restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado. ID: " + id));
        this.restauranteRepository.delete(restaurante);
    }
}
