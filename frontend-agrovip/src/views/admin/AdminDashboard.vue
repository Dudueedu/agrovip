<template>
  <div class="dashboard-view">
    <div v-if="isLoading" class="loading-message">Carregando dados do dashboard...</div>
    <div v-if="errorApi" class="mensagem erro">{{ errorApi }}</div>

    <div class="filtro-data" v-if="!isLoading && !errorApi">
      <label>
        De: <input type="date" v-model="dataInicio" />
      </label>
      <label>
        Até: <input type="date" v-model="dataFim" />
      </label>
    </div>

    <div class="kpi-grid" v-if="!isLoading && !errorApi">
      <div class="kpi-card">
        <span class="kpi-label">Total Faturado</span>
        <span class="kpi-value">R$ {{ kpi.totalFaturado.toFixed(2) }}</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-label">Total de Vendas</span>
        <span class="kpi-value">{{ kpi.totalVendas }}</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-label">Ticket Médio</span>
        <span class="kpi-value">R$ {{ kpi.ticketMedio.toFixed(2) }}</span>
      </div>
      <div class="kpi-card">
        <span class="kpi-label">Itens Vendidos</span>
        <span class="kpi-value">{{ kpi.totalItens }}</span>
      </div>
    </div>

    <div v-if="!isLoading && !errorApi">
      <div class="grafico-row">
        <div class="grafico-card">
          <h3>Vendas por Vendedor</h3>
          <PieChart :data="vendasPorVendedorData" style="height:300px"/>
        </div>
        <div class="grafico-card">
          <h3>Vendas por Produto</h3>
          <PieChart :data="vendasPorProdutoData" style="height:300px"/>
        </div>
        <div class="grafico-card" style="flex: 2;">
          <h3>Total Vendido por Dia</h3>
          <BarChart :data="faturamentoPorDiaData" style="height:300px"/>
        </div>
      </div>
    </div>

    <div class="list-card" v-if="!isLoading && !errorApi">
      <div class="list-header">
        <h2>Últimas Vendas Realizadas</h2>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th>ID Venda</th>
            <th>Data</th>
            <th>Cliente</th>
            <th>Vendedor</th>
            <th>Total (R$)</th>
            <th>Detalhes</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="venda in ultimasVendasFiltradas" :key="venda.id">
            <td>{{ venda.id }}</td>
            <td>{{ formatarData(venda.dataVenda) }}</td>
            <td>{{ venda.cliente.nome }}</td>
            <td>{{ venda.usuario.nome }}</td>
            <td class="total-venda">R$ {{ venda.valorTotal.toFixed(2) }}</td>
            <td>
            <button @click="abrirDetalhes(venda)" class="btn-detalhes">Ver Detalhes</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div v-if="mostrarPopup" class="popup-overlay" @click.self="fecharPopup">
  <div class="popup-modal">
    <h3>Produtos da Venda #{{ vendaSelecionada.id }}</h3>
    <table>
      <thead>
        <tr>
          <th>Produto</th>
          <th>Quantidade</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in vendaSelecionada.itens" :key="item.id">
          <td>{{ item.produto.nome }}</td>
          <td>{{ item.quantidade }}</td>
        </tr>
      </tbody>
    </table>
    <button @click="fecharPopup" class="btn-fechar">Fechar</button>
  </div>
</div>
</template>

<script>
//import axios from 'axios'
import api from '@/services/api';
import PieChart from '@/components/PieChart.vue'
import BarChart from '@/components/BarChart.vue'

export default {
  name: 'AdminDashboard',
  components: { PieChart, BarChart },
  data() {
    return {
      isLoading: false,
      errorApi: null,
      vendas: [],
      dataInicio: '',
      dataFim: '',
      kpi: {
        totalFaturado: 0,
        totalVendas: 0,
        ticketMedio: 0,
        totalItens: 0
      },
      vendaSelecionada: null,
      mostrarPopup: false
    }
  },
  computed: {
    vendasFiltradas() {
      // Filtro por data
      return this.vendas.filter(venda => {
        let data;
        if (Array.isArray(venda.dataVenda)) {
          data = new Date(venda.dataVenda[0], venda.dataVenda[1] - 1, venda.dataVenda[2]);
        } else {
          data = new Date(venda.dataVenda);
        }
        const inicio = this.dataInicio ? new Date(this.dataInicio) : null;
        const fim = this.dataFim ? new Date(this.dataFim) : null;
        if (inicio && data < inicio) return false;
        if (fim && data > fim) return false;
        return true;
      });
    },
    ultimasVendasFiltradas() {
      return [...this.vendasFiltradas].reverse().slice(0, 10);
    },
    vendasPorVendedorData() {
      const counts = {};
      this.vendasFiltradas.forEach(v => {
        const vendedor = v.usuario.nome || 'Desconhecido';
        counts[vendedor] = (counts[vendedor] || 0) + 1;
      });
      return {
        labels: Object.keys(counts),
        datasets: [{
          label: 'Vendas',
          data: Object.values(counts),
          backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4CAF50', '#8E24AA', '#FB8C00']
        }]
      };
    },
    vendasPorProdutoData() {
      const counts = {};
      this.vendasFiltradas.forEach(v => {
        if (v.itens) v.itens.forEach(item => {
          const produto = item.produto.nome || 'Desconhecido';
          counts[produto] = (counts[produto] || 0) + 1;
        });
      });
      return {
        labels: Object.keys(counts),
        datasets: [{
          label: 'Vendas',
          data: Object.values(counts),
          backgroundColor: ['#E3F2FD', '#FFF3E0', '#F3E5F5', '#FFCE56', '#4CAF50', '#FB8C00']
        }]
      };
    },
    faturamentoPorDiaData() {
      const dailyTotals = {};
      this.vendasFiltradas.forEach(v => {
        let dateStr;
        if (Array.isArray(v.dataVenda)) {
          dateStr = `${String(v.dataVenda[2]).padStart(2, '0')}/${String(v.dataVenda[1]).padStart(2, '0')}/${v.dataVenda[0]}`;
        } else {
          const d = new Date(v.dataVenda);
          dateStr = d.toLocaleDateString('pt-BR');
        }
        dailyTotals[dateStr] = (dailyTotals[dateStr] || 0) + (v.valorTotal || 0);
      });
      return {
        labels: Object.keys(dailyTotals),
        datasets: [{
          label: 'Total Vendido (R$)',
          data: Object.values(dailyTotals),
          backgroundColor: '#36A2EB'
        }]
      };
    }
  },
  mounted() {
    this.carregarVendas();
  },
  watch: {
    vendasFiltradas() {
      this.calcularKPIs();
    }
  },
  methods: {
    async carregarVendas() {
      this.isLoading = true;
      this.errorApi = null;
      try {
        //const response = await axios.get('http://localhost:8080/api/vendas');
        const response = await api.get('/vendas');
        this.vendas = response.data;
        this.calcularKPIs();
      } catch (err) {
        this.errorApi = "Erro ao carregar os dados das vendas.";
      } finally {
        this.isLoading = false;
      }
    },
    calcularKPIs() {
      const totalFaturado = this.vendasFiltradas.reduce((sum, venda) => sum + venda.valorTotal, 0);
      const totalVendas = this.vendasFiltradas.length;
      const totalItens = this.vendasFiltradas.reduce((sum, venda) =>
        sum + (venda.itens ? venda.itens.reduce((a, b) => a + b.quantidade, 0) : 0), 0);
      this.kpi.totalFaturado = totalFaturado;
      this.kpi.totalVendas = totalVendas;
      this.kpi.totalItens = totalItens;
      this.kpi.ticketMedio = totalVendas > 0 ? (totalFaturado / totalVendas) : 0;
    },
    formatarData(dataInput) {
      let data;
      if (Array.isArray(dataInput)) {
        data = new Date(
          dataInput[0],
          dataInput[1] - 1,
          dataInput[2],
          dataInput[3] || 0,
          dataInput[4] || 0,
          dataInput[5] || 0
        );
      } else if (typeof dataInput === 'string' && dataInput.includes(' ')) {
        data = new Date(dataInput.replace(' ', 'T'));
      } else {
        data = new Date(dataInput);
      }
      if (isNaN(data.getTime())) return "--";
      return data.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    abrirDetalhes(venda) {
    this.vendaSelecionada = venda;
    this.mostrarPopup = true;
  },
  fecharPopup() {
    this.vendaSelecionada = null;
    this.mostrarPopup = false;
  },
  }
}
</script>

<style scoped>
.filtro-data { display: flex; gap: 20px; margin-bottom: 20px; }
.grafico-row { display: flex; gap: 30px; margin-bottom: 40px; flex-wrap: wrap; }
.grafico-card { background: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 25px; box-shadow: var(--sombra-card); flex: 1; min-width: 280px; }
.grafico-card h3 { margin: 0 0 10px 0; font-size: 1.08rem; color: var(--cor-texto-principal);}
.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 30px; }
@media (max-width: 900px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .kpi-grid { grid-template-columns: 1fr; } .grafico-row { flex-direction: column; } }
.kpi-card { background-color: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 25px; box-shadow: var(--sombra-card); border-left: 5px solid var(--cor-primaria); }
.kpi-label { display: block; font-size: 0.9rem; color: var(--cor-texto-secundario); margin-bottom: 5px; text-transform: uppercase; }
.kpi-value { display: block; font-size: 2rem; font-weight: 700; color: var(--cor-texto-principal); }
.list-card { background-color: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 30px; box-shadow: var(--sombra-card); width: 100%; }
.list-header { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid var(--cor-borda); }
.data-table { width: 100%; border-collapse: collapse;}
.data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid var(--cor-borda);}
.data-table th { background-color: var(--cor-fundo-app); color: var(--cor-texto-secundario); font-size: 0.9rem; text-transform: uppercase;}
.data-table .total-venda { font-weight: 700; color: var(--cor-primaria);}
.loading-message, .empty-message { text-align: center; padding: 40px; color: var(--cor-texto-secundario); font-size: 1.1rem;}
.mensagem.erro { background-color: #ffebee; color: var(--cor-erro); border: 1px solid var(--cor-erro); padding: 12px 15px; border-radius: var(--raio-borda); margin-bottom: 20px;}
.popup-overlay {
  position: fixed;
  z-index: 1000;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}
.popup-modal {
  background: #fff;
  padding: 30px 25px;
  border-radius: 8px;
  min-width: 320px;
  max-width: 90vw;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
}
.btn-detalhes {
  padding: 6px 12px;
  border-radius: 5px;
  background: var(--cor-primaria);
  color: #fff;
  border: none;
  cursor: pointer;
}
.btn-fechar {
  margin-top: 18px;
  padding: 7px 17px;
  border-radius: 6px;
  border: none;
  color: #fff;
  background: #db4444;
  cursor: pointer;
  font-weight: 600;
}

.filtro-data {
  display: flex;
  gap: 22px;
  margin-bottom: 25px;
  align-items: center;
}

.filtro-data label {
  font-size: 1.05rem;
  font-weight: 500;
  color: var(--cor-texto-principal);
  display: flex;
  align-items: center;
  gap: 6px;
}

.filtro-data input[type="date"] {
  border: 1px solid var(--cor-borda);
  border-radius: 6px;
  padding: 7px 12px;
  font-size: 1rem;
  background: var(--cor-fundo-app);
  color: var(--cor-texto-principal);
  outline: none;
  width: 145px;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.filtro-data input[type="date"]:focus {
  border-color: var(--cor-primaria);
}

@media (max-width: 600px) {
  .filtro-data {
    flex-direction: column;
    align-items: start;
    gap: 12px;
  }
  .filtro-data input[type="date"] {
    width: 100%;
  }
}
</style>