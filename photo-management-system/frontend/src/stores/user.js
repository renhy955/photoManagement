import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const loginAction = async (credentials) => {
    const res = await login(credentials)
    setToken(res.data.token)
    userInfo.value = res.data.user
    return res
  }

  const registerAction = async (data) => {
    const res = await register(data)
    setToken(res.data.token)
    userInfo.value = res.data.user
    return res
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      userInfo.value = res.data
    } catch (error) {
      clearToken()
    }
  }

  const logout = () => {
    clearToken()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    clearToken,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout
  }
})
