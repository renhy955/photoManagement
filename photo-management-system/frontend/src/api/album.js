import request from './request'

export const getAlbums = () => {
  return request.get('/api/albums')
}

export const createAlbum = (data) => {
  return request.post('/api/albums', data)
}

export const updateAlbum = (id, data) => {
  return request.put(`/api/albums/${id}`, data)
}

export const deleteAlbum = (id) => {
  return request.delete(`/api/albums/${id}`)
}
