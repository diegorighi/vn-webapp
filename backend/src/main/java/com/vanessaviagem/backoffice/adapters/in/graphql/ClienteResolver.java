package com.vanessaviagem.backoffice.adapters.in.graphql;

import com.vanessaviagem.backoffice.application.services.ClienteModuleService;
import com.vanessaviagem.backoffice.domain.model.cliente.Cliente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteContato;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDocumento;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteEndereco;
import com.vanessaviagem.backoffice.domain.model.enums.StatusCliente;
import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import com.vanessaviagem.backoffice.domain.model.enums.TipoEndereco;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.vanessaviagem.backoffice.domain.model.TenantContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
public class ClienteResolver {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ClienteModuleService service;

    public ClienteResolver(ClienteModuleService service) {
        this.service = service;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<ClienteDTO> clientes() {
        UUID tenantId = TenantContext.current().tenantId();
        return service.listarTodos(tenantId).stream()
                .map(this::toDTO)
                .toList();
    }

    @QueryMapping
    public ClienteDTO clienteById(@Argument String id) {
        UUID tenantId = TenantContext.current().tenantId();
        return service.buscarPorId(tenantId, UUID.fromString(id))
                .map(this::toDTO)
                .orElse(null);
    }

    @QueryMapping
    public List<ClienteDTO> clientesPorStatus(@Argument StatusCliente status) {
        UUID tenantId = TenantContext.current().tenantId();
        return service.listarPorStatus(tenantId, status).stream()
                .map(this::toDTO)
                .toList();
    }

    @QueryMapping
    public ResumoClientesDTO resumoClientes() {
        UUID tenantId = TenantContext.current().tenantId();
        var resumo = service.resumo(tenantId);
        return new ResumoClientesDTO(
                resumo.totalClientes(),
                resumo.ativos(),
                resumo.inativos(),
                resumo.pendentes(),
                resumo.clientes().stream().map(this::toDTO).toList()
        );
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public ClienteDTO criarCliente(@Argument CriarClienteInput input) {
        UUID tenantId = TenantContext.current().tenantId();
        LocalDate dataNascimento = input.dataNascimento != null && !input.dataNascimento.isBlank()
                ? LocalDate.parse(input.dataNascimento, DATE_FORMATTER)
                : null;

        Cliente cliente = service.criarCliente(
                tenantId,
                input.nome,
                input.sobrenome,
                dataNascimento,
                mapDocumentos(input.documentos),
                mapEnderecos(input.enderecos),
                mapContatos(input.contatos),
                input.status,
                input.observacoes
        );

        return toDTO(cliente);
    }

    @MutationMapping
    public ClienteDTO atualizarCliente(@Argument AtualizarClienteInput input) {
        UUID tenantId = TenantContext.current().tenantId();
        LocalDate dataNascimento = input.dataNascimento != null && !input.dataNascimento.isBlank()
                ? LocalDate.parse(input.dataNascimento, DATE_FORMATTER)
                : null;

        Cliente cliente = service.atualizarCliente(
                tenantId,
                UUID.fromString(input.id),
                input.nome,
                input.sobrenome,
                dataNascimento,
                mapDocumentos(input.documentos),
                mapEnderecos(input.enderecos),
                mapContatos(input.contatos),
                input.status,
                input.observacoes
        );

        return toDTO(cliente);
    }

    @MutationMapping
    public ClienteDTO alterarStatusCliente(@Argument String id, @Argument StatusCliente status) {
        UUID tenantId = TenantContext.current().tenantId();
        Cliente cliente = service.alterarStatus(tenantId, UUID.fromString(id), status);
        return toDTO(cliente);
    }

    @MutationMapping
    public boolean excluirCliente(@Argument String id) {
        UUID tenantId = TenantContext.current().tenantId();
        service.excluirCliente(tenantId, UUID.fromString(id));
        return true;
    }

    // ==================== MAPPERS ====================

    private ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.id().toString(),
                cliente.nome(),
                cliente.sobrenome(),
                cliente.dataNascimento() != null ? cliente.dataNascimento().toString() : null,
                cliente.documentos().stream().map(this::toDocumentoDTO).toList(),
                cliente.enderecos().stream().map(this::toEnderecoDTO).toList(),
                cliente.contatos().stream().map(this::toContatoDTO).toList(),
                cliente.status(),
                cliente.observacoes(),
                cliente.criadoEm() != null ? cliente.criadoEm().toString() : null,
                cliente.atualizadoEm() != null ? cliente.atualizadoEm().toString() : null
        );
    }

    private ClienteDocumentoDTO toDocumentoDTO(ClienteDocumento doc) {
        return new ClienteDocumentoDTO(
                doc.id().toString(),
                doc.tipo(),
                doc.numero(),
                doc.principal(),
                doc.arquivoUrl(),
                doc.nomeArquivo()
        );
    }

    private ClienteEnderecoDTO toEnderecoDTO(ClienteEndereco end) {
        return new ClienteEnderecoDTO(
                end.id().toString(),
                end.tipo(),
                end.cep(),
                end.logradouro(),
                end.numero(),
                end.complemento(),
                end.bairro(),
                end.cidade(),
                end.estado(),
                end.principal()
        );
    }

    private ClienteContatoDTO toContatoDTO(ClienteContato contato) {
        return new ClienteContatoDTO(
                contato.id().toString(),
                contato.tipo(),
                contato.valor(),
                contato.principal()
        );
    }

    private List<ClienteDocumento> mapDocumentos(List<ClienteDocumentoInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new ClienteDocumento(
                        UUID.randomUUID(),
                        i.tipo,
                        i.numero,
                        i.principal,
                        i.arquivoUrl,
                        i.nomeArquivo
                ))
                .toList();
    }

    private List<ClienteEndereco> mapEnderecos(List<ClienteEnderecoInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new ClienteEndereco(
                        UUID.randomUUID(),
                        i.tipo,
                        i.cep,
                        i.logradouro,
                        i.numero,
                        i.complemento,
                        i.bairro,
                        i.cidade,
                        i.estado,
                        i.principal
                ))
                .toList();
    }

    private List<ClienteContato> mapContatos(List<ClienteContatoInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new ClienteContato(
                        UUID.randomUUID(),
                        i.tipo,
                        i.valor,
                        i.principal
                ))
                .toList();
    }

    // ==================== DTOs ====================

    public record ClienteDTO(
            String id,
            String nome,
            String sobrenome,
            String dataNascimento,
            List<ClienteDocumentoDTO> documentos,
            List<ClienteEnderecoDTO> enderecos,
            List<ClienteContatoDTO> contatos,
            StatusCliente status,
            String observacoes,
            String criadoEm,
            String atualizadoEm
    ) {}

    public record ClienteDocumentoDTO(
            String id,
            TipoDocumento tipo,
            String numero,
            boolean principal,
            String arquivoUrl,
            String nomeArquivo
    ) {}

    public record ClienteEnderecoDTO(
            String id,
            TipoEndereco tipo,
            String cep,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            boolean principal
    ) {}

    public record ClienteContatoDTO(
            String id,
            TipoContato tipo,
            String valor,
            boolean principal
    ) {}

    public record ResumoClientesDTO(
            int totalClientes,
            int ativos,
            int inativos,
            int pendentes,
            List<ClienteDTO> clientes
    ) {}

    // ==================== INPUT DTOs ====================

    public record CriarClienteInput(
            String nome,
            String sobrenome,
            String dataNascimento,
            List<ClienteDocumentoInput> documentos,
            List<ClienteEnderecoInput> enderecos,
            List<ClienteContatoInput> contatos,
            StatusCliente status,
            String observacoes
    ) {}

    public record AtualizarClienteInput(
            String id,
            String nome,
            String sobrenome,
            String dataNascimento,
            List<ClienteDocumentoInput> documentos,
            List<ClienteEnderecoInput> enderecos,
            List<ClienteContatoInput> contatos,
            StatusCliente status,
            String observacoes
    ) {}

    public record ClienteDocumentoInput(
            TipoDocumento tipo,
            String numero,
            boolean principal,
            String arquivoUrl,
            String nomeArquivo
    ) {}

    public record ClienteEnderecoInput(
            TipoEndereco tipo,
            String cep,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            boolean principal
    ) {}

    public record ClienteContatoInput(
            TipoContato tipo,
            String valor,
            boolean principal
    ) {}
}
