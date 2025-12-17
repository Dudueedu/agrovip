<template>
  <div class="gerenciar-clientes-view">
    
    <div class="form-card">
      <div class="form-header">
        <h2 v-if="isEditing">Editar Cliente (ID: {{ cliente.id }})</h2>
        <h2 v-else>Cadastrar Novo Cliente</h2>
      </div>

      <form @submit.prevent="salvarCliente">
        <div class="form-grid">
          <div class="form-group span-2">
            <label for="nome" class="form-label">Nome Completo</label>
            <input 
              type="text" 
              id="nome" 
              class="form-input" 
              placeholder="Ex: João da Silva"
              v-model="cliente.nome"
              required
            >
          </div>
          <div class="form-group">
            <label for="telefone" class="form-label">Telefone / WhatsApp</label>
            <input 
              type="text" 
              id="telefone" 
              class="form-input" 
              placeholder="(19) 99999-9999"
              v-model="cliente.telefone"
            >
          </div>
          <div class="form-group">
            <label for="email" class="form-label">E-mail</label>
            <input 
              type="email" 
              id="email" 
              class="form-input" 
              placeholder="email@exemplo.com"
              v-model="cliente.email"
            >
          </div>
        </div>

        <div v-if="mensagem.texto" :class="['mensagem', mensagem.tipo]">
          {{ mensagem.texto }}
        </div>

        <div class="form-actions">
          <button v-if="isEditing" @click="limparFormulario" type="button" class="btn btn-secondary">
            Cancelar Edição
          </button>
          <button type="submit" class="btn btn-primary" :disabled="isLoading">
            {{ isEditing ? 'Atualizar Cliente' : 'Salvar Novo Cliente' }}
          </button>
        </div>
      </form>
    </div> 

    <div class="list-card">
      <div class="list-header">
        <h2>Clientes Cadastrados</h2>
        <button @click="carregarClientes" class="btn btn-secondary" :disabled="isLoading">
          Atualizar Lista
        </button>
      </div>

      <div v-if="isLoading" class="loading-message">Carregando clientes...</div>
      <div v-if="errorApi" class="mensagem erro">{{ errorApi }}</div>

      <table class="data-table" v-if="clientes.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Telefone</th>
            <th>E-mail</th>
            
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in clientes" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.nome }}</td>
            <td>{{ c.telefone }}</td>
            <td>{{ c.email }}</td>
           
          </tr>
        </tbody>
      </table>
      <div v-if="!isLoading && clientes.length === 0" class="empty-message">
        Nenhum cliente cadastrado ainda.
      </div>
    </div>

  </div>
</template>

<script>
import api from '@/services/api';

const clienteVazio = {
  id: null,
  nome: '',
  telefone: '',
  email: ''
};

export default {
  name: 'GerenciarClientes',
  data() {
    return {
      clientes: [], 
      cliente: { ...clienteVazio }, 
      isEditing: false,
      isLoading: false,
      errorApi: null,
      mensagem: { texto: '', tipo: '' }
    }
  },
  
  mounted() {
    this.carregarClientes();
  },

  methods: {
    async carregarClientes() {
      this.isLoading = true;
      this.errorApi = null;
      try {
        const response = await api.get('/clientes');
        this.clientes = response.data;
      } catch (err) {
        this.errorApi = 'Erro ao carregar clientes.';
      } finally {
        this.isLoading = false;
      }
    },

    async salvarCliente() {
      this.isLoading = true;
      this.mensagem.texto = '';

      try {
        if (this.isEditing) {
          await api.put(`/clientes/${this.cliente.id}`, this.cliente);
          this.mensagem.texto = 'Cliente atualizado com sucesso!';
          this.mensagem.tipo = 'sucesso';
          
        } else {
          await api.post('/clientes', this.cliente);
          this.mensagem.texto = 'Cliente salvo com sucesso!';
          this.mensagem.tipo = 'sucesso';
        }
        
        this.limparFormulario(); 
        await this.carregarClientes(); 

      } catch (err) {
        this.mensagem.texto = 'Erro ao salvar. Verifique os dados.';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },

    iniciarEdicao(cliente) {
      this.cliente = { ...cliente }; 
      this.isEditing = true;
      window.scrollTo(0, 0); 
    },

    limparFormulario() {
      this.cliente = { ...clienteVazio };
      this.isEditing = false;
      this.mensagem.texto = '';
    }
  }
}
</script>

<style scoped>
.form-card {
  background-color: var(--cor-fundo-card);
  border-radius: var(--raio-borda);
  padding: 30px;
  box-shadow: var(--sombra-card);
  width: 100%;
  margin-bottom: 30px; 
}
.form-header {
  margin-bottom: 30px;
  border-bottom: 1px solid var(--cor-borda);
  padding-bottom: 15px;
}
.form-header h2 { margin: 0; }
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px 25px;
}
.form-group.span-2 { grid-column: span 2; }
@media (max-width: 768px) {
  .form-grid { grid-template-columns: 1fr; }
  .form-group.span-2 { grid-column: span 1; }
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--cor-borda);
}
.mensagem {
  padding: 12px 15px;
  border-radius: var(--raio-borda);
  margin-top: 20px;
  text-align: center;
  font-weight: 500;
}
.mensagem.sucesso {
  background-color: var(--cor-secundaria);
  color: var(--cor-primaria);
  border: 1px solid var(--cor-primaria);
}
.mensagem.erro {
  background-color: #ffebee;
  color: var(--cor-erro);
  border: 1px solid var(--cor-erro);
}

.list-card {
  background-color: var(--cor-fundo-card);
  border-radius: var(--raio-borda);
  padding: 30px;
  box-shadow: var(--sombra-card);
  width: 100%;
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--cor-borda);
}
.list-header h2 { margin: 0; }
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th,
.data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid var(--cor-borda);
}
.data-table th {
  background-color: var(--cor-fundo-app);
  color: var(--cor-texto-secundario);
  font-size: 0.9rem;
  text-transform: uppercase;
}
.data-table td { color: var(--cor-texto-principal); }
.data-table tr:hover { background-color: var(--cor-secundaria); }
.btn-acao {
  background-color: transparent;
  border: none;
  cursor: pointer;
  color: var(--cor-primaria);
  font-weight: 700;
  margin-right: 10px;
}
.btn-acao.btn-excluir { color: var(--cor-erro); }
.loading-message, .empty-message {
  text-align: center;
  padding: 40px;
  color: var(--cor-texto-secundario);
  font-size: 1.1rem;
}
</style>