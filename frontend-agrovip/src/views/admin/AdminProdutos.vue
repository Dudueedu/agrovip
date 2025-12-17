<template>
  <div class="gerenciar-view">
    
    <div class="form-card">
      <div class="form-header">
        <h2 v-if="isEditing">Editar Produto (ID: {{ produto.id }})</h2>
        <h2 v-else>Cadastrar Novo Produto</h2>
      </div>

      <form @submit.prevent="salvarProduto">
        <div class="form-grid">
          <div class="form-group span-2">
            <label for="nome" class="form-label">Nome do Produto</label>
            <input type="text" id="nome" class="form-input" v-model="produto.nome" required>
          </div>
          <div class="form-group">
            <label for="precoVenda" class="form-label">Preço de Venda (R$)</label>
            <input type="number" step="0.01" id="precoVenda" class="form-input" v-model.number="produto.precoVenda" required>
          </div>
          <div class="form-group">
            <label for="estoque" class="form-label">Quantidade em Estoque</label>
            <input type="number" id="estoque" class="form-input" v-model.number="produto.quantidadeEstoque" required>
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
            {{ isEditing ? 'Atualizar Produto' : 'Salvar Novo Produto' }}
          </button>
        </div>
      </form>
    </div>

    <div class="list-card">
      <div class="list-header">
        <h2>Produtos Cadastrados</h2>
        <button @click="carregarProdutos" class="btn btn-secondary" :disabled="isLoading">
          Atualizar Lista
        </button>
      </div>

      <div v-if="isLoading" class="loading-message">Carregando produtos...</div>
      <div v-if="errorApi" class="mensagem erro">{{ errorApi }}</div>

      <table class="data-table" v-if="produtos.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Venda</th>
            <th>Estoque</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in produtos" :key="p.id">
            <td>{{ p.id }}</td>
            <td>{{ p.nome }}</td>
            <td>R$ {{ (p.precoVenda ?? 0).toFixed(2) }}</td>
            <td>{{ p.quantidadeEstoque }}</td>
            <td>
              <button @click="iniciarEdicao(p)" class="btn-acao">Editar</button>
              <button @click="excluirProduto(p)" class="btn-acao btn-excluir">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

  </div>
</template>

<script>
import api from '@/services/api';

const produtoVazio = {
  id: null,
  nome: '',
  precoVenda: 0,
  quantidadeEstoque: 0
};

export default {
  name: 'AdminProdutos',
  data() {
    return {
      produtos: [],
      produto: { ...produtoVazio },
      isEditing: false,
      isLoading: false,
      errorApi: null,
      mensagem: { texto: '', tipo: '' }
    }
  },
  mounted() {
    this.carregarProdutos();
  },
  methods: {
    async carregarProdutos() {
      this.isLoading = true;
      this.errorApi = null;
      try {
        const response = await api.get('/produtos');
        this.produtos = response.data;
      } catch (err) {
        console.error(err);
        this.errorApi = 'Erro ao carregar produtos.';
      } finally {
        this.isLoading = false;
      }
    },

    async salvarProduto() {
      this.isLoading = true;
      this.mensagem.texto = '';
      try {
        if (this.isEditing) {
          await api.put(`/produtos/${this.produto.id}`, this.produto);
          
          this.mensagem.texto = 'Produto atualizado com sucesso!';
          this.mensagem.tipo = 'sucesso';
        } else {
          await api.post('/produtos', this.produto);
          
          this.mensagem.texto = 'Produto salvo com sucesso!';
          this.mensagem.tipo = 'sucesso';
        }
        
        this.limparFormulario();
        await this.carregarProdutos();
        
      } catch (err) {
        console.error(err);
        this.mensagem.texto = 'Erro ao salvar o produto. Verifique o Back-End.';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },

    async excluirProduto(produto) {
      if (!confirm(`Tem certeza que deseja excluir o produto "${produto.nome}"?`)) return;
      this.isLoading = true;
      this.mensagem.texto = '';
      try {
        await api.delete(`/produtos/${produto.id}`);
        
        this.mensagem.texto = 'Produto excluído com sucesso!';
        this.mensagem.tipo = 'sucesso';
        
        await this.carregarProdutos();
        if (this.produto.id === produto.id) this.limparFormulario();
        
      } catch (err) {
        console.error(err);
        this.mensagem.texto = 'Erro ao excluir o produto. Já cadastrada uma venda com este produto!';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },

    iniciarEdicao(produto) {
      this.produto = { ...produto }; 
      this.isEditing = true;
      window.scrollTo(0, 0); 
    },

    limparFormulario() {
      this.produto = { ...produtoVazio };
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
.form-header { margin-bottom: 30px; border-bottom: 1px solid var(--cor-borda); padding-bottom: 15px; }
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
.form-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 30px; padding-top: 20px; border-top: 1px solid var(--cor-borda); }
.mensagem { padding: 12px 15px; border-radius: var(--raio-borda); margin-top: 20px; text-align: center; font-weight: 500; }
.mensagem.sucesso { background-color: var(--cor-secundaria); color: var(--cor-primaria); border: 1px solid var(--cor-primaria); }
.mensagem.erro { background-color: #ffebee; color: var(--cor-erro); border: 1px solid var(--cor-erro); }
.list-card { background-color: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 30px; box-shadow: var(--sombra-card); width: 100%; }
.list-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid var(--cor-borda); }
.list-header h2 { margin: 0; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid var(--cor-borda); }
.data-table th { background-color: var(--cor-fundo-app); color: var(--cor-texto-secundario); font-size: 0.9rem; text-transform: uppercase; }
.btn-acao { background-color: transparent; border: none; cursor: pointer; color: var(--cor-primaria); font-weight: 700; margin-right: 10px; }
.loading-message, .empty-message { text-align: center; padding: 40px; color: var(--cor-texto-secundario); font-size: 1.1rem; }

</style>