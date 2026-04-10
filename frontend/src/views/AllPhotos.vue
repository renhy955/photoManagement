<template>
  <div class="all-photos-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="title">全部照片</span>
          <div class="actions">
            <el-button type="danger" :disabled="selectedPhotos.length === 0" @click="handleBatchDelete">
              批量删除 ({{ selectedPhotos.length }})
            </el-button>
          </div>
        </div>
      </template>
      
      <div v-if="photos.length > 0" class="waterfall-container">
        <div
          v-for="photo in photos"
          :key="photo.id"
          class="waterfall-item"
        >
          <el-checkbox
            v-model="photo.selected"
            class="photo-checkbox"
            @change="handleSelectChange"
          />
          <img
            :src="photo.url"
            :alt="photo.name"
            @click="openPhotoSwipe(photo)"
          />
          <div class="photo-info">
            <div class="photo-name">{{ photo.name }}</div>
            <div class="photo-actions">
              <span class="photo-date">{{ formatDate(photo.createdAt) }}</span>
              <div>
                <el-button link type="primary" size="small" @click="handleRename(photo)">
                  重命名
                </el-button>
                <el-button link type="danger" size="small" @click="handleDelete(photo.id)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="暂无照片" />
      
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[20, 40, 60, 80]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadPhotos"
          @current-change="loadPhotos"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="renameDialogVisible" title="重命名照片" width="400px">
      <el-form>
        <el-form-item label="照片名称">
          <el-input v-model="newName" placeholder="请输入新的照片名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRename">确定</el-button>
      </template>
    </el-dialog>
    
    <div ref="pswpElement" class="pswp" tabindex="-1" role="dialog" aria-hidden="true"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPhotos, renamePhoto, deletePhoto, deletePhotos } from '@/api/photo'
import PhotoSwipe from 'photoswipe'
import 'photoswipe/style.css'

const photos = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedPhotos = ref([])
const renameDialogVisible = ref(false)
const newName = ref('')
const currentPhoto = ref(null)
const pswpElement = ref(null)

const loadPhotos = async () => {
  try {
    const res = await getPhotos({
      page: currentPage.value,
      size: pageSize.value
    })
    photos.value = res.data.records.map(photo => ({
      ...photo,
      selected: false
    }))
    total.value = res.data.total
  } catch (error) {
    console.error('加载照片失败:', error)
  }
}

const handleSelectChange = () => {
  selectedPhotos.value = photos.value.filter(photo => photo.selected)
}

const handleBatchDelete = async () => {
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
    
    const ids = selectedPhotos.value.map(photo => photo.id)
    await deletePhotos(ids)
    ElMessage.success('删除成功')
    loadPhotos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleRename = (photo) => {
  currentPhoto.value = photo
  newName.value = photo.name
  renameDialogVisible.value = true
}

const confirmRename = async () => {
  if (!newName.value.trim()) {
    ElMessage.warning('照片名称不能为空')
    return
  }
  
  try {
    await renamePhoto(currentPhoto.value.id, { name: newName.value })
    ElMessage.success('重命名成功')
    renameDialogVisible.value = false
    loadPhotos()
  } catch (error) {
    console.error('重命名失败:', error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deletePhoto(id)
    ElMessage.success('删除成功')
    loadPhotos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const openPhotoSwipe = (photo) => {
  const dataSource = photos.value
    .filter(p => !p.selected)
    .map(p => ({
      src: p.url,
      width: p.width || 1920,
      height: p.height || 1080,
      alt: p.name
    }))
  
  const index = dataSource.findIndex(p => p.src === photo.url)
  
  const pswp = new PhotoSwipe({
    element: pswpElement.value,
    dataSource,
    index: index >= 0 ? index : 0
  })
  
  pswp.init()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadPhotos()
})
</script>

<style scoped lang="scss">
.all-photos-container {
  .page-card {
    min-height: calc(100vh - 100px);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 18px;
      font-weight: 500;
    }
  }
  
  .waterfall-container {
    position: relative;
  }
  
  .waterfall-item {
    position: relative;
    
    .photo-checkbox {
      position: absolute;
      top: 10px;
      left: 10px;
      z-index: 10;
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

.pswp {
  z-index: 9999;
}
</style>
