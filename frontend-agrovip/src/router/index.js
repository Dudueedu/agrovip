import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '../views/Login.vue'
import DefaultLayout from '../layouts/DefaultLayout.vue'
import HomeView from '../views/Home.vue'

import GerenciarClientes from '../views/GerenciarClientes.vue' 
import CadastrarVenda from '../views/CadastrarVenda.vue'
import VerEstoque from '../views/VerEstoque.vue'

import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminUsuarios from '../views/admin/AdminUsuarios.vue'
import AdminProdutos from '../views/admin/AdminProdutos.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView 
  },
  {
    path: '/',
    component: DefaultLayout,
    redirect: '/home', 
    children: [
      { path: '/home', name: 'home', component: HomeView },
      
      { 
        path: '/clientes', 
        name: 'gerenciar-clientes', 
        component: GerenciarClientes 
      },
      { 
        path: '/vendas/cadastrar', 
        name: 'cadastrar-venda', 
        component: CadastrarVenda 
      },
      { 
        path: '/estoque', 
        name: 'ver-estoque', 
        component: VerEstoque 
      },

      { 
        path: '/admin/dashboard', 
        name: 'admin-dashboard', 
        component: AdminDashboard,
        meta: { requiresAdmin: true }
      },
      { 
        path: '/admin/usuarios', 
        name: 'admin-usuarios', 
        component: AdminUsuarios,
        meta: { requiresAdmin: true }
      },
      { 
        path: '/admin/produtos', 
        name: 'admin-produtos', 
        component: AdminProdutos,
        meta: { requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAdmin)) {
    const usuarioRole = localStorage.getItem('usuarioRole')
    if (usuarioRole === 'ADMIN') {
      next() 
    } else {
      next('/home') 
    }
  } else {
    next()
  }
})

export default router