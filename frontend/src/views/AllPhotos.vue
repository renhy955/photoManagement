<template>
  <div class="photos-container">
    <div class="page-header">
      <h2>全部照片</h2>
      <div class="header-actions">
        <el-button v-if="selectedPhotos.length > 0" type="danger" @click="handleBatchDelete">
          删除选中 ({{ selectedPhotos.length }})
        </el-button>
      </div>
    </div>

    <div class="photos-grid" ref="photosContainer">
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
            <el-button type="primary" link size="small" @click="showRenameDialog(photo)">重命名</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(photo.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="photoList.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无照片，快去上传吧" />
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

    <div class="pswp-gallery" id="gallery-all"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PhotoSwipeLightbox from 'photoswipe/lightbox'
import 'photoswipe/style.css'
import { photoApi } from '@/api'

const loading = ref(false)
const photoList = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const checkedPhotos = ref([])
const renameDialogVisible = ref(false)
const renameForm = ref({ id: null, name: '' })

const selectedPhotos = computed(() => checkedPhotos.value)

const loadPhotos = async () => {
  loading.value = true
  try {
    const data = await photoApi.getList({
      page: currentPage.value,
      size: pageSize.value
    })
    photoList.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const openPhotoSwipe = (photo) => {
  const lightbox = new PhotoSwipeLightbox({
    gallery: '#gallery-all',
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

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？', '提示', { type: 'warning' })
    await photoApi.delete([id])
    ElMessage.success('删除成功')
    loadPhotos()
  } catch {
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedPhotos.value.length} 张照片吗？`, '提示', { type: 'warning' })
    await photoApi.delete(selectedPhotos.value)
    ElMessage.success('删除成功')
    checkedPhotos.value = []
    loadPhotos()
  } catch {
  }
}

onMounted(() => {
  loadPhotos()
})

watch(checkedPhotos, () => {
  console.log('Selected:', checkedPhotos.value)
})
</script>

<style scoped lang="scss">
.photos-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 {
    margin: 0;
    color: #303133;
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
