<template>
  <div class="upload-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="title">图片管理</span>
        </div>
      </template>
      
      <el-form :model="uploadForm" label-width="100px" class="upload-form">
        <el-form-item label="选择相册">
          <el-select v-model="uploadForm.albumId" placeholder="请选择相册（可选）" clearable style="width: 300px">
            <el-option label="不上传到相册" :value="null" />
            <el-option
              v-for="album in albums"
              :key="album.id"
              :label="album.name"
              :value="album.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="上传照片">
          <el-upload
            ref="uploadRef"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :data="{ albumId: uploadForm.albumId }"
            :on-success="handleSuccess"
            :on-error="handleError"
            :before-upload="beforeUpload"
            :on-change="handleFileChange"
            :file-list="fileList"
            :auto-upload="false"
            :multiple="true"
            :accept="acceptTypes"
            list-type="picture-card"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg、jpeg、png、gif、bmp、webp 格式，单张照片最大 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="uploading" @click="handleUpload">
            开始上传
          </el-button>
          <el-button @click="handleClear">清空列表</el-button>
        </el-form-item>
      </el-form>
      
      <el-divider />
      
      <div class="upload-tips">
        <h3>上传说明</h3>
        <ul>
          <li>支持批量上传多张照片</li>
          <li>支持拖拽上传</li>
          <li>可以选择将照片上传到指定相册</li>
          <li>上传的照片将按时间倒序显示在"全部照片"中</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllAlbums } from '@/api/album'

const albums = ref([])
const fileList = ref([])
const uploading = ref(false)
const uploadRef = ref(null)

const uploadForm = reactive({
  albumId: null
})

const uploadUrl = computed(() => {
  return '/api/photos/upload'
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

const acceptTypes = '.jpg,.jpeg,.png,.gif,.bmp,.webp'

const loadAlbums = async () => {
  try {
    const res = await getAllAlbums()
    albums.value = res.data
  } catch (error) {
    console.error('加载相册列表失败:', error)
  }
}

const beforeUpload = (file) => {
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
  const isImage = allowedTypes.includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB！')
    return false
  }
  return true
}

const handleSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    ElMessage.success(`${file.name} 上传成功`)
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleError = (error, file, fileList) => {
  ElMessage.error(`${file.name} 上传失败`)
}

const handleFileChange = (file, uploadFileList) => {
  fileList.value = uploadFileList
}

const handleUpload = async () => {
  if (!uploadRef.value || !uploadRef.value.uploadFiles || uploadRef.value.uploadFiles.length === 0) {
    ElMessage.warning('请先选择要上传的照片')
    return
  }
  
  uploading.value = true
  
  try {
    uploadRef.value.submit()
    setTimeout(() => {
      ElMessage.success('照片上传完成')
      fileList.value = []
      uploadRef.value.clearFiles()
      uploading.value = false
    }, 1000)
  } catch (error) {
    console.error('上传失败:', error)
    uploading.value = false
  }
}

const handleClear = () => {
  fileList.value = []
  uploadRef.value.clearFiles()
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped lang="scss">
.upload-container {
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
  
  .upload-form {
    max-width: 800px;
  }
  
  .upload-tips {
    h3 {
      margin-bottom: 15px;
      color: #303133;
    }
    
    ul {
      padding-left: 20px;
      
      li {
        margin-bottom: 8px;
        color: #606266;
        line-height: 1.6;
      }
    }
  }
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 150px;
  height: 150px;
}
</style>
