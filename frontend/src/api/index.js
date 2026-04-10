import request from '@/utils/request'

export const authApi = {
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  }
}

export const albumApi = {
  getList() {
    return request({
      url: '/albums',
      method: 'get'
    })
  },
  getDetail(id) {
    return request({
      url: `/albums/${id}`,
      method: 'get'
    })
  },
  create(data) {
    return request({
      url: '/albums',
      method: 'post',
      data
    })
  },
  update(id, data) {
    return request({
      url: `/albums/${id}`,
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/albums/${id}`,
      method: 'delete'
    })
  }
}

export const photoApi = {
  getList(params) {
    return request({
      url: '/photos',
      method: 'get',
      params
    })
  },
  upload(data) {
    return request({
      url: '/photos/upload',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  rename(id, name) {
    return request({
      url: `/photos/${id}/rename`,
      method: 'put',
      params: { name }
    })
  },
  move(photoIds, targetAlbumId) {
    return request({
      url: '/photos/move',
      method: 'post',
      params: { photoIds, targetAlbumId }
    })
  },
  copy(photoIds, targetAlbumId) {
    return request({
      url: '/photos/copy',
      method: 'post',
      params: { photoIds, targetAlbumId }
    })
  },
  delete(photoIds) {
    return request({
      url: '/photos',
      method: 'delete',
      params: { photoIds }
    })
  }
}
