<template>
  <div class="list-card">
    
    <div class="list-header">
      <h2>Estoque de Produtos</h2>
      <button @click="carregarProdutos" class="btn btn-secondary" :disabled="isLoading">
        Atualizar Lista
      </button>
    </div>

    <div v-if="isLoading" class="loading-message">
      Carregando produtos do estoque...
    </div>

    <div v-if="error" class="mensagem erro">
      {{ error }}
    </div>

    <table class="data-table" v-if="!isLoading && !error && produtos.length > 0">
      <thead>
        <tr>
          <th>ID</th>
          <th>Produto</th>
          <th>Preço de Venda</th>
          <th>Estoque Atual</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="produto in produtos" :key="produto.id" :class="{ 'estoque-baixo': produto.quantidadeEstoque <= 5, 'sem-estoque': produto.quantidadeEstoque === 0 }">
          <td>{{ produto.id }}</td>
          <td>{{ produto.nome }}</td>
          <td>R$ {{ produto.precoVenda.toFixed(2) }}</td>
          <td class="estoque-coluna">
            <strong>{{ produto.quantidadeEstoque }}</strong> unidades
          </td>
          <td>
            <span v-if="produto.quantidadeEstoque === 0" class="status-chip chip-vermelho">Sem Estoque</span>
            <span v-else-if="produto.quantidadeEstoque <= 5" class="status-chip chip-laranja">Estoque Baixo</span>
            <span v-else class="status-chip chip-verde">Em Estoque</span>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="!isLoading && !error && produtos.length === 0" class="empty-message">
      Nenhum produto cadastrado no sistema.
    </div>

  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'VerEstoque',
  
  data() {
    return {
      produtos: [], 
      isLoading: false,
      error: null
    }
  },

  mounted() {
    this.carregarProdutos();
  },

  methods: {
    async carregarProdutos() {
      this.isLoading = true;
      this.error = null;

      try {
        const response = await api.get('/produtos');        
        this.produtos = response.data; 

      } catch (err) {
        console.error('Erro ao carregar produtos:', err);
        this.error = 'Não foi possível carregar os produtos do estoque.';
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
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
.list-header h2 {
  margin: 0;
}

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

.data-table td {
  color: var(--cor-texto-principal);
}

.estoque-coluna strong {
  font-size: 1.1rem;
  color: var(--cor-primaria);
}

.status-chip {
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.8rem;
  color: #fff;
  text-transform: uppercase;
}
.chip-verde {
  background-color: var(--cor-primaria);
}
.chip-laranja {
  background-color: #FB8C00; 
}
.chip-vermelho {
  background-color: var(--cor-erro);
}

tr.estoque-baixo td {
  background-color: #FFF3E0; 
}
tr.sem-estoque td {
  background-color: #ffebee; 
  color: var(--cor-texto-secundario);
}
tr.sem-estoque .estoque-coluna strong {
  color: var(--cor-erro);
}

.loading-message, .empty-message {
  text-align: center;
  padding: 40px;
  color: var(--cor-texto-secundario);
  font-size: 1.1rem;
}
.mensagem.erro {
  background-color: #ffebee;
  color: var(--cor-erro);
  border: 1px solid var(--cor-erro);
  padding: 12px 15px;
  border-radius: var(--raio-borda);
  margin-bottom: 20px;
}
</style>