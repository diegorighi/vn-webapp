package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.out.ClienteModuleRepository;
import com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoException;
import com.vanessaviagem.backoffice.domain.model.cliente.Cliente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteContato;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDocumento;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteEndereco;
import com.vanessaviagem.backoffice.domain.model.enums.StatusCliente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service for Cliente module operations.
 * Orchestrates domain operations and coordinates with repositories.
 */
@Service
public class ClienteModuleService {

    private final ClienteModuleRepository repository;

    public ClienteModuleService(ClienteModuleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository eh obrigatorio");
    }

    /**
     * Creates a new cliente.
     *
     * @param tenantId       the tenant ID
     * @param nome           first name
     * @param sobrenome      last name
     * @param dataNascimento birth date (optional)
     * @param documentos     list of documents
     * @param enderecos      list of addresses
     * @param contatos       list of contacts
     * @param status         status (optional, defaults to ATIVO)
     * @param observacoes    notes (optional)
     * @return the created cliente
     */
    @Transactional
    public Cliente criarCliente(
            UUID tenantId,
            String nome,
            String sobrenome,
            LocalDate dataNascimento,
            List<ClienteDocumento> documentos,
            List<ClienteEndereco> enderecos,
            List<ClienteContato> contatos,
            StatusCliente status,
            String observacoes
    ) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(nome, "nome eh obrigatorio");
        Objects.requireNonNull(sobrenome, "sobrenome eh obrigatorio");

        Cliente cliente = Cliente.criar(
                tenantId,
                nome,
                sobrenome,
                dataNascimento,
                documentos != null ? documentos : List.of(),
                enderecos != null ? enderecos : List.of(),
                contatos != null ? contatos : List.of(),
                status,
                observacoes
        );

        return repository.salvar(cliente);
    }

    /**
     * Updates an existing cliente.
     *
     * @param tenantId       tenant ID (for security isolation)
     * @param id             cliente ID
     * @param nome           first name
     * @param sobrenome      last name
     * @param dataNascimento birth date (optional)
     * @param documentos     list of documents
     * @param enderecos      list of addresses
     * @param contatos       list of contacts
     * @param status         status (optional)
     * @param observacoes    notes (optional)
     * @return the updated cliente
     * @throws ClienteNaoEncontradoException if cliente not found
     */
    @Transactional
    public Cliente atualizarCliente(
            UUID tenantId,
            UUID id,
            String nome,
            String sobrenome,
            LocalDate dataNascimento,
            List<ClienteDocumento> documentos,
            List<ClienteEndereco> enderecos,
            List<ClienteContato> contatos,
            StatusCliente status,
            String observacoes
    ) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(nome, "nome eh obrigatorio");
        Objects.requireNonNull(sobrenome, "sobrenome eh obrigatorio");

        Cliente existing = repository.buscarPorId(tenantId, id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        Cliente updated = existing.atualizar(
                nome,
                sobrenome,
                dataNascimento,
                documentos != null ? documentos : List.of(),
                enderecos != null ? enderecos : List.of(),
                contatos != null ? contatos : List.of(),
                status,
                observacoes
        );

        return repository.atualizar(updated);
    }

    /**
     * Changes the status of a cliente.
     *
     * @param tenantId  tenant ID (for security isolation)
     * @param id        cliente ID
     * @param newStatus new status
     * @return the updated cliente
     * @throws ClienteNaoEncontradoException if cliente not found
     */
    @Transactional
    public Cliente alterarStatus(UUID tenantId, UUID id, StatusCliente newStatus) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(newStatus, "newStatus eh obrigatorio");

        Cliente existing = repository.buscarPorId(tenantId, id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        Cliente updated = existing.comStatus(newStatus);
        return repository.atualizar(updated);
    }

    /**
     * Deletes a cliente.
     *
     * @param tenantId tenant ID (for security isolation)
     * @param id cliente ID
     * @throws ClienteNaoEncontradoException if cliente not found
     */
    @Transactional
    public void excluirCliente(UUID tenantId, UUID id) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(id, "id eh obrigatorio");

        repository.buscarPorId(tenantId, id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        repository.excluir(tenantId, id);
    }

    /**
     * Finds a cliente by ID.
     *
     * @param tenantId tenant ID (for security isolation)
     * @param id cliente ID
     * @return an Optional containing the cliente if found
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(UUID tenantId, UUID id) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(id, "id eh obrigatorio");
        return repository.buscarPorId(tenantId, id);
    }

    /**
     * Lists all clientes for a tenant.
     *
     * @param tenantId tenant ID
     * @return list of clientes
     */
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos(UUID tenantId) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        return repository.buscarTodos(tenantId);
    }

    /**
     * Lists clientes by status for a tenant.
     *
     * @param tenantId tenant ID
     * @param status   status filter
     * @return list of clientes with the specified status
     */
    @Transactional(readOnly = true)
    public List<Cliente> listarPorStatus(UUID tenantId, StatusCliente status) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(status, "status eh obrigatorio");
        return repository.buscarPorStatus(tenantId, status);
    }

    /**
     * Returns a summary with counts by status.
     *
     * @param tenantId tenant ID
     * @return summary record
     */
    @Transactional(readOnly = true)
    public ResumoClientes resumo(UUID tenantId) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");

        int total = repository.contarTodos(tenantId);
        int ativos = repository.contarPorStatus(tenantId, StatusCliente.ATIVO);
        int inativos = repository.contarPorStatus(tenantId, StatusCliente.INATIVO);
        int pendentes = repository.contarPorStatus(tenantId, StatusCliente.PENDENTE);
        List<Cliente> clientes = repository.buscarTodos(tenantId);

        return new ResumoClientes(total, ativos, inativos, pendentes, clientes);
    }

    /**
     * Summary record for cliente counts and list.
     */
    public record ResumoClientes(
            int totalClientes,
            int ativos,
            int inativos,
            int pendentes,
            List<Cliente> clientes
    ) {}
}
