<template>
  <div class="venda-layout">
    
    <div class="venda-coluna-acoes">
      <div class="form-card">
        <div class="form-header">
          <h2>1. Selecionar Itens</h2>
        </div>
        
        <div v-if="mensagem.texto" :class="['mensagem', mensagem.tipo]" style="margin: -10px 0 20px 0;">
          {{ mensagem.texto }}
        </div>
        
        <form @submit.prevent="adicionarItem" class="form-grid-venda">
          <div class="form-group span-2">
            <label for="cliente" class="form-label">Cliente</label>
            <select id="cliente" class="form-input" v-model="venda.clienteId" required>
              <option :value="null" disabled>Selecione um cliente...</option>
              <option v-for="c in listaClientes" :key="c.id" :value="c.id">
                {{ c.nome }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="produto" class="form-label">Produto</label>
            <select id="produto" class="form-input" v-model="itemAtual.produto" required>
              <option :value="null" disabled>Selecione um produto...</option>
              <option v-for="p in listaProdutos" :key="p.id" :value="p" :disabled="p.quantidadeEstoque <= 0">
                {{ p.nome }} (Estoque: {{ p.quantidadeEstoque }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="quantidade" class="form-label">Quantidade</label>
            <input 
              type="number" 
              id="quantidade" 
              class="form-input" 
              v-model.number="itemAtual.quantidade"
              min="1"
              required
            >
          </div>
          
          <div class="form-actions span-2">
            <button type="submit" class="btn btn-primary btn-block">
              + Adicionar Item ao Carrinho
            </button>
          </div>
        </form>
      </div>

      <div class="list-card">
        <div class="list-header"><h2>2. Itens da Venda</h2></div>
        <table class="data-table">
          <thead>
            <tr>
              <th>Produto</th>
              <th>Qtd.</th>
              <th>Preço Unit.</th>
              <th>Subtotal</th>
              <th>Ação</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in venda.itens" :key="index">
              <td>{{ item.produto.nome }}</td>
              <td>{{ item.quantidade }}</td>
              <td>R$ {{ item.precoUnitario.toFixed(2) }}</td>
              <td>R$ {{ (item.quantidade * item.precoUnitario).toFixed(2) }}</td>
              <td>
                <button @click="removerItem(index)" class="btn-acao btn-excluir">
                  Remover
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="venda.itens.length === 0" class="empty-message">
          Nenhum item adicionado.
        </div>
      </div>
    </div>

    <div class="venda-coluna-resumo">
      <div class="form-card sticky-card">
        <div class="form-header"><h2>3. Resumo da Venda</h2></div>
        <div class="resumo-grid">
          <span>Subtotal</span>
          <span>R$ {{ totalVenda.toFixed(2) }}</span>
          
          <strong class="total">Total</strong>
          <strong class="total">R$ {{ totalVenda.toFixed(2) }}</strong>
        </div>

        <div class="form-actions">
          <button 
            @click="finalizarVenda" 
            class="btn btn-primary btn-block" 
            :disabled="venda.itens.length === 0 || isLoading || venda.clienteId === null"
          >
            {{ isLoading ? 'Finalizando...' : 'Finalizar Venda' }}
          </button>
        </div>
        <div v-if="mensagemFinal.texto" :class="['mensagem', mensagemFinal.tipo]">
          {{ mensagemFinal.texto }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'CadastrarVenda',
  data() {
    return {
      listaClientes: [],
      listaProdutos: [], 
      venda: {
        clienteId: null,
        itens: []
      },
      itemAtual: {
        produto: null,
        quantidade: 1
      },
      isLoading: false,
      mensagem: { texto: '', tipo: '' }, 
      mensagemFinal: { texto: '', tipo: '' } 
    }
  },

  computed: {
    totalVenda() {
      return this.venda.itens.reduce((total, item) => {
        return total + (item.quantidade * item.precoUnitario);
      }, 0);
    }
  },

  mounted() {
    this.carregarDadosIniciais();
  },

  methods: {
    async carregarDadosIniciais() {
      this.isLoading = true;
      this.mensagem.texto = '';
      try {
        const [resClientes, resProdutos] = await Promise.all([
          api.get('/clientes'),
          api.get('/produtos')
        ]);
        this.listaClientes = resClientes.data;
        this.listaProdutos = resProdutos.data;
      } catch (err) {
        this.mensagem.texto = 'Erro fatal ao carregar clientes ou produtos.';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },

    adicionarItem() {
      this.mensagem.texto = ''; 
      if (!this.itemAtual.produto || this.itemAtual.quantidade <= 0) {
        this.mensagem.texto = 'Selecione um produto e quantidade válida.';
        this.mensagem.tipo = 'erro';
        return;
      }

      const produtoSelecionado = this.itemAtual.produto;
      const quantidadeDesejada = this.itemAtual.quantidade;

      let qtdNoCarrinho = 0;
      this.venda.itens.forEach(item => {
        if (item.produto.id === produtoSelecionado.id) {
          qtdNoCarrinho += item.quantidade;
        }
      });
      const totalFinal = qtdNoCarrinho + quantidadeDesejada;

      if (totalFinal > produtoSelecionado.quantidadeEstoque) {
        this.mensagem.texto = `Estoque insuficiente! Você pode adicionar no máximo mais ${produtoSelecionado.quantidadeEstoque - qtdNoCarrinho} unidades.`;
        this.mensagem.tipo = 'erro';
        return;
      }
      
      this.venda.itens.push({
        produto: produtoSelecionado, 
        produtoId: produtoSelecionado.id, 
        quantidade: quantidadeDesejada,
        precoUnitario: produtoSelecionado.precoVenda 
      });

      this.itemAtual.produto = null;
      this.itemAtual.quantidade = 1;
    },

    removerItem(index) {
      this.venda.itens.splice(index, 1);
    },

    async finalizarVenda() {
      this.isLoading = true;
      this.mensagemFinal.texto = '';
      
      const clienteId = this.venda.clienteId;
      const vendedorId = localStorage.getItem('usuarioId'); 

      if (!vendedorId) {
         this.mensagemFinal.texto = 'Erro: Vendedor não identificado. Faça login novamente.';
         this.mensagemFinal.tipo = 'erro';
         this.isLoading = false;
         return;
      }

      const vendaParaSalvar = {
        usuario: { id: parseInt(vendedorId) }, 
        
        valorTotal: this.totalVenda, 

        itens: this.venda.itens.map(item => ({
          produto: { id: item.produtoId },
          quantidade: item.quantidade,
          precoUnitario: item.precoUnitario
        }))
        
      };

      try {
        const url = `/vendas/cliente/${clienteId}`;
        await api.post(url, vendaParaSalvar);
        
        this.mensagemFinal.texto = 'Venda finalizada com sucesso!';
        this.mensagemFinal.tipo = 'sucesso';
        this.limparVenda();
        await this.carregarDadosIniciais(); 

      } catch (err) {
        if (err.response && err.response.data && err.response.data.message) {
            this.mensagemFinal.texto = err.response.data.message;
        } else if (err.response && err.response.data) {
             this.mensagemFinal.texto = err.response.data.error || "Erro 500 no Back-End.";
        } else {
            this.mensagemFinal.texto = "Erro ao conectar.";
        }
        this.mensagemFinal.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },

    limparVenda() {
      this.venda = { clienteId: null, itens: [] };
      this.itemAtual = { produto: null, quantidade: 1 };
    }
  }
}
</script>

<style scoped>
.venda-layout { display: flex; gap: 30px; }
.venda-coluna-acoes { flex: 3; display: flex; flex-direction: column; gap: 30px; }
.venda-coluna-resumo { flex: 1; }
.sticky-card { position: sticky; top: 30px; }
.form-grid-venda { display: grid; grid-template-columns: 2fr 1fr; gap: 20px 25px; }
.form-group.span-2 { grid-column: span 2; }
.resumo-grid { display: grid; grid-template-columns: 1fr auto; gap: 15px; padding: 20px 0; font-size: 1.1rem; }
.resumo-grid .total { font-size: 1.5rem; font-weight: 700; color: var(--cor-primaria); }
.resumo-grid span:nth-child(odd) { font-weight: 500; color: var(--cor-texto-secundario); }
.resumo-grid span:nth-child(even) { font-weight: 700; text-align: right; }
.form-card, .list-card { background-color: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 30px; box-shadow: var(--sombra-card); width: 100%; }
.form-header, .list-header { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid var(--cor-borda); }
.form-header h2, .list-header h2 { margin: 0; }
.form-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 30px; padding-top: 20px; border-top: 1px solid var(--cor-borda); }
.btn-block { width: 100%; }
.mensagem { padding: 12px 15px; border-radius: var(--raio-borda); margin-top: 20px; text-align: center; font-weight: 500; }
.mensagem.sucesso { background-color: var(--cor-secundaria); color: var(--cor-primaria); border: 1px solid var(--cor-primaria); }
.mensagem.erro { background-color: #ffebee; color: var(--cor-erro); border: 1px solid var(--cor-erro); }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid var(--cor-borda); }
.data-table th { background-color: var(--cor-fundo-app); color: var(--cor-texto-secundario); font-size: 0.9rem; text-transform: uppercase; }
.btn-acao.btn-excluir { color: var(--cor-erro); cursor: pointer; background: none; border: none; font-weight: 700; padding: 0; }
.empty-message { text-align: center; padding: 40px; color: var(--cor-texto-secundario); }
</style>