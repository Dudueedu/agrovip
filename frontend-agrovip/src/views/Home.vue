<template>
  <div class="home-view">
    <div class="home-header">
      <h1>Acesso Rápido</h1>
      <p class="home-subtitle">Selecione uma das principais áreas da sua loja.</p>
    </div>
    
    <div class="shortcut-grid">
      <RouterLink to="/vendas/cadastrar" class="shortcut-card">
        <div class="card-icon" style="background-color: var(--cor-secundaria); color: var(--cor-primaria);">🛒</div>
        <div class="card-content">
          <h3>Frente de Caixa</h3>
          <p>Iniciar nova venda.</p>
        </div>
        <div class="card-arrow">→</div>
      </RouterLink>

      <RouterLink to="/estoque" class="shortcut-card">
        <div class="card-icon" style="background-color: #E3F2FD; color: #1E88E5;">📦</div>
        <div class="card-content">
          <h3>Gerenciar Estoque</h3>
          <p>Verificar estoque.</p>
        </div>
        <div class="card-arrow">→</div>
      </RouterLink>

      <RouterLink to="/clientes" class="shortcut-card">
        <div class="card-icon" style="background-color: #FFF3E0; color: #FB8C00;">👥</div>
        <div class="card-content">
          <h3>Cadastros</h3>
          <p>Clientes.</p>
        </div>
        <div class="card-arrow">→</div>
      </RouterLink>

      <RouterLink to="admin/dashboard" class="shortcut-card">
        <div class="card-icon" style="background-color: #F3E5F5; color: #8E24AA;">💰</div>
        <div class="card-content">
          <h3>Financeiro</h3>
          <p>Administração.</p>
        </div>
        <div class="card-arrow">→</div>
      </RouterLink>
    </div>
  </div>
</template>

<script>
import { RouterLink } from 'vue-router'
export default {
  name: 'HomeView',
  components: { RouterLink }
}
</script>

<style scoped>

.home-header {
  text-align: center;
  margin-bottom: 30px;
}

.home-header h1 {
  font-size: 1.8rem;
  margin-bottom: 5px;
  color: var(--cor-texto-principal);
}

.home-header .home-subtitle {
  font-size: 1.1rem;
  color: var(--cor-texto-secundario);
  margin: 0;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); 
  gap: 25px;
  
  max-width: 950px; 
  margin: 0 auto; 
}

@media (max-width: 900px) {
  .shortcut-grid {
    grid-template-columns: 1fr;
  }
}

.shortcut-card {
  display: flex;
  align-items: center;
  background-color: var(--cor-fundo-card); 
  padding: 25px;
  border-radius: var(--raio-borda);
  border: 1px solid var(--cor-borda);
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  text-decoration: none;
  color: var(--cor-texto-principal);
  transition: all 0.3s ease;
}

.shortcut-card:hover {
  transform: translateY(-5px); 
  box-shadow: var(--sombra-card); 
  text-decoration: none;
  border-color: var(--cor-primaria);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--raio-borda);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  margin-right: 20px;
  flex-shrink: 0;
}

.card-content {
  flex-grow: 1;
}
.card-content h3 {
  font-size: 1.2rem;
  margin: 0 0 5px 0;
}
.card-content p {
  margin: 0;
  color: var(--cor-texto-secundario);
  font-size: 0.9rem;
}

.card-arrow {
  font-size: 1.5rem;
  color: var(--cor-borda);
  transition: all 0.3s ease;
}

.shortcut-card:hover .card-arrow {
  color: var(--cor-primaria);
  transform: translateX(5px);
}
</style>