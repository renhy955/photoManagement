<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的相册</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建相册
      </el-button>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="albums.length === 0" class="empty-container">
      <el-empty description="暂无相册，快去创建吧">
        <el-button type="primary" @click="showCreateDialog = true">创建相册</el-button>
      </el-empty>
    </div>
    
    <div v-else class="albums-grid">
      <el-card
        v-for="album in albums"
        :key="album.id"
        class="album-card"
        shadow="hover"
        @click="goToAlbum(album)"
      >
        <div class="album-cover">
          <el-image
            v-if="album.coverUrl"
            :src="album.coverUrl"
            fit="cover"
            style="width: 100%; height: 100%"
          />
          <el-icon v-else size="64"><Folder /></el-icon>
        </div>
        <div class="album-info">
          <h3 class="album-name">{{ album.name }}</h3>
          <p class="album-desc" v-if="album.description">{{ album.description }}</p>
          <p class="album-count">{{ album.photoCount || 0 }} 张照片</p>
        </div>
        <div class="album-actions" @click.stop>
          <el-button
            type="primary"
            link
            size="small"
            @click="handleEdit(album)"
          >
            编辑
          </el-button>
          <el-button
            type="danger"
            link
            size="small"
            @click="handleDelete(album)"
          >
            删除
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 创建/编辑相册对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="isEdit ? '编辑相册' : '创建相册'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="相册描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入相册描述（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder } from '@element-plus/icons-vue'
import { getAlbums, createAlbum, updateAlbum, deleteAlbum } from '@/api/album'

const router = useRouter()
const loading = ref(false)
const albums = ref([])
const showCreateDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentAlbum = ref(null)
const formRef = ref(null)

const form = reactive({
  name: '',
  description: ''
})

const rules = {
  name: [
    { required: true, message: '请输入相册名称', trigger: 'blur' },
    { max: 50, message: '相册名称不能超过50个字符', trigger: 'blur' }
  ]
}

const loadAlbums = async () => {
  loading.value = true
  try {
    const res = await getAlbums()
    albums.value = res.data
  } finally {
    loading.value = false
  }
}

const goToAlbum = (album) => {
  router.push(`/album/${album.id}`)
}

const handleEdit = (album) => {
  isEdit.value = true
  currentAlbum.value = album
  form.name = album.name
  form.description = album.description || ''
  showCreateDialog.value = true
}

const handleDelete = async (album) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除相册 "${album.name}" 吗？相册内的照片不会被删除。`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteAlbum(album.id)
    ElMessage.success('删除成功')
    loadAlbums()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAlbum(currentAlbum.value.id, {
        name: form.name,
        description: form.description
      })
      ElMessage.success('更新成功')
    } else {
      await createAlbum({
        name: form.name,
        description: form.description
      })
      ElMessage.success('创建成功')
    }
    showCreateDialog.value = false
    loadAlbums()
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  form.name = ''
  form.description = ''
  isEdit.value = false
  currentAlbum.value = null
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped lang="scss">
.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.album-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
  
  .album-cover {
    height: 200px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    overflow: hidden;
    
    .el-icon {
      opacity: 0.8;
    }
  }
  
  .album-info {
    padding: 16px;
    
    .album-name {
      margin: 0 0 8px;
      font-size: 16px;
      font-weight: 500;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .album-desc {
      margin: 0 0 8px;
      font-size: 13px;
      color: #666;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    
    .album-count {
      margin: 0;
      font-size: 13px;
      color: #999;
    }
  }
  
  .album-actions {
    display: flex;
    justify-content: flex-end;
    padding: 0 16px 16px;
    gap: 8px;
  }
}

.loading-container,
.empty-container {
  padding: 60px 0;
}
</style>
