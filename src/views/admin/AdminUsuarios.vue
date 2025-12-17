<template>
  <div class="gerenciar-view">
    <div class="form-card">
      <div class="form-header">
        <h2 v-if="isEditing">Editar Usuário (ID: {{ usuario.id }})</h2>
        <h2 v-else>Cadastrar Novo Usuário</h2>
      </div>

      <form @submit.prevent="salvarUsuario">
        <div class="form-grid">
          <div class="form-group span-2">
            <label for="nome" class="form-label">Nome Completo</label>
            <input type="text" id="nome" class="form-input" v-model="usuario.nome" required>
          </div>
          <div class="form-group">
            <label for="login" class="form-label">Login (para entrar)</label>
            <input type="text" id="login" class="form-input" v-model="usuario.login" required>
          </div>
          <div class="form-group">
            <label for="senha" class="form-label">Senha</label>
            <input type="password" id="senha" class="form-input" v-model="usuario.senha" :required="!isEditing">
          </div>
          <div class="form-group span-2">
            <label for="role" class="form-label">Tipo de Usuário (Permissão)</label>
            <select id="role" class="form-input" v-model="usuario.role" required>
              <option value="VENDEDOR">Vendedor</option>
              <option value="ADMIN">Administrador</option>
            </select>
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
            {{ isEditing ? 'Atualizar Usuário' : 'Salvar Novo Usuário' }}
          </button>
        </div>
      </form>
    </div>

    <div class="list-card">
      <div class="list-header">
        <h2>Usuários Cadastrados</h2>
      </div>
      <table class="data-table" v-if="usuarios.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Login</th>
            <th>Permissão (Role)</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in usuarios" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.nome }}</td>
            <td>{{ u.login }}</td>
            <td>
              <span class="status-chip" :class="u.role === 'ADMIN' ? 'chip-vermelho' : 'chip-verde'">
                {{ u.role }}
              </span>
            </td>
            <td>
              <button @click="iniciarEdicao(u)" class="btn-acao">Editar</button>
              <button @click="excluirUsuario(u)" class="btn-acao btn-excluir">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

const usuarioVazio = {
  nome: '',
  login: '',
  senha: '',
  role: 'VENDEDOR'
};

export default {
  name: 'AdminUsuarios',
  data() {
    return {
      usuarios: [],
      usuario: { ...usuarioVazio },
      isEditing: false,
      isLoading: false,
      mensagem: { texto: '', tipo: '' }
    }
  },
  mounted() {
    this.carregarUsuarios();
  },
  methods: {
    async carregarUsuarios() {
      this.isLoading = true;
      try {
        const response = await api.get('/usuarios');
        this.usuarios = response.data;
      } catch (err) {
        this.mensagem.texto = 'Erro ao carregar usuários.';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },
    async salvarUsuario() {
      this.isLoading = true;
      this.mensagem.texto = '';
      try {
        if (this.isEditing) {
          await api.put(`/usuarios/${this.usuario.id}`, this.usuario);
          this.mensagem.texto = 'Usuário atualizado com sucesso!';
          this.mensagem.tipo = 'sucesso';
        } else {
          await api.post('/usuarios', this.usuario);          
          this.mensagem.texto = 'Usuário salvo com sucesso!';
          this.mensagem.tipo = 'sucesso';
        }
        this.limparFormulario();
        await this.carregarUsuarios();
      } catch (err) {
        this.mensagem.texto = 'Erro ao salvar o usuário.';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },
    iniciarEdicao(usuario) {
      this.usuario = { ...usuario, senha: '' }; 
      this.isEditing = true;
      window.scrollTo(0, 0);
    },
    async excluirUsuario(usuario) {
      if (!confirm(`Tem certeza que deseja excluir o usuário "${usuario.nome}"?`)) return;
      this.isLoading = true;
      this.mensagem.texto = '';
      try {
        await api.delete(`/usuarios/${usuario.id}`);
        this.mensagem.texto = 'Usuário excluído com sucesso!';
        this.mensagem.tipo = 'sucesso';
        await this.carregarUsuarios();
        if (this.usuario.id === usuario.id) this.limparFormulario(); 
      } catch (err) {
        this.mensagem.texto = 'Erro ao excluir o usuário. Vendas já foram registradas com este usuário.';
        this.mensagem.tipo = 'erro';
      } finally {
        this.isLoading = false;
      }
    },
    limparFormulario() {
      this.usuario = { ...usuarioVazio };
      this.isEditing = false;
      this.mensagem.texto = '';
    }
  }
}
</script>

<style scoped>
.form-card { background-color: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 30px; box-shadow: var(--sombra-card); width: 100%; margin-bottom: 30px; }
.form-header { margin-bottom: 30px; border-bottom: 1px solid var(--cor-borda); padding-bottom: 15px; }
.form-header h2 { margin: 0; }
.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px 25px; }
.form-group.span-2 { grid-column: span 2; }
@media (max-width: 768px) { .form-grid { grid-template-columns: 1fr; } .form-group.span-2 { grid-column: span 1; } }
.form-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 30px; padding-top: 20px; border-top: 1px solid var(--cor-borda); }
.mensagem { padding: 12px 15px; border-radius: var(--raio-borda); margin-top: 20px; text-align: center; font-weight: 500; }
.mensagem.sucesso { background-color: var(--cor-secundaria); color: var(--cor-primaria); border: 1px solid var(--cor-primaria); }
.mensagem.erro { background-color: #ffebee; color: var(--cor-erro); border: 1px solid var(--cor-erro); }
.list-card { background-color: var(--cor-fundo-card); border-radius: var(--raio-borda); padding: 30px; box-shadow: var(--sombra-card); width: 100%; }
.list-header { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid var(--cor-borda); }
.list-header h2 { margin: 0; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid var(--cor-borda); }
.data-table th { background-color: var(--cor-fundo-app); color: var(--cor-texto-secundario); font-size: 0.9rem; text-transform: uppercase; }
.status-chip { padding: 4px 10px; border-radius: 12px; font-weight: 700; font-size: 0.8rem; color: #fff; text-transform: uppercase; }
.chip-verde { background-color: var(--cor-primaria); }
.chip-vermelho { background-color: var(--cor-erro); }
.btn-acao { background-color: transparent; border: none; cursor: pointer; color: var(--cor-primaria); font-weight: 700; margin-right: 10px; }
.btn-excluir { color: #db4444; margin-left: 4px; }
.btn-excluir:hover { text-decoration: underline; color: #a61111; }
.loading-message, .empty-message { text-align: center; padding: 40px; color: var(--cor-texto-secundario); font-size: 1.1rem; }
</style>