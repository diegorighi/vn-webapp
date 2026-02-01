package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import com.vanessaviagem.backoffice.domain.model.enums.StatusCliente;
import com.vanessaviagem.backoffice.domain.model.enums.TipoCliente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root representing a Customer.
 * A customer can be either TITULAR (primary) or DEPENDENTE (linked to a titular).
 * Dependents are always linked to a titular and cascade delete when titular is deleted.
 */
public record Cliente(
        UUID id,
        UUID tenantId,
        TipoCliente tipo,
        String nome,
        String nomeDoMeio,
        String sobrenome,
        LocalDate dataNascimento,
        Sexo sexo,
        List<ClienteDocumento> documentos,
        List<ClienteEndereco> enderecos,
        List<ClienteContato> contatos,
        boolean ativo,
        StatusCliente status,
        String observacoes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public Cliente {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(nome, "nome eh obrigatorio");
        Objects.requireNonNull(sobrenome, "sobrenome eh obrigatorio");
        Objects.requireNonNull(documentos, "documentos eh obrigatorio");
        Objects.requireNonNull(enderecos, "enderecos eh obrigatorio");
        Objects.requireNonNull(contatos, "contatos eh obrigatorio");
        Objects.requireNonNull(criadoEm, "criadoEm eh obrigatorio");
        Objects.requireNonNull(atualizadoEm, "atualizadoEm eh obrigatorio");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("nome nao pode ser vazio");
        }
        if (sobrenome.isBlank()) {
            throw new IllegalArgumentException("sobrenome nao pode ser vazio");
        }
    }

    /**
     * Creates a new customer with generated ID and timestamps.
     * Defaults to TITULAR type with ATIVO status.
     * Used by ClienteModuleService for basic CRUD.
     */
    public static Cliente criar(
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
        LocalDateTime now = LocalDateTime.now();
        StatusCliente finalStatus = status != null ? status : StatusCliente.ATIVO;
        return new Cliente(
                UUID.randomUUID(),
                tenantId,
                TipoCliente.TITULAR,
                nome,
                null,
                sobrenome,
                dataNascimento,
                null,
                documentos,
                enderecos,
                contatos,
                finalStatus == StatusCliente.ATIVO,
                finalStatus,
                observacoes,
                now,
                now
        );
    }

    /**
     * Creates a new TITULAR customer with full details.
     */
    public static Cliente criarTitular(
            UUID tenantId,
            String nome,
            String nomeDoMeio,
            String sobrenome,
            LocalDate dataNascimento,
            Sexo sexo,
            List<ClienteDocumento> documentos,
            List<ClienteEndereco> enderecos,
            List<ClienteContato> contatos,
            String observacoes
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new Cliente(
                UUID.randomUUID(),
                tenantId,
                TipoCliente.TITULAR,
                nome,
                nomeDoMeio,
                sobrenome,
                dataNascimento,
                sexo,
                documentos,
                enderecos,
                contatos,
                true,
                StatusCliente.ATIVO,
                observacoes,
                now,
                now
        );
    }

    /**
     * Creates a new DEPENDENTE customer with full details.
     */
    public static Cliente criarDependente(
            UUID tenantId,
            String nome,
            String nomeDoMeio,
            String sobrenome,
            LocalDate dataNascimento,
            Sexo sexo,
            List<ClienteDocumento> documentos,
            List<ClienteEndereco> enderecos,
            List<ClienteContato> contatos
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new Cliente(
                UUID.randomUUID(),
                tenantId,
                TipoCliente.DEPENDENTE,
                nome,
                nomeDoMeio,
                sobrenome,
                dataNascimento,
                sexo,
                documentos,
                enderecos,
                contatos,
                true,
                StatusCliente.ATIVO,
                null,
                now,
                now
        );
    }

    /**
     * Returns the full name of the customer.
     */
    public String nomeCompleto() {
        if (nomeDoMeio != null && !nomeDoMeio.isBlank()) {
            return nome + " " + nomeDoMeio + " " + sobrenome;
        }
        return nome + " " + sobrenome;
    }

    /**
     * Returns the primary document if exists.
     */
    public ClienteDocumento documentoPrincipal() {
        return documentos.stream()
                .filter(ClienteDocumento::principal)
                .findFirst()
                .orElse(documentos.isEmpty() ? null : documentos.get(0));
    }

    /**
     * Returns the primary address if exists.
     */
    public ClienteEndereco enderecoPrincipal() {
        return enderecos.stream()
                .filter(ClienteEndereco::principal)
                .findFirst()
                .orElse(enderecos.isEmpty() ? null : enderecos.get(0));
    }

    /**
     * Returns the primary contact if exists.
     */
    public ClienteContato contatoPrincipal() {
        return contatos.stream()
                .filter(ClienteContato::principal)
                .findFirst()
                .orElse(contatos.isEmpty() ? null : contatos.get(0));
    }

    /**
     * Creates a copy with updated status.
     * Used by ClienteModuleService.
     */
    public Cliente comStatus(StatusCliente novoStatus) {
        boolean novoAtivo = novoStatus == StatusCliente.ATIVO;
        return new Cliente(
                id, tenantId, tipo, nome, nomeDoMeio, sobrenome, dataNascimento, sexo,
                documentos, enderecos, contatos,
                novoAtivo, novoStatus, observacoes, criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Creates a copy with updated active status (boolean).
     */
    public Cliente comAtivo(boolean novoAtivo) {
        StatusCliente novoStatusCliente = novoAtivo ? StatusCliente.ATIVO : StatusCliente.INATIVO;
        return new Cliente(
                id, tenantId, tipo, nome, nomeDoMeio, sobrenome, dataNascimento, sexo,
                documentos, enderecos, contatos,
                novoAtivo, novoStatusCliente, observacoes, criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Creates a copy with updated data.
     * Used by ClienteModuleService.
     */
    public Cliente atualizar(
            String novoNome,
            String novoSobrenome,
            LocalDate novaDataNascimento,
            List<ClienteDocumento> novosDocumentos,
            List<ClienteEndereco> novosEnderecos,
            List<ClienteContato> novosContatos,
            StatusCliente novoStatus,
            String novasObservacoes
    ) {
        StatusCliente finalStatus = novoStatus != null ? novoStatus : status;
        boolean novoAtivo = finalStatus == StatusCliente.ATIVO;
        return new Cliente(
                id, tenantId, tipo,
                novoNome, nomeDoMeio, novoSobrenome, novaDataNascimento, sexo,
                novosDocumentos, novosEnderecos, novosContatos,
                novoAtivo, finalStatus, novasObservacoes,
                criadoEm, LocalDateTime.now()
        );
    }

    /**
     * Checks if this is a titular customer.
     */
    public boolean isTitular() {
        return tipo == TipoCliente.TITULAR;
    }

    /**
     * Checks if this is a dependent customer.
     */
    public boolean isDependente() {
        return tipo == TipoCliente.DEPENDENTE;
    }

    /**
     * Checks if the customer is active.
     */
    public boolean isAtivo() {
        return ativo;
    }
}
