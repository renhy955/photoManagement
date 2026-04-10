<template>
  <div class="album-detail-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button link @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <span class="title">{{ album?.name || '相册详情' }}</span>
          </div>
          <div class="actions">
            <el-button type="primary" :disabled="selectedPhotos.length === 0" @click="handleMove">
              移动 ({{ selectedPhotos.length }})
            </el-button>
            <el-button type="success" :disabled="selectedPhotos.length === 0" @click="handleCopy">
              复制 ({{ selectedPhotos.length }})
            </el-button>
            <el-button type="danger" :disabled="selectedPhotos.length === 0" @click="handleBatchDelete">
              删除 ({{ selectedPhotos.length }})
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
      
      <el-empty v-else description="该相册暂无照片" />
      
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
    
    <el-dialog v-model="moveDialogVisible" title="移动照片" width="400px">
      <el-form>
        <el-form-item label="目标相册">
          <el-select v-model="targetAlbumId" placeholder="请选择目标相册" style="width: 100%">
            <el-option label="未分类" :value="null" />
            <el-option
              v-for="album in allAlbums"
              :key="album.id"
              :label="album.name"
              :value="album.id"
              :disabled="album.id === currentAlbumId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="moveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmMove">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="copyDialogVisible" title="复制照片" width="400px">
      <el-form>
        <el-form-item label="目标相册">
          <el-select v-model="targetAlbumId" placeholder="请选择目标相册" style="width: 100%">
            <el-option
              v-for="album in allAlbums"
              :key="album.id"
              :label="album.name"
              :value="album.id"
              :disabled="album.id === currentAlbumId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="copyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCopy">确定</el-button>
      </template>
    </el-dialog>
    
    <div ref="pswpElement" class="pswp" tabindex="-1" role="dialog" aria-hidden="true"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAlbum } from '@/api/album'
import { getAlbumPhotos, renamePhoto, deletePhoto, deletePhotos, movePhotos, copyPhotos } from '@/api/photo'
import { getAllAlbums } from '@/api/album'
import PhotoSwipe from 'photoswipe'
import 'photoswipe/style.css'

const route = useRoute()
const router = useRouter()

const album = ref(null)
const photos = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedPhotos = ref([])
const renameDialogVisible = ref(false)
const moveDialogVisible = ref(false)
const copyDialogVisible = ref(false)
const newName = ref('')
const currentPhoto = ref(null)
const allAlbums = ref([])
const targetAlbumId = ref(null)
const pswpElement = ref(null)

const currentAlbumId = computed(() => parseInt(route.params.id))

const loadAlbum = async () => {
  try {
    const res = await getAlbum(currentAlbumId.value)
    album.value = res.data
  } catch (error) {
    console.error('加载相册失败:', error)
  }
}

const loadPhotos = async () => {
  try {
    const res = await getAlbumPhotos(currentAlbumId.value, {
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

const loadAllAlbums = async () => {
  try {
    const res = await getAllAlbums()
    allAlbums.value = res.data
  } catch (error) {
    console.error('加载相册列表失败:', error)
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
    loadAlbum()
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
    loadAlbum()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleMove = () => {
  targetAlbumId.value = null
  moveDialogVisible.value = true
}

const confirmMove = async () => {
  try {
    await movePhotos({
      albumId: targetAlbumId.value,
      photoIds: selectedPhotos.value.map(p => p.id)
    })
    ElMessage.success('移动成功')
    moveDialogVisible.value = false
    loadPhotos()
    loadAlbum()
  } catch (error) {
    console.error('移动失败:', error)
  }
}

const handleCopy = () => {
  targetAlbumId.value = null
  copyDialogVisible.value = true
}

const confirmCopy = async () => {
  if (!targetAlbumId.value) {
    ElMessage.warning('请选择目标相册')
    return
  }
  
  try {
    await copyPhotos({
      albumId: targetAlbumId.value,
      photoIds: selectedPhotos.value.map(p => p.id)
    })
    ElMessage.success('复制成功')
    copyDialogVisible.value = false
  } catch (error) {
    console.error('复制失败:', error)
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

const goBack = () => {
  router.push('/my-albums')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadAlbum()
  loadPhotos()
  loadAllAlbums()
})
</script>

<style scoped lang="scss">
.album-detail-container {
  .page-card {
    min-height: calc(100vh - 100px);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .title {
        font-size: 18px;
        font-weight: 500;
      }
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
