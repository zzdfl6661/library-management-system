import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Home from '../views/Home.vue'
import BookSearch from '../views/BookSearch.vue'
import BookDetail from '../views/BookDetail.vue'
import CardManagement from '../views/CardManagement.vue'
import CardCreate from '../views/CardCreate.vue'
import CardReportLost from '../views/CardReportLost.vue'
import CardReissue from '../views/CardReissue.vue'
import CardCancel from '../views/CardCancel.vue'
import Borrow from '../views/Borrow.vue'
import Return from '../views/Return.vue'
import BookImport from '../views/BookImport.vue'
import MyBorrows from '../views/MyBorrows.vue'
import MyFines from '../views/MyFines.vue'

const routes = [
  { path: '/login', component: Login },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: '', component: Home },
      { path: 'books', component: BookSearch },
      { path: 'books/:id', component: BookDetail },
      { path: 'cards', component: CardManagement, meta: { role: 'OFFICE' } },
      { path: 'card/create', component: CardCreate, meta: { role: 'OFFICE' } },
      { path: 'card/report-lost', component: CardReportLost, meta: { role: 'OFFICE' } },
      { path: 'card/reissue', component: CardReissue, meta: { role: 'OFFICE' } },
      { path: 'card/cancel', component: CardCancel, meta: { role: 'OFFICE' } },
      { path: 'circulation/borrow', component: Borrow, meta: { role: 'CIRCULATION' } },
      { path: 'circulation/return', component: Return, meta: { role: 'CIRCULATION' } },
      { path: 'acquisition/books', component: BookImport, meta: { role: 'ACQUISITION' } },
      { path: 'myborrows', component: MyBorrows },
      { path: 'myfines', component: MyFines }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  if (to.meta.role && role !== to.meta.role) {
    next('/')
    return
  }

  next()
})

export default router
