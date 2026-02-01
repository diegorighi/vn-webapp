package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.in.AtualizarProgramaUseCase;
import com.vanessaviagem.backoffice.application.ports.in.CadastrarProgramaUseCase;
import com.vanessaviagem.backoffice.application.ports.in.ConsultarProgramaUseCase;
import com.vanessaviagem.backoffice.application.ports.out.ProgramaMilhasRepository;
import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoException;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import com.vanessaviagem.backoffice.domain.model.enums.StatusPrograma;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing loyalty programs.
 * Implements all use cases related to program lifecycle management.
 *
 * <p>This service orchestrates domain operations and delegates persistence
 * to the repository port. It does not contain business logic, which belongs
 * in the domain layer.</p>
 */
@Service
public class ProgramaMilhasService implements
        CadastrarProgramaUseCase,
        AtualizarProgramaUseCase,
        ConsultarProgramaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProgramaMilhasService.class);

    private final ProgramaMilhasRepository repository;

    /**
     * Creates a new service instance.
     *
     * @param repository the repository for program persistence
     */
    public ProgramaMilhasService(ProgramaMilhasRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers a new loyalty program.
     *
     * @param command the command containing program data
     * @return the result with the created program
     * @throws IllegalStateException if a program with the same brand already exists
     */
    @Override
    @Transactional
    public CadastrarProgramaResult execute(CadastrarProgramaCommand command) {
        logger.info("Cadastrando novo programa de milhas: brand={}", command.brand());

        if (repository.existePorBrand(command.brand())) {
            logger.warn("Tentativa de cadastrar programa com brand duplicado: {}", command.brand());
            throw new IllegalStateException(
                    String.format("Ja existe um programa com o brand: %s", command.brand())
            );
        }

        ProgramaDeMilhas programa = new ProgramaDeMilhas(
                UUID.randomUUID(),
                command.brand(),
                StatusPrograma.ATIVO,
                command.moeda(),
                command.regrasArredondamento()
        );

        ProgramaDeMilhas salvo = repository.salvar(programa);
        logger.info("Programa cadastrado com sucesso: id={}, brand={}", salvo.id(), salvo.brand());

        return new CadastrarProgramaResult(salvo.id(), salvo);
    }

    /**
     * Updates an existing loyalty program.
     *
     * @param command the command containing update data
     * @return the result with the updated program
     * @throws ProgramaNaoEncontradoException if program does not exist
     */
    @Override
    @Transactional
    public AtualizarProgramaResult execute(AtualizarProgramaCommand command) {
        logger.info("Atualizando programa de milhas: id={}", command.programaId());

        ProgramaDeMilhas existente = repository.buscarPorId(command.programaId())
                .orElseThrow(() -> new ProgramaNaoEncontradoException(command.programaId()));

        ProgramaDeMilhas atualizado = aplicarAtualizacoes(existente, command);
        ProgramaDeMilhas salvo = repository.atualizar(atualizado);

        logger.info("Programa atualizado com sucesso: id={}", salvo.id());
        return new AtualizarProgramaResult(salvo);
    }

    /**
     * Finds a program by ID.
     *
     * @param id the program ID
     * @return an Optional containing the program if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProgramaDeMilhas> buscarPorId(UUID id) {
        logger.debug("Buscando programa por id: {}", id);
        return repository.buscarPorId(id);
    }

    /**
     * Finds a program by brand name.
     *
     * @param brand the brand to search
     * @return an Optional containing the program if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProgramaDeMilhas> buscarPorBrand(String brand) {
        logger.debug("Buscando programa por brand: {}", brand);
        return repository.buscarPorBrand(brand);
    }

    /**
     * Lists all loyalty programs.
     *
     * @return list of all programs
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProgramaDeMilhas> listarTodos() {
        logger.debug("Listando todos os programas de milhas");
        return repository.buscarTodos();
    }

    /**
     * Lists only active programs.
     *
     * @return list of active programs
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProgramaDeMilhas> listarAtivos() {
        logger.debug("Listando programas de milhas ativos");
        return repository.buscarAtivos();
    }

    /**
     * Cadastra um novo programa de milhas.
     *
     * @param programa o programa a ser cadastrado
     * @return o programa cadastrado
     * @throws IllegalStateException se ja existir programa com mesmo brand
     */
    @Transactional
    public ProgramaDeMilhas cadastrarPrograma(ProgramaDeMilhas programa) {
        logger.info("Cadastrando programa: brand={}", programa.brand());

        if (repository.existePorBrand(programa.brand())) {
            throw new IllegalStateException(
                    String.format("Ja existe um programa com o brand: %s", programa.brand())
            );
        }

        return repository.salvar(programa);
    }

    /**
     * Busca um programa pelo ID.
     *
     * @param id o ID do programa
     * @return Optional contendo o programa se encontrado
     */
    @Transactional(readOnly = true)
    public Optional<ProgramaDeMilhas> buscarPrograma(UUID id) {
        return repository.buscarPorId(id);
    }

    /**
     * Busca um programa pelo brand.
     *
     * @param brand o brand do programa
     * @return Optional contendo o programa se encontrado
     */
    @Transactional(readOnly = true)
    public Optional<ProgramaDeMilhas> buscarProgramaPorBrand(String brand) {
        return repository.buscarPorBrand(brand);
    }

    /**
     * Lista todos os programas.
     *
     * @return lista de todos os programas
     */
    @Transactional(readOnly = true)
    public List<ProgramaDeMilhas> listarProgramas() {
        return repository.buscarTodos();
    }

    /**
     * Lista apenas programas ativos.
     *
     * @return lista de programas ativos
     */
    @Transactional(readOnly = true)
    public List<ProgramaDeMilhas> listarProgramasAtivos() {
        return repository.buscarAtivos();
    }

    /**
     * Atualiza um programa existente.
     *
     * @param programa o programa com dados atualizados
     * @return o programa atualizado
     * @throws ProgramaNaoEncontradoException se o programa nao existir
     */
    @Transactional
    public ProgramaDeMilhas atualizarPrograma(ProgramaDeMilhas programa) {
        logger.info("Atualizando programa: id={}", programa.id());

        if (repository.buscarPorId(programa.id()).isEmpty()) {
            throw new ProgramaNaoEncontradoException(programa.id());
        }

        return repository.atualizar(programa);
    }

    /**
     * Desativa um programa (muda status para INATIVO).
     *
     * @param id o ID do programa a desativar
     * @throws ProgramaNaoEncontradoException se o programa nao existir
     */
    @Transactional
    public void desativarPrograma(UUID id) {
        logger.info("Desativando programa: id={}", id);

        ProgramaDeMilhas existente = repository.buscarPorId(id)
                .orElseThrow(() -> new ProgramaNaoEncontradoException(id));

        ProgramaDeMilhas desativado = existente.desativar();
        repository.atualizar(desativado);

        logger.info("Programa desativado com sucesso: id={}", id);
    }

    /**
     * Ativa um programa (muda status para ATIVO).
     *
     * @param id o ID do programa a ativar
     * @throws ProgramaNaoEncontradoException se o programa nao existir
     */
    @Transactional
    public void ativarPrograma(UUID id) {
        logger.info("Ativando programa: id={}", id);

        ProgramaDeMilhas existente = repository.buscarPorId(id)
                .orElseThrow(() -> new ProgramaNaoEncontradoException(id));

        ProgramaDeMilhas ativado = existente.ativar();
        repository.atualizar(ativado);

        logger.info("Programa ativado com sucesso: id={}", id);
    }

    /**
     * Applies partial updates from command to existing program.
     */
    private ProgramaDeMilhas aplicarAtualizacoes(
            ProgramaDeMilhas existente,
            AtualizarProgramaCommand command
    ) {
        String brand = command.brand() != null ? command.brand() : existente.brand();
        String moeda = command.moeda() != null ? command.moeda() : existente.moeda();
        StatusPrograma status = command.status() != null ? command.status() : existente.status();
        var regras = command.regrasArredondamento() != null
                ? command.regrasArredondamento()
                : existente.regrasArredondamento();

        return new ProgramaDeMilhas(existente.id(), brand, status, moeda, regras);
    }
}
