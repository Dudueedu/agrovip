<template>
  <div class="login-page">
    
    <div class="login-branding">
      <img src="@/assets/logo.png" alt="Logo Agrovip" class="login-logo">
      <h1>A gestão completa para sua agropecuária.</h1>
      <p>Controle seu estoque, vendas e clientes em um só lugar.</p>
    </div>
    
    <div class="login-form-area">
      <div class="form-wrapper">
        <h2>Acessar o Sistema</h2>
        <p class="subtitle">Bem-vindo de volta! Por favor, insira seus dados.</p>
        
        <form @submit.prevent="fazerLogin">
          
          <div class="form-group">
            <label for="usuario" class="form-label">Usuário</label>
            <input 
              type="text" 
              id="usuario" 
              class="form-input" 
              placeholder="Seu nome de usuário"
              v-model="credenciais.login" required
            >
          </div>
          
          <div class="form-group">
            <label for="senha" class="form-label">Senha</label>
            <input 
              type="password" 
              id="senha" 
              class="form-input" 
              placeholder="Sua senha"
              v-model="credenciais.senha" required
            >
          </div>

          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <button type="submit" class="btn btn-primary btn-block" :disabled="isLoading">
            {{ isLoading ? 'Entrando...' : 'Entrar' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'LoginView',
  
  data() {
    return {
      credenciais: {
        login: '',
        senha: ''
      },
      error: null,
      isLoading: false
    }
  },

  methods: {
    async fazerLogin() {
      this.error = null;
      this.isLoading = true;

      try {
        const response = await api.post('/login', this.credenciais);
        const data = response.data; 

        localStorage.setItem('usuarioId', data.usuarioId); 
        localStorage.setItem('usuarioNome', data.nome);
        localStorage.setItem('usuarioIniciais', data.nome ? data.nome.charAt(0).toUpperCase() : '?');
        localStorage.setItem('usuarioRole', data.role) 

        this.$router.push('/');

      } catch (err) {
        if (err.response && err.response.data && err.response.data.message) {
          this.error = err.response.data.message;
        } else {
          this.error = "Não foi possível conectar ao servidor. O Back-End está rodando?";
        }
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.btn:disabled {
  background-color: var(--cor-texto-secundario);
  cursor: not-allowed;
  opacity: 0.7;
}

.login-page {
  display: flex;
  min-height: 100vh;
}
.login-branding {
  flex-basis: 50%;
  background-color: var(--cor-primaria);
  color: var(--cor-texto-branco);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 50px;
  @media (max-width: 900px) { display: none; }
}
.login-logo {
  max-width: 220px;
  height: auto;
  margin-bottom: 30px;
}
.login-branding h1 {
  font-size: 2.5rem;
  margin-bottom: 15px;
}
.login-branding p {
  font-size: 1.125rem;
  opacity: 0.9;
  max-width: 450px;
}
.login-form-area {
  flex-basis: 50%;
  background-color: var(--cor-fundo-card);
  display: flex;
  justify-content: center;
  align-items: center; 
  padding: 40px;
  @media (max-width: 900px) { flex-basis: 100%; }
}
.form-wrapper {
  width: 100%;
  max-width: 400px;
}
.form-wrapper h2 {
  font-size: 1.75rem;
  color: var(--cor-texto-principal);
  margin-bottom: 10px;
}
.form-wrapper .subtitle {
  color: var(--cor-texto-secundario);
  margin-bottom: 30px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group:last-of-type {
  margin-bottom: 20px; 
}
.error-message {
  color: var(--cor-erro);
  background-color: #ffebee;
  border: 1px solid var(--cor-erro);
  border-radius: var(--raio-borda);
  padding: 10px 15px;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 500;
}
</style>