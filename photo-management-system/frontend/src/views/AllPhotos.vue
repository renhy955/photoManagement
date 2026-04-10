<template>
  <div class="page-container">
    <div class="page-header">
      <h2>全部照片</h2>
      <div class="header-actions">
        <el-button
          v-if="selectedPhotos.length > 0"
          type="danger"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedPhotos.length }})
        </el-button>
        <el-button type="primary" @click="$router.push('/upload')">
          <el-icon><Plus /></el-icon>
          上传照片
        </el-button>
      </div>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="photos.length === 0" class="empty-container">
      <el-empty description="暂无照片，快去上传吧">
        <el-button type="primary" @click="$router.push('/upload')">上传照片</el-button>
      </el-empty>
    </div>
    
    <div v-else class="photos-container">
      <div class="masonry-grid" ref="masonryRef">
        <div
          v-for="photo in photos"
          :key="photo.id"
          class="masonry-grid-column"
          :style="{ width: columnWidth + 'px' }"
        >
          <div
            class="photo-card"
            :class="{ selected: selectedPhotos.includes(photo.id) }"
            @click="handlePhotoClick(photo, $event)"
          >
            <div class="photo-wrapper">
              <img
                :src="photo.thumbnailUrl || photo.url"
                :alt="photo.name"
                loading="lazy"
                @load="handleImageLoad"
              />
              <div class="photo-overlay" @click.stop>
                <el-checkbox
                  v-model="selectedPhotos"
                  :label="photo.id"
                  @click.stop
                >
                  选择
                </el-checkbox>
                <el-button
                  type="primary"
                  circle
                  size="small"
                  @click.stop="handlePreview(photo)"
                >
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button
                  type="warning"
                  circle
                  size="small"
                  @click.stop="handleRename(photo)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button
                  type="danger"
                  circle
                  size="small"
                  @click.stop="handleDelete(photo)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="photo-info">
              <p class="photo-name" :title="photo.name">{{ photo.name }}</p>
              <p class="photo-date">{{ formatDate(photo.createTime) }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[20, 40, 60, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 图片预览 -->
    <PhotoViewer
      v-model:visible="viewerVisible"
      :photos="photos"
      :initial-index="viewerIndex"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, View, Edit, Delete } from '@element-plus/icons-vue'
import { getPhotos, deletePhoto, batchDeletePhotos, renamePhoto } from '@/api/photo'
import PhotoViewer from '@/components/PhotoViewer.vue'
import Masonry from 'masonry-layout'
import imagesLoaded from 'imagesloaded'

const loading = ref(false)
const photos = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedPhotos = ref([])
const masonryRef = ref(null)
let masonry = null

const columnWidth = ref(280)
const viewerVisible = ref(false)
const viewerIndex = ref(0)

const loadPhotos = async () => {
  loading.value = true
  try {
    const res = await getPhotos({
      page: currentPage.value,
      size: pageSize.value
    })
    photos.value = res.data.records
    total.value = res.data.total
    selectedPhotos.value = []
    nextTick(() => {
      initMasonry()
    })
  } finally {
    loading.value = false
  }
}

const initMasonry = () => {
  if (masonry) {
    masonry.destroy()
  }
  
  const container = masonryRef.value
  if (!container) return
  
  masonry = new Masonry(container, {
    itemSelector: '.masonry-grid-column',
    columnWidth: columnWidth.value,
    gutter: 16,
    fitWidth: true
  })
  
  imagesLoaded(container, () => {
    masonry.layout()
  })
}

const handleImageLoad = () => {
  if (masonry) {
    masonry.layout()
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadPhotos()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadPhotos()
}

const handlePhotoClick = (photo, event) => {
  if (event.target.closest('.el-checkbox') || event.target.closest('.el-button')) {
    return
  }
  handlePreview(photo)
}

const handlePreview = (photo) => {
  viewerIndex.value = photos.value.findIndex(p => p.id === photo.id)
  viewerVisible.value = true
}

const handleRename = async (photo) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新名称', '重命名', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: photo.name,
      inputValidator: (value) => {
        if (!value || value.trim() === '') {
          return '名称不能为空'
        }
        return true
      }
    })
    
    await renamePhoto(photo.id, { name: value.trim() })
    ElMessage.success('重命名成功')
    loadPhotos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleDelete = async (photo) => {
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deletePhoto(photo.id)
    ElMessage.success('删除成功')
    loadPhotos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedPhotos.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedPhotos.value.length} 张照片吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await batchDeletePhotos({ ids: selectedPhotos.value })
    ElMessage.success('批量删除成功')
    selectedPhotos.value = []
    loadPhotos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const handleResize = () => {
  const containerWidth = masonryRef.value?.parentElement?.clientWidth || 1200
  const columns = Math.floor(containerWidth / (columnWidth.value + 16))
  const newWidth = Math.floor((containerWidth - (columns - 1) * 16) / columns)
  columnWidth.value = Math.max(200, Math.min(300, newWidth))
  nextTick(() => {
    initMasonry()
  })
}

onMounted(() => {
  loadPhotos()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (masonry) {
    masonry.destroy()
  }
})
</script>

<style scoped lang="scss">
.photos-container {
  .masonry-grid {
    margin: 0 auto;
  }
  
  .photo-wrapper {
    position: relative;
    overflow: hidden;
    border-radius: 8px 8px 0 0;
    
    img {
      width: 100%;
      display: block;
      transition: transform 0.3s;
    }
  }
  
  .photo-info {
    padding: 12px;
    background: #fff;
    border-radius: 0 0 8px 8px;
    
    .photo-name {
      margin: 0 0 4px;
      font-size: 14px;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .photo-date {
      margin: 0;
      font-size: 12px;
      color: #999;
    }
  }
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.loading-container,
.empty-container {
  padding: 60px 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}
</style>
