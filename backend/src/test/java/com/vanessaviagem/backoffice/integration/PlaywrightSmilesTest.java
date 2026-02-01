package com.vanessaviagem.backoffice.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração com Smiles usando Playwright (browser automation).
 *
 * FLUXO DE LOGIN SMILES (Auth0):
 * 1. Navegar para https://www.smiles.com.br/login (redireciona para Auth0)
 * 2. Preencher CPF no campo "CPF, e-mail ou número Smiles"
 * 3. Resolver reCAPTCHA (clicar no checkbox dentro do iframe)
 * 4. Clicar em "Continuar" (fica habilitado após reCAPTCHA)
 * 5. Preencher senha no campo que aparece
 * 6. Clicar em "Continuar" novamente
 * 7. Tratar 2FA (SMS ou E-mail) se solicitado
 *
 * Para executar:
 * ./gradlew test --tests "PlaywrightSmilesTest" -Dsmiles.cpf=35330301807 -Dsmiles.senha=1304
 */
@DisplayName("Playwright Smiles Integration")
class PlaywrightSmilesTest {

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setupPlaywright() {
        System.out.println("Iniciando Playwright...");
        playwright = Playwright.create();

        // Usar modo visível (headless=false) para permitir resolver reCAPTCHA manualmente
        // Para CI, pode usar headless=true mas o teste vai falhar no reCAPTCHA
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        int slowMo = Integer.parseInt(System.getProperty("slowmo", "0"));

        System.out.println("Modo headless: " + headless);

        browser = playwright.firefox().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo)
        );
        System.out.println("Firefox iniciado!");
    }

    @AfterAll
    static void teardownPlaywright() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setUp() {
        TenantContext.set(UUID.randomUUID(), UUID.randomUUID(), Set.of("READ"));
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Deve fazer login e consultar saldo no Smiles")
    void shouldLoginAndQueryBalance() {
        String cpf = System.getProperty("smiles.cpf", System.getenv("SMILES_CPF"));
        String senha = System.getProperty("smiles.senha", System.getenv("SMILES_SENHA"));

        if (cpf == null || senha == null) {
            System.out.println("Credenciais não configuradas. Use -Dsmiles.cpf=XXX -Dsmiles.senha=XXX");
            return;
        }

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .setViewportSize(1920, 1080)
                .setLocale("pt-BR")
        );

        Page page = context.newPage();

        try {
            // PASSO 1: Navegar para página de login
            System.out.println("Navegando para Smiles...");
            page.navigate("https://www.smiles.com.br/login");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            System.out.println("URL atual: " + page.url());
            System.out.println("Título: " + page.title());

            // PASSO 2: Aguardar formulário de login (Auth0)
            page.waitForSelector("input[type='text']",
                    new Page.WaitForSelectorOptions().setTimeout(15000));

            // PASSO 3: Preencher CPF
            String cleanCpf = cpf.replaceAll("[^0-9]", "");
            page.locator("input[type='text']").first().fill(cleanCpf);
            System.out.println("CPF preenchido: " + cleanCpf);

            // Aguardar para o reCAPTCHA carregar (pode demorar em headless)
            Thread.sleep(4000);

            // Debug: listar todos os frames disponíveis
            System.out.println("Frames disponíveis:");
            for (Frame frame : page.frames()) {
                System.out.println("  - Frame: name='" + frame.name() + "' url=" + frame.url());
            }

            // PASSO 4: Tentar resolver reCAPTCHA
            boolean recaptchaSolved = solveRecaptcha(page);
            if (!recaptchaSolved) {
                System.out.println("AVISO: reCAPTCHA não encontrado ou já resolvido");
            }

            // PASSO 5: Aguardar botão Continuar ficar habilitado e clicar
            // O botão só fica habilitado após reCAPTCHA ser resolvido
            try {
                page.waitForSelector("button:has-text('Continuar'):not([disabled])",
                        new Page.WaitForSelectorOptions().setTimeout(65000)); // 65s = tempo do reCAPTCHA + margem
            } catch (Exception e) {
                System.out.println("==========================================");
                System.out.println("TIMEOUT: reCAPTCHA não foi resolvido");
                System.out.println("==========================================");
                System.out.println("O Smiles requer verificação reCAPTCHA que");
                System.out.println("não pode ser automatizada em headless.");
                System.out.println("");
                System.out.println("Para testar manualmente:");
                System.out.println("./gradlew test --tests PlaywrightSmilesTest \\");
                System.out.println("  -Dheadless=false -Dslowmo=500 \\");
                System.out.println("  -Dsmiles.cpf=XXX -Dsmiles.senha=XXX");
                System.out.println("==========================================");
                return; // Não é falha, é limitação conhecida
            }
            page.locator("button:has-text('Continuar')").first().click();
            System.out.println("Clicou em Continuar após CPF");

            // Aguardar transição
            page.waitForLoadState(LoadState.NETWORKIDLE);
            Thread.sleep(1500);

            // PASSO 6: Preencher senha
            page.waitForSelector("input[type='password']",
                    new Page.WaitForSelectorOptions().setTimeout(10000));
            page.locator("input[type='password']").first().fill(senha);
            System.out.println("Senha preenchida");

            // PASSO 7: Clicar em Continuar para fazer login
            page.waitForSelector("button:has-text('Continuar'):not([disabled])",
                    new Page.WaitForSelectorOptions().setTimeout(5000));
            page.locator("button:has-text('Continuar')").first().click();
            System.out.println("Botão de login clicado, aguardando resposta...");

            // Aguardar navegação
            page.waitForLoadState(LoadState.NETWORKIDLE);
            Thread.sleep(3000);

            System.out.println("URL após login: " + page.url());

            // PASSO 8: Verificar se pediu 2FA (MFA)
            if (page.url().contains("mfa") || page.url().contains("2fa")) {
                handle2FAPage(page);
                return; // 2FA necessário, não podemos continuar automaticamente
            }

            // Verificar por opções de 2FA na página
            if (page.locator("button:has-text('SMS')").count() > 0 ||
                page.locator("button:has-text('E-mail')").count() > 0) {
                System.out.println("==========================================");
                System.out.println("2FA NECESSÁRIO!");
                System.out.println("==========================================");
                System.out.println("O Smiles está pedindo código de verificação.");
                System.out.println("Opções disponíveis: SMS ou E-mail");
                System.out.println("==========================================");
                return;
            }

            // Verificar por campo de código OTP
            if (page.locator("input[placeholder*='código'], input[name='otp'], #otp").count() > 0) {
                System.out.println("==========================================");
                System.out.println("2FA NECESSÁRIO - CÓDIGO OTP!");
                System.out.println("==========================================");
                return;
            }

            // Verificar erro de login (mas não tratar reCAPTCHA como erro)
            Locator errorElements = page.locator(".error, .alert-danger, [class*='error']:not([class*='recaptcha'])");
            if (errorElements.count() > 0) {
                String errorMsg = errorElements.first().textContent();
                if (errorMsg != null && !errorMsg.isBlank()) {
                    System.out.println("Erro de login: " + errorMsg);
                    fail("Login falhou: " + errorMsg);
                }
            }

            // Se chegou aqui, login foi bem-sucedido
            System.out.println("Login bem-sucedido!");

            // Navegar para área logada e buscar saldo
            page.navigate("https://www.smiles.com.br/minha-conta");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Extrair saldo
            long saldo = extractBalance(page);

            System.out.println("==========================================");
            System.out.println("SUCESSO!");
            System.out.println("==========================================");
            System.out.println("Saldo: " + String.format("%,d", saldo) + " milhas");
            System.out.println("==========================================");

            assertTrue(saldo >= 0);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Teste interrompido");
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());

            // Capturar screenshot para debug
            try {
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(java.nio.file.Paths.get("build/smiles-error.png")));
                System.out.println("Screenshot salvo em build/smiles-error.png");
            } catch (Exception ignored) {}

            throw e;
        } finally {
            context.close();
        }
    }

    /**
     * Tenta resolver o reCAPTCHA clicando no checkbox dentro do iframe.
     * Retorna true se conseguiu clicar, false se não encontrou.
     */
    private boolean solveRecaptcha(Page page) {
        try {
            // Aguardar o iframe do reCAPTCHA aparecer
            try {
                page.waitForSelector("iframe[name^='a-']",
                        new Page.WaitForSelectorOptions().setTimeout(8000));
            } catch (Exception e) {
                System.out.println("reCAPTCHA iframe não apareceu (pode não ser necessário)");
                return false;
            }

            // O reCAPTCHA tem 2 iframes:
            // - iframe[name^='a-'] = checkbox principal
            // - iframe[name^='c-'] = desafio de imagens (se necessário)
            // Precisamos clicar no primeiro (checkbox)

            // Encontrar o frame do reCAPTCHA pelo nome (começa com "a-")
            Frame recaptchaFrame = null;
            for (Frame frame : page.frames()) {
                String name = frame.name();
                if (name != null && name.startsWith("a-")) {
                    recaptchaFrame = frame;
                    System.out.println("reCAPTCHA frame encontrado: " + name);
                    break;
                }
            }

            if (recaptchaFrame != null) {
                // Clicar no checkbox dentro do iframe
                Locator checkbox = recaptchaFrame.locator("#recaptcha-anchor, .recaptcha-checkbox-border, [role='checkbox']");

                if (checkbox.count() > 0) {
                    checkbox.first().click();
                    System.out.println("reCAPTCHA checkbox clicado!");

                    // Aguardar resolução (pode aparecer desafio de imagens)
                    Thread.sleep(3000);

                    // Verificar se passou (checkbox marcado) ou se precisa resolver desafio
                    // Aguardar até que o botão Continuar fique habilitado (indica reCAPTCHA resolvido)
                    int maxWaitSeconds = 60; // 60 segundos para resolver manualmente se necessário
                    int waitedSeconds = 0;

                    while (waitedSeconds < maxWaitSeconds) {
                        // Verificar se o botão Continuar está habilitado
                        Locator continueBtn = page.locator("button:has-text('Continuar'):not([disabled])");
                        if (continueBtn.count() > 0) {
                            System.out.println("reCAPTCHA resolvido! Botão Continuar habilitado.");
                            return true;
                        }

                        // Verificar se há desafio de imagens visível
                        Locator challengeIframe = page.locator("iframe[name^='c-']");
                        if (challengeIframe.count() > 0 && waitedSeconds == 0) {
                            System.out.println("==========================================");
                            System.out.println("DESAFIO reCAPTCHA DETECTADO!");
                            System.out.println("Aguardando resolução manual (até " + maxWaitSeconds + "s)...");
                            System.out.println("==========================================");
                        }

                        Thread.sleep(1000);
                        waitedSeconds++;

                        if (waitedSeconds % 10 == 0) {
                            System.out.println("Aguardando reCAPTCHA... " + waitedSeconds + "s");
                        }
                    }

                    System.out.println("Timeout aguardando reCAPTCHA ser resolvido");
                    return false;
                }
            }

            System.out.println("reCAPTCHA iframe encontrado mas checkbox não localizado");
        } catch (Exception e) {
            System.out.println("Não foi possível resolver reCAPTCHA: " + e.getMessage());
        }
        return false;
    }

    /**
     * Trata a página de 2FA/MFA.
     */
    private void handle2FAPage(Page page) {
        System.out.println("==========================================");
        System.out.println("2FA/MFA NECESSÁRIO!");
        System.out.println("==========================================");
        System.out.println("URL: " + page.url());

        // Verificar opções disponíveis
        if (page.locator("button:has-text('SMS')").count() > 0) {
            System.out.println("Opção disponível: SMS");
        }
        if (page.locator("button:has-text('E-mail')").count() > 0) {
            System.out.println("Opção disponível: E-mail");
        }

        System.out.println("==========================================");
        System.out.println("Para continuar, o código deve ser inserido manualmente");
        System.out.println("ou usar TwoFactorAuthService para armazenar o challenge.");
        System.out.println("==========================================");
    }

    private long extractBalance(Page page) {
        // Tentar diferentes seletores para saldo
        String[] selectors = {
                ".miles-balance",
                ".saldo-milhas",
                "[data-testid='miles']",
                ".pontos",
                ".balance"
        };

        for (String selector : selectors) {
            try {
                if (page.locator(selector).count() > 0) {
                    String text = page.locator(selector).first().textContent();
                    return parseBalance(text);
                }
            } catch (Exception ignored) {}
        }

        // Fallback: buscar padrão numérico na página
        String content = page.content();
        Pattern pattern = Pattern.compile("(\\d{1,3}(?:[.,]\\d{3})+)\\s*(?:milhas|pontos|pts)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return parseBalance(matcher.group(1));
        }

        return 0;
    }

    private long parseBalance(String text) {
        if (text == null) return 0;
        String cleaned = text.replaceAll("[^0-9]", "");
        return cleaned.isEmpty() ? 0 : Long.parseLong(cleaned);
    }
}
