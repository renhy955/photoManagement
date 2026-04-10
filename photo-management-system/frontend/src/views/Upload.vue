<template>
  <div class="page-container">
    <div class="page-header">
      <h2>上传照片</h2>
    </div>
    
    <el-card class="upload-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择相册">
          <el-select v-model="form.albumId" placeholder="请选择相册（可选）" clearable style="width: 300px">
            <el-option
              v-for="album in albums"
              :key="album.id"
              :label="album.name"
              :value="album.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择照片">
          <el-upload
            ref="uploadRef"
            v-model:file-list="fileList"
            action=""
            :auto-upload="false"
            :on-change="handleChange"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            multiple
            drag
            accept="image/jpeg,image/png,image/gif,image/webp"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 JPG、PNG、GIF、WEBP 格式，单个文件不超过 20MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="uploading"
            :disabled="fileList.length === 0"
            @click="handleUpload"
          >
            开始上传 ({{ fileList.length }})
          </el-button>
          <el-button size="large" @click="handleClear">清空列表</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 上传进度 -->
      <div v-if="uploading" class="upload-progress">
        <el-progress
          :percentage="uploadProgress"
          :status="uploadStatus"
          :stroke-width="20"
          striped
          striped-flow
        />
        <p class="progress-text">正在上传... {{ uploadedCount }} / {{ fileList.length }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { uploadPhotos } from '@/api/photo'
import { getAlbums } from '@/api/album'

const router = useRouter()
const uploadRef = ref(null)
const fileList = ref([])
const albums = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadedCount = ref(0)
const uploadStatus = ref('')

const form = reactive({
  albumId: null
})

const loadAlbums = async () => {
  try {
    const res = await getAlbums()
    albums.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const beforeUpload = (file) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  const isAllowed = allowedTypes.includes(file.type)
  const isLt20M = file.size / 1024 / 1024 < 20

  if (!isAllowed) {
    ElMessage.error('只支持 JPG、PNG、GIF、WEBP 格式的图片')
    return false
  }
  if (!isLt20M) {
    ElMessage.error('图片大小不能超过 20MB')
    return false
  }
  return true
}

const handleChange = (file, fileList) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  const isAllowed = allowedTypes.includes(file.raw.type)
  const isLt20M = file.raw.size / 1024 / 1024 < 20

  if (!isAllowed) {
    ElMessage.error(`${file.name} 格式不支持`)
    const index = fileList.indexOf(file)
    if (index > -1) {
      fileList.splice(index, 1)
    }
    return
  }
  if (!isLt20M) {
    ElMessage.error(`${file.name} 超过20MB`)
    const index = fileList.indexOf(file)
    if (index > -1) {
      fileList.splice(index, 1)
    }
    return
  }
}

const handleRemove = (file, fileList) => {
  console.log('移除文件:', file.name)
}

const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的照片')
    return
  }

  uploading.value = true
  uploadProgress.value = 0
  uploadedCount.value = 0
  uploadStatus.value = ''

  try {
    const formData = new FormData()
    fileList.value.forEach((file, index) => {
      formData.append('files', file.raw)
      
      // 模拟进度更新
      setTimeout(() => {
        uploadedCount.value = index + 1
        uploadProgress.value = Math.round(((index + 1) / fileList.value.length) * 100)
      }, index * 500)
    })
    
    if (form.albumId) {
      formData.append('albumId', form.albumId)
    }

    await uploadPhotos(formData)
    uploadStatus.value = 'success'
    ElMessage.success(`成功上传 ${fileList.value.length} 张照片`)
    
    setTimeout(() => {
      router.push('/photos')
    }, 1000)
  } catch (error) {
    uploadStatus.value = 'exception'
    console.error(error)
  } finally {
    uploading.value = false
  }
}

const handleClear = () => {
  fileList.value = []
  uploadProgress.value = 0
  uploadedCount.value = 0
  uploadStatus.value = ''
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped lang="scss">
.upload-card {
  max-width: 800px;
  margin: 0 auto;
  
  :deep(.el-upload) {
    width: 100%;
    
    .el-upload-dragger {
      width: 100%;
      height: 300px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
    }
  }
  
  :deep(.el-upload__tip) {
    margin-top: 12px;
    color: #666;
  }
}

.upload-progress {
  margin-top: 24px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  
  .progress-text {
    text-align: center;
    margin: 12px 0 0;
    color: #666;
  }
}
</style>
