<template>
  <div class="my-albums-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="title">我的相册</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            创建相册
          </el-button>
        </div>
      </template>
      
      <div v-if="albums.length > 0" class="album-grid">
        <div
          v-for="album in albums"
          :key="album.id"
          class="album-card"
          @click="goToAlbum(album.id)"
        >
          <div class="album-cover">
            <img v-if="album.coverUrl" :src="album.coverUrl" :alt="album.name" />
            <el-icon v-else class="no-cover"><Picture /></el-icon>
          </div>
          <div class="album-info">
            <div class="album-name">{{ album.name }}</div>
            <div class="album-meta">
              <span class="photo-count">{{ album.photoCount }} 张照片</span>
              <div class="album-actions" @click.stop>
                <el-button link type="primary" size="small" @click="handleEdit(album)">
                  编辑
                </el-button>
                <el-button link type="danger" size="small" @click="handleDelete(album.id)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="暂无相册，快去创建一个吧" />
      
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 40]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadAlbums"
          @current-change="loadAlbums"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="albumFormRef" :model="albumForm" :rules="albumRules" label-width="80px">
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="相册描述" prop="description">
          <el-input
            v-model="albumForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入相册描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAlbums, createAlbum, updateAlbum, deleteAlbum } from '@/api/album'

const router = useRouter()

const albums = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentAlbumId = ref(null)

const albumFormRef = ref(null)
const albumForm = reactive({
  name: '',
  description: ''
})

const albumRules = {
  name: [
    { required: true, message: '请输入相册名称', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => isEdit.value ? '编辑相册' : '创建相册')

const loadAlbums = async () => {
  try {
    const res = await getAlbums({
      page: currentPage.value,
      size: pageSize.value
    })
    albums.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载相册失败:', error)
  }
}

const handleCreate = () => {
  isEdit.value = false
  currentAlbumId.value = null
  albumForm.name = ''
  albumForm.description = ''
  dialogVisible.value = true
}

const handleEdit = (album) => {
  isEdit.value = true
  currentAlbumId.value = album.id
  albumForm.name = album.name
  albumForm.description = album.description || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!albumFormRef.value) return
  
  await albumFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateAlbum(currentAlbumId.value, albumForm)
          ElMessage.success('更新成功')
        } else {
          await createAlbum(albumForm)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadAlbums()
      } catch (error) {
        console.error('操作失败:', error)
      }
    }
  })
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('删除相册后，相册内的照片将移至未分类，确定要删除吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAlbum(id)
    ElMessage.success('删除成功')
    loadAlbums()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const goToAlbum = (id) => {
  router.push(`/album/${id}`)
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped lang="scss">
.my-albums-container {
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
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
