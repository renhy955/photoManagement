import request from './request'

export const login = (data) => {
  return request.post('/api/auth/login', data)
}

export const register = (data) => {
  return request.post('/api/auth/register', data)
}

export const getCurrentUser = () => {
  return request.get('/api/auth/me')
}
