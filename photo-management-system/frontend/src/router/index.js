import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/photos',
    children: [
      {
        path: '/photos',
        name: 'AllPhotos',
        component: () => import('@/views/AllPhotos.vue'),
        meta: { title: '全部照片' }
      },
      {
        path: '/albums',
        name: 'Albums',
        component: () => import('@/views/Albums.vue'),
        meta: { title: '我的相册' }
      },
      {
        path: '/album/:id',
        name: 'AlbumDetail',
        component: () => import('@/views/AlbumDetail.vue'),
        meta: { title: '相册详情' }
      },
      {
        path: '/upload',
        name: 'Upload',
        component: () => import('@/views/Upload.vue'),
        meta: { title: '上传照片' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (!to.meta.public && !userStore.token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
