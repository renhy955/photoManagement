import { defineStore } from 'pinia'
import { login, register, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),

  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      this.user = res.data.user
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      return res
    },

    async register(registerForm) {
      const res = await register(registerForm)
      return res
    },

    async getCurrentUser() {
      const res = await getCurrentUser()
      this.user = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
      return res
    },

    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
  }
})
