import request from '@/utils/request'

export function getPhotos(params) {
  return request({
    url: '/photos',
    method: 'get',
    params
  })
}

export function getAlbumPhotos(albumId, params) {
  return request({
    url: `/photos/album/${albumId}`,
    method: 'get',
    params
  })
}

export function getPhoto(id) {
  return request({
    url: `/photos/${id}`,
    method: 'get'
  })
}

export function uploadPhoto(formData, onProgress) {
  return request({
    url: '/photos/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  })
}

export function uploadPhotos(formData, onProgress) {
  return request({
    url: '/photos/upload/batch',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  })
}

export function renamePhoto(id, data) {
  return request({
    url: `/photos/${id}/rename`,
    method: 'put',
    data
  })
}

export function deletePhoto(id) {
  return request({
    url: `/photos/${id}`,
    method: 'delete'
  })
}

export function deletePhotos(ids) {
  return request({
    url: '/photos/batch',
    method: 'delete',
    data: ids
  })
}

export function movePhotos(data) {
  return request({
    url: '/photos/move',
    method: 'post',
    data
  })
}

export function copyPhotos(data) {
  return request({
    url: '/photos/copy',
    method: 'post',
    data
  })
}
