package com.vanessaviagem.backoffice.adapters.in.graphql;

import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * GraphQL resolver for development-only queries.
 * These queries expose accounts and users for testing multi-tenant isolation.
 *
 * <p>IMPORTANT: This resolver is only active in 'dev' profile.</p>
 */
@Controller
@Profile("dev")
public class DevResolver {

    private final JdbcTemplate jdbc;

    public DevResolver(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Lists all accounts from the database.
     * Used by DevToolbar to switch between tenants.
     */
    @QueryMapping
    public List<Map<String, Object>> devAccounts() {
        return jdbc.query("""
            SELECT id, name as nome, slug, email, status, plan
            FROM account
            ORDER BY name
            """,
            (rs, rowNum) -> Map.of(
                "id", rs.getString("id"),
                "nome", rs.getString("nome"),
                "slug", rs.getString("slug"),
                "email", rs.getString("email"),
                "status", rs.getString("status"),
                "plan", rs.getString("plan")
            )
        );
    }

    /**
     * Lists all users for a specific account.
     * Used by DevToolbar to switch between user profiles.
     */
    @QueryMapping
    public List<Map<String, Object>> devUsersByAccount(@Argument String accountId) {
        UUID accountUuid = UUID.fromString(accountId);

        return jdbc.query("""
            SELECT u.id, u.email, u.name as nome, r.type as role_type
            FROM "user" u
            LEFT JOIN user_role ur ON u.id = ur.user_id
            LEFT JOIN role r ON ur.role_id = r.id
            WHERE u.account_id = ?
            ORDER BY u.name
            """,
            (rs, rowNum) -> {
                String roleType = rs.getString("role_type");
                return Map.of(
                    "id", rs.getString("id"),
                    "email", rs.getString("email"),
                    "nome", rs.getString("nome"),
                    "roles", roleType != null ? List.of(roleType) : List.of()
                );
            },
            accountUuid
        );
    }
}
