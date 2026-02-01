package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.out.ClienteRepository;
import com.vanessaviagem.backoffice.application.ports.out.ViagemRepository;
import com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoException;
import com.vanessaviagem.backoffice.domain.exceptions.TitularInativoException;
import com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaException;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing clients (titular and dependents) and their trips.
 * This service orchestrates domain operations and coordinates with repositories.
 *
 * <p>All methods use TenantContext for multi-tenant isolation.</p>
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ViagemRepository viagemRepository;

    public ClienteService(ClienteRepository clienteRepository, ViagemRepository viagemRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository,
                "clienteRepository eh obrigatorio");
        this.viagemRepository = Objects.requireNonNull(viagemRepository,
                "viagemRepository eh obrigatorio");
    }

    private UUID currentTenantId() {
        return TenantContext.current().accountId();
    }

    // ========== TITULAR OPERATIONS ==========

    @Transactional
    public ClienteTitular registrarTitular(ClienteTitular titular) {
        Objects.requireNonNull(titular, "titular eh obrigatorio");
        return clienteRepository.salvarTitular(titular);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteTitular> buscarTitular(UUID titularId) {
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");
        return clienteRepository.buscarTitular(titularId);
    }

    @Transactional(readOnly = true)
    public List<ClienteTitular> buscarTodosTitulares() {
        return clienteRepository.buscarTodosTitulares();
    }

    @Transactional
    public ClienteTitular atualizarTitular(ClienteTitular titular) {
        Objects.requireNonNull(titular, "titular eh obrigatorio");
        Objects.requireNonNull(titular.clienteId(), "titular.clienteId eh obrigatorio");

        clienteRepository.buscarTitular(titular.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(titular.clienteId()));

        return clienteRepository.atualizarTitular(titular);
    }

    @Transactional
    public void desativarTitular(UUID titularId) {
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");

        ClienteTitular titular = clienteRepository.buscarTitular(titularId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(titularId));

        ClienteTitular desativado = new ClienteTitular(
                titular.clienteId(),
                titular.dadosPessoais(),
                titular.viagens(),
                false,
                titular.dependentes()
        );

        clienteRepository.atualizarTitular(desativado);

        List<ClienteDependente> dependentes = clienteRepository.buscarDependentesPorTitular(titularId);
        for (ClienteDependente dependente : dependentes) {
            if (dependente.ativo()) {
                clienteRepository.atualizarDependente(dependente.comStatus(false));
            }
        }
    }

    // ========== DEPENDENTE OPERATIONS ==========

    @Transactional
    public ClienteDependente adicionarDependente(UUID titularId, Parentesco parentesco,
                                                  Pessoa dependenteDados) {
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");
        Objects.requireNonNull(parentesco, "parentesco eh obrigatorio");
        Objects.requireNonNull(dependenteDados, "dependenteDados eh obrigatorio");

        ClienteTitular titular = clienteRepository.buscarTitular(titularId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(titularId));

        if (!titular.ativo()) {
            throw new TitularInativoException(titularId);
        }

        ClienteDependente dependente = new ClienteDependente(
                UUID.randomUUID(),
                titularId,
                parentesco,
                dependenteDados,
                List.of(),
                true
        );

        return clienteRepository.salvarDependente(dependente);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDependente> buscarDependente(UUID dependenteId) {
        Objects.requireNonNull(dependenteId, "dependenteId eh obrigatorio");
        return clienteRepository.buscarDependente(dependenteId);
    }

    @Transactional(readOnly = true)
    public List<ClienteDependente> buscarDependentesPorTitular(UUID titularId) {
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");

        clienteRepository.buscarTitular(titularId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(titularId));

        return clienteRepository.buscarDependentesPorTitular(titularId);
    }

    @Transactional
    public ClienteDependente atualizarDependente(ClienteDependente dependente) {
        Objects.requireNonNull(dependente, "dependente eh obrigatorio");
        Objects.requireNonNull(dependente.clienteId(), "dependente.clienteId eh obrigatorio");

        clienteRepository.buscarDependente(dependente.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(dependente.clienteId(),
                        String.format("Dependente nao encontrado: %s", dependente.clienteId())));

        return clienteRepository.atualizarDependente(dependente);
    }

    @Transactional
    public void removerDependente(UUID dependenteId) {
        Objects.requireNonNull(dependenteId, "dependenteId eh obrigatorio");

        clienteRepository.buscarDependente(dependenteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(dependenteId,
                        String.format("Dependente nao encontrado: %s", dependenteId)));

        clienteRepository.excluirDependente(dependenteId);
    }

    // ========== VIAGEM OPERATIONS ==========

    @Transactional
    public Viagem registrarViagem(UUID titularId, Viagem viagem) {
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");
        Objects.requireNonNull(viagem, "viagem eh obrigatorio");

        clienteRepository.buscarTitular(titularId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(titularId));

        return viagemRepository.salvar(currentTenantId(), titularId, viagem);
    }

    @Transactional(readOnly = true)
    public Optional<Viagem> buscarViagem(UUID viagemId) {
        Objects.requireNonNull(viagemId, "viagemId eh obrigatorio");
        return viagemRepository.buscarPorId(currentTenantId(), viagemId);
    }

    @Transactional(readOnly = true)
    public List<Viagem> buscarViagensPorTitular(UUID titularId) {
        Objects.requireNonNull(titularId, "titularId eh obrigatorio");

        clienteRepository.buscarTitular(titularId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(titularId));

        return viagemRepository.buscarPorTitular(currentTenantId(), titularId);
    }

    @Transactional(readOnly = true)
    public List<Viagem> buscarTodasViagens() {
        return viagemRepository.buscarTodos(currentTenantId());
    }

    @Transactional
    public Viagem atualizarViagem(Viagem viagem) {
        Objects.requireNonNull(viagem, "viagem eh obrigatorio");
        Objects.requireNonNull(viagem.viagemId(), "viagem.viagemId eh obrigatorio");

        UUID tenantId = currentTenantId();
        viagemRepository.buscarPorId(tenantId, viagem.viagemId())
                .orElseThrow(() -> new ViagemNaoEncontradaException(viagem.viagemId()));

        return viagemRepository.atualizar(tenantId, viagem);
    }

    @Transactional
    public void cancelarViagem(UUID viagemId) {
        Objects.requireNonNull(viagemId, "viagemId eh obrigatorio");

        UUID tenantId = currentTenantId();
        Viagem viagem = viagemRepository.buscarPorId(tenantId, viagemId)
                .orElseThrow(() -> new ViagemNaoEncontradaException(viagemId));

        Viagem cancelada = new Viagem(
                viagem.viagemId(),
                viagem.localizador(),
                viagem.trecho(),
                viagem.data(),
                viagem.assento(),
                viagem.companhiaAereaList(),
                viagem.moeda(),
                viagem.precoTotal(),
                ViagemStatus.CANCELADO,
                viagem.createdAt(),
                OffsetDateTime.now()
        );

        viagemRepository.atualizar(tenantId, cancelada);
    }

    @Transactional
    public void excluirViagem(UUID viagemId) {
        Objects.requireNonNull(viagemId, "viagemId eh obrigatorio");

        viagemRepository.excluir(currentTenantId(), viagemId);
    }
}
