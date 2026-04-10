<template>
  <div class="album-detail-container">
    <div class="page-header">
      <div>
        <el-button @click="$router.back()" link>
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>{{ albumInfo.name }}</h2>
        <p class="album-desc">{{ albumInfo.description }}</p>
      </div>
      <div class="header-actions">
        <el-dropdown @command="handleBatchAction" v-if="selectedPhotos.length > 0">
          <el-button type="primary">
            批量操作 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="move">移动到相册</el-dropdown-item>
              <el-dropdown-item command="copy">复制到相册</el-dropdown-item>
              <el-dropdown-item command="delete" style="color: #f56c6c">删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="photos-grid">
      <div
        v-for="photo in photoList"
        :key="photo.id"
        class="photo-item"
        :class="{ selected: selectedPhotos.includes(photo.id) }"
      >
        <div class="photo-checkbox">
          <el-checkbox v-model="checkedPhotos" :value="photo.id" size="large" />
        </div>
        <img
          :src="'http://localhost:8080/api' + photo.filePath"
          :alt="photo.name"
          class="photo-img"
          @click="openPhotoSwipe(photo)"
        />
        <div class="photo-info">
          <span class="photo-name" @click="showRenameDialog(photo)">{{ photo.name }}</span>
          <div class="photo-actions">
            <el-dropdown @command="(cmd) => handlePhotoAction(cmd, photo)">
              <el-button type="primary" link size="small">操作</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="rename">重命名</el-dropdown-item>
                  <el-dropdown-item command="move">移动到相册</el-dropdown-item>
                  <el-dropdown-item command="copy">复制到相册</el-dropdown-item>
                  <el-dropdown-item command="delete" style="color: #f56c6c">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <div v-if="photoList.length === 0 && !loading" class="empty-state">
      <el-empty description="相册暂无照片" />
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadPhotos"
      />
    </div>

    <el-dialog v-model="renameDialogVisible" title="重命名" width="400px">
      <el-form :model="renameForm">
        <el-form-item label="新名称">
          <el-input v-model="renameForm.name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRename">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="albumDialogVisible" :title="actionType === 'move' ? '移动到相册' : '复制到相册'" width="400px">
      <el-form>
        <el-form-item label="选择相册">
          <el-select v-model="targetAlbumId" placeholder="请选择相册" style="width: 100%">
            <el-option
              v-for="album in allAlbums"
              :key="album.id"
              :label="album.name"
              :value="album.id"
              :disabled="album.id === albumId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="albumDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAlbumAction">确定</el-button>
      </template>
    </el-dialog>

    <div class="pswp-gallery" id="gallery-album"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import PhotoSwipeLightbox from 'photoswipe/lightbox'
import 'photoswipe/style.css'
import { photoApi, albumApi } from '@/api'

const route = useRoute()
const albumId = computed(() => route.params.id)

const loading = ref(false)
const albumInfo = ref({})
const photoList = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const checkedPhotos = ref([])
const allAlbums = ref([])

const renameDialogVisible = ref(false)
const renameForm = ref({ id: null, name: '' })

const albumDialogVisible = ref(false)
const actionType = ref('move')
const targetAlbumId = ref(null)
const actionPhotoIds = ref([])

const selectedPhotos = computed(() => checkedPhotos.value)

const loadAlbumInfo = async () => {
  const data = await albumApi.getDetail(albumId.value)
  albumInfo.value = data
}

const loadPhotos = async () => {
  loading.value = true
  try {
    const data = await photoApi.getList({
      page: currentPage.value,
      size: pageSize.value,
      albumId: albumId.value
    })
    photoList.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const loadAllAlbums = async () => {
  allAlbums.value = await albumApi.getList()
}

const openPhotoSwipe = (photo) => {
  const lightbox = new PhotoSwipeLightbox({
    gallery: '#gallery-album',
    children: 'a',
    pswpModule: () => import('photoswipe')
  })
  lightbox.init()
  lightbox.loadAndOpen(0, [
    {
      src: 'http://localhost:8080/api' + photo.filePath,
      width: photo.width || 1200,
      height: photo.height || 800,
      alt: photo.name
    }
  ])
}

const showRenameDialog = (photo) => {
  renameForm.value = { id: photo.id, name: photo.name }
  renameDialogVisible.value = true
}

const handleRename = async () => {
  if (!renameForm.value.name.trim()) return
  await photoApi.rename(renameForm.value.id, renameForm.value.name)
  ElMessage.success('重命名成功')
  renameDialogVisible.value = false
  loadPhotos()
}

const handleDelete = async (photoIds) => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${photoIds.length} 张照片吗？`, '提示', { type: 'warning' })
    await photoApi.delete(photoIds)
    ElMessage.success('删除成功')
    checkedPhotos.value = []
    loadPhotos()
  } catch {
  }
}

const handlePhotoAction = (cmd, photo) => {
  if (cmd === 'rename') {
    showRenameDialog(photo)
  } else if (cmd === 'delete') {
    handleDelete([photo.id])
  } else {
    actionType.value = cmd
    actionPhotoIds.value = [photo.id]
    albumDialogVisible.value = true
  }
}

const handleBatchAction = (cmd) => {
  if (cmd === 'delete') {
    handleDelete(selectedPhotos.value)
  } else {
    actionType.value = cmd
    actionPhotoIds.value = [...selectedPhotos.value]
    albumDialogVisible.value = true
  }
}

const confirmAlbumAction = async () => {
  if (!targetAlbumId.value) {
    ElMessage.warning('请选择目标相册')
    return
  }
  if (actionType.value === 'move') {
    await photoApi.move(actionPhotoIds.value, targetAlbumId.value)
    ElMessage.success('移动成功')
  } else {
    await photoApi.copy(actionPhotoIds.value, targetAlbumId.value)
    ElMessage.success('复制成功')
  }
  checkedPhotos.value = []
  albumDialogVisible.value = false
  loadPhotos()
}

onMounted(() => {
  loadAlbumInfo()
  loadPhotos()
  loadAllAlbums()
})
</script>

<style scoped lang="scss">
.album-detail-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;

  h2 {
    margin: 8px 0 0 0;
    color: #303133;
  }

  .album-desc {
    margin: 4px 0 0 0;
    color: #909399;
    font-size: 14px;
  }
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.photo-item {
  position: relative;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &.selected {
    box-shadow: 0 0 0 2px #409eff;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);

    .photo-checkbox {
      opacity: 1;
    }
  }
}

.photo-checkbox {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-item:hover .photo-checkbox,
.photo-item.selected .photo-checkbox {
  opacity: 1;
}

.photo-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  cursor: pointer;
}

.photo-info {
  padding: 12px;

  .photo-name {
    font-size: 14px;
    color: #303133;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;

    &:hover {
      color: #409eff;
    }
  }

  .photo-actions {
    display: flex;
    gap: 8px;
  }
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
