import request from './request'

export const getPhotos = (params) => {
  return request.get('/api/photos', { params })
}

export const getAlbumPhotos = (albumId, params) => {
  return request.get(`/api/photos/album/${albumId}`, { params })
}

export const uploadPhotos = (data) => {
  return request.post('/api/photos/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const renamePhoto = (id, data) => {
  return request.put(`/api/photos/${id}/rename`, data)
}

export const movePhoto = (id, data) => {
  return request.put(`/api/photos/${id}/move`, data)
}

export const copyPhoto = (id, data) => {
  return request.post(`/api/photos/${id}/copy`, data)
}

export const deletePhoto = (id) => {
  return request.delete(`/api/photos/${id}`)
}

export const batchDeletePhotos = (data) => {
  return request.post('/api/photos/batch-delete', data)
}
