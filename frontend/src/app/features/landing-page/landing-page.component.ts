import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="landing">
      <!-- Navigation -->
      <nav class="nav" [class.nav--scrolled]="isScrolled">
        <div class="nav__container">
          <a routerLink="/landing" class="nav__logo">
            <img src="images/vn-group.png" alt="Vá Nessa Viagem">
          </a>
          
          <div class="nav__links">
            <a href="#features" class="nav__link">Recursos</a>
            <a href="#how-it-works" class="nav__link">Como Funciona</a>
            <a href="#testimonials" class="nav__link">Depoimentos</a>
          </div>
          
          <a routerLink="/login" class="nav__cta">Entrar</a>
        </div>
      </nav>

      <!-- Hero Section -->
      <section class="hero">
        <div class="hero__parallax">
          <div class="hero__parallax-layer hero__parallax-layer--back" 
               [style.transform]="'translateY(' + scrollY * 0.3 + 'px)'">
            <img src="https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1920&q=80" alt="Travel landscape">
          </div>
          <div class="hero__parallax-layer hero__parallax-layer--mid" 
               [style.transform]="'translateY(' + scrollY * 0.15 + 'px)'">
            <img src="https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=1920&q=80" alt="Airplane wing">
          </div>
        </div>
        <div class="hero__overlay"></div>
        
        <div class="hero__container">
          <div class="hero__content">
            <div class="hero__badge">
              <span class="hero__badge-dot"></span>
              Plataforma de Gestão de Viagens
            </div>
            
            <h1 class="hero__title">
              Gerencie suas
              <span class="hero__title-highlight">milhas</span>
              e viagens com
              <span class="hero__title-highlight">inteligência</span>
            </h1>
            
            <p class="hero__description">
              Acompanhe programas de fidelidade, organize clientes e controle 
              todas as suas viagens em uma única plataforma completa.
            </p>
            
            <div class="hero__actions">
              <a routerLink="/login" class="btn btn--primary">
                Acessar Plataforma
              </a>
              <a href="#features" class="btn btn--secondary">
                Conhecer Recursos
              </a>
            </div>
            
            <div class="hero__stats">
              <div class="hero__stat">
                <span class="hero__stat-number">10k+</span>
                <span class="hero__stat-label">Clientes Ativos</span>
              </div>
              <div class="hero__stat-divider"></div>
              <div class="hero__stat">
                <span class="hero__stat-number">50k+</span>
                <span class="hero__stat-label">Viagens Gerenciadas</span>
              </div>
              <div class="hero__stat-divider"></div>
              <div class="hero__stat">
                <span class="hero__stat-number">100M+</span>
                <span class="hero__stat-label">Milhas Rastreadas</span>
              </div>
            </div>
          </div>
        </div>
        
      </section>

      <!-- Features Section -->
      <section id="features" class="features">
        <div class="features__container">
          <div class="features__header">
            <span class="section-badge">Recursos</span>
            <h2 class="section-title">Tudo que você precisa em um só lugar</h2>
            <p class="section-description">
              Ferramentas poderosas para gerenciar milhas, clientes e viagens com eficiência
            </p>
          </div>
          
          <div class="features__grid">
            <div class="feature-card">
              <h3 class="feature-card__title">Gestão de Milhas</h3>
              <p class="feature-card__description">
                Controle todos os seus programas de fidelidade em um só lugar. 
                Acompanhe saldos, transferências e datas de expiração.
              </p>
            </div>
            
            <div class="feature-card">
              <h3 class="feature-card__title">Cadastro de Clientes</h3>
              <p class="feature-card__description">
                Mantenha seus clientes organizados com documentos, 
                contatos e histórico de viagens completos.
              </p>
            </div>
            
            <div class="feature-card">
              <h3 class="feature-card__title">Controle de Viagens</h3>
              <p class="feature-card__description">
                Registre e acompanhe todas as viagens dos seus clientes 
                com detalhes de voos, hotéis e documentação.
              </p>
            </div>
            
            <div class="feature-card">
              <h3 class="feature-card__title">Relatórios e Análises</h3>
              <p class="feature-card__description">
                Visualize estatísticas e relatórios detalhados sobre 
                milhas, viagens e performance dos clientes.
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- How It Works -->
      <section id="how-it-works" class="how-it-works">
        <div class="how-it-works__container">
          <div class="how-it-works__header">
            <span class="section-badge">Como Funciona</span>
            <h2 class="section-title">Comece em poucos minutos</h2>
          </div>
          
          <div class="steps">
            <div class="step">
              <div class="step__number">01</div>
              <h3 class="step__title">Crie sua conta</h3>
              <p class="step__description">
                Faça login na plataforma com seu email e senha
              </p>
            </div>
            
            <div class="step__arrow">→</div>
            
            <div class="step">
              <div class="step__number">02</div>
              <h3 class="step__title">Cadastre clientes</h3>
              <p class="step__description">
                Adicione seus clientes com documentos e contatos
              </p>
            </div>
            
            <div class="step__arrow">→</div>
            
            <div class="step">
              <div class="step__number">03</div>
              <h3 class="step__title">Gerencie viagens</h3>
              <p class="step__description">
                Controle milhas, voos e todo o histórico
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta">
        <div class="cta__background">
          <img src="https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1920&q=80" alt="Beach destination">
        </div>
        <div class="cta__overlay"></div>
        <div class="cta__container">
          <h2 class="cta__title">Pronto para organizar suas viagens?</h2>
          <p class="cta__description">
            Acesse agora mesmo nossa plataforma e descubra como gerenciar 
            milhas e viagens pode ser simples e eficiente.
          </p>
          <a routerLink="/login" class="btn btn--primary btn--large">
            Acessar Plataforma
          </a>
        </div>
      </section>

      <!-- Footer -->
      <footer class="footer">
        <div class="footer__container">
          <div class="footer__brand">
            <a routerLink="/landing" class="footer__logo">
              <img src="images/vn-group.png" alt="Vá Nessa Viagem">
            </a>
            <p class="footer__tagline">
              Sua viagem começa aqui. Gerencie milhas e viagens de forma inteligente.
            </p>
          </div>
          
          <div class="footer__links">
            <div class="footer__column">
              <h4>Plataforma</h4>
              <a routerLink="/login">Login</a>
              <a href="#features">Recursos</a>
              <a href="#how-it-works">Como Funciona</a>
            </div>
            <div class="footer__column">
              <h4>Contato</h4>
              <span>contato&#64;vanessaviagem.com.br</span>
              <span>+55 (11) 99999-9999</span>
            </div>
          </div>
        </div>
        
        <div class="footer__bottom">
          <p>&copy; 2024 Vá Nessa Viagem. Todos os direitos reservados.</p>
        </div>
      </footer>
    </div>
  `,
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {
  isScrolled = false;
  scrollY = 0;

  @HostListener('window:scroll')
  onScroll() {
    this.scrollY = window.scrollY;
    this.isScrolled = this.scrollY > 50;
  }
}
