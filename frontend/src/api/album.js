import request from '@/utils/request'

export function getAlbums(params) {
  return request({
    url: '/albums',
    method: 'get',
    params
  })
}

export function getAllAlbums() {
  return request({
    url: '/albums/all',
    method: 'get'
  })
}

export function getAlbum(id) {
  return request({
    url: `/albums/${id}`,
    method: 'get'
  })
}

export function createAlbum(data) {
  return request({
    url: '/albums',
    method: 'post',
    data
  })
}

export function updateAlbum(id, data) {
  return request({
    url: `/albums/${id}`,
    method: 'put',
    data
  })
}

export function deleteAlbum(id) {
  return request({
    url: `/albums/${id}`,
    method: 'delete'
  })
}
