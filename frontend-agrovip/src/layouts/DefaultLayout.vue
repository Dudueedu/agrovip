<template>

  <div class="app-layout">

   

    <aside class="sidebar">

      <div class="sidebar-logo">

        <div class="logo-placeholder-app"><img src="@/assets/logo.png" alt="Logo Agrovip" class="login-logo"></div>

      </div>

     

      <nav class="sidebar-nav">

        <RouterLink to="/home" class="nav-link">

          <span>🏠</span>

          <span class="nav-text">Home</span>

        </RouterLink>

       

        <div class="nav-title">Operações</div>

        <RouterLink to="/vendas/cadastrar" class="nav-link">

          <span>🛒</span>

          <span class="nav-text">Cadastrar Venda</span>

        </RouterLink>

        <RouterLink to="/estoque" class="nav-link">

          <span>📦</span>

          <span class="nav-text">Ver Estoque</span>

        </RouterLink>



        <div class="nav-title">Gerenciamento</div>

        <RouterLink to="/clientes" class="nav-link">

          <span>👥</span>

          <span class="nav-text">Clientes</span>

        </RouterLink>



        <div class="nav-title">Administrador</div>

        <RouterLink to="/admin/dashboard" class="nav-link">

          <span>📊</span>

          <span class="nav-text">Dashboard</span>

        </RouterLink>

        <RouterLink to="/admin/produtos" class="nav-link">

          <span>🌾</span>

          <span class="nav-text">Produtos</span>

        </RouterLink>

        <RouterLink to="/admin/usuarios" class="nav-link">

          <span>🔑</span>

          <span class="nav-text">Usuários</span>

        </RouterLink>

      </nav>

     

      <div class="sidebar-footer">

        <RouterLink to="/login" class="nav-link" @click="fazerLogout">

          <span>🚪</span>

          <span class="nav-text">Sair</span>

        </RouterLink>

      </div>

    </aside>

   

    <div class="main-container">

      <header class="app-header">

        <h2>{{ $route.name }}</h2> <div class="header-profile">

          <span>{{nomeUsuario}}</span>

          <div class="profile-avatar">{{ iniciaisUsuario }}</div>

        </div>

      </header>

      <main class="app-content">

        <RouterView />

      </main>

    </div>

  </div>

</template>



<script>

import { RouterLink, RouterView } from 'vue-router'

export default {

  name: 'DefaultLayout',

  components: { RouterLink, RouterView },

  data() {

    return {

      nomeUsuario: 'Carregando...',

      iniciaisUsuario: '?'

    }

  },

  mounted() {

    this.carregarDadosUsuario();

  },

  methods: {

    carregarDadosUsuario() {
      const nome = localStorage.getItem('usuarioNome');

      const iniciais = localStorage.getItem('usuarioIniciais');

      if (nome && iniciais) {

        this.nomeUsuario = nome;

        this.iniciaisUsuario = iniciais;

      } else {


        this.$router.push('/login');

      }

    },

    fazerLogout() {


      localStorage.removeItem('usuarioNome');

      localStorage.removeItem('usuarioIniciais');

    }

  }

}

</script>



<style scoped>

.app-layout { display: flex; min-height: 100vh; }

.sidebar {

  width: 260px;

  background-color: var(--cor-primaria);

  color: var(--cor-texto-branco);

  padding: 20px;

  display: flex;

  flex-direction: column;

  flex-shrink: 0;

}

.sidebar-logo { text-align: center; margin-bottom: 30px; }

.logo-placeholder-app {

  font-size: 1.8rem;

  font-weight: 700;

  border: 2px solid var(--cor-texto-branco);

  padding: 8px 15px;

  border-radius: var(--raio-borda);

  display: inline-block;

}

.sidebar-nav { flex-grow: 1; }

.nav-link {

  display: flex;

  align-items: center;

  padding: 12px 15px;

  margin-bottom: 8px;

  border-radius: var(--raio-borda);

  color: var(--cor-texto-branco);

  text-decoration: none;

  font-weight: 500;

  transition: background-color 0.3s ease;

}

.nav-link:hover { background-color: rgba(255, 255, 255, 0.1); }

.nav-link.router-link-exact-active {

  background-color: var(--cor-secundaria);

  color: var(--cor-primaria);

  font-weight: 700;

}

.nav-link span:first-child { font-size: 1.1rem; margin-right: 15px; }

.sidebar-footer { border-top: 1px solid rgba(255, 255, 255, 0.2); padding-top: 15px; }



/* ❗️ NOVO ESTILO ❗️ */

.nav-title {

  padding: 10px 15px;

  margin-top: 10px;

  font-size: 0.8rem;

  font-weight: 700;

  color: var(--cor-secundaria);

  text-transform: uppercase;

  opacity: 0.7;

}



.main-container {

  flex-grow: 1;

  display: flex;

  flex-direction: column;

  overflow-x: hidden;

}

.app-header {

  background-color: var(--cor-fundo-card);

  border-bottom: 1px solid var(--cor-borda);

  padding: 20px 30px;

  display: flex;

  justify-content: space-between;

  align-items: center;

  flex-shrink: 0;

}

.app-header h2 {

  margin: 0;

  text-transform: capitalize; /* Deixa o nome da rota bonito */

}

.header-profile { display: flex; align-items: center; }

.profile-info { text-align: right; margin-right: 15px; }

.profile-info strong { display: block; }

.profile-info span { font-size: 0.9rem; color: var(--cor-texto-secundario); }

.profile-avatar {

  width: 45px;

  height: 45px;

  border-radius: 50%;

  background-color: var(--cor-primaria);

  color: var(--cor-texto-branco);

  display: flex;

  justify-content: center;

  align-items: center;

  font-weight: 700;

  font-size: 1.2rem;

}

.app-content {

  padding: 30px;

  flex-grow: 1;

  overflow-y: auto;

  background-color: var(--cor-fundo-app);

}

.login-logo {
  max-width: 220px;
  width: 100%;
  height: auto;
  display: block;
}

</style>