<template>
  <div class="upload-container">
    <div class="page-header">
      <h2>上传照片</h2>
    </div>

    <el-card class="upload-card">
      <div class="upload-options">
        <el-form label-width="100px">
          <el-form-item label="选择相册">
            <el-select v-model="selectedAlbum" placeholder="不选择则上传到未分类" style="width: 300px">
              <el-option label="未分类" :value="null" />
              <el-option
                v-for="album in albumList"
                :key="album.id"
                :label="album.name"
                :value="album.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <el-upload
        ref="uploadRef"
        class="upload-area"
        :auto-upload="false"
        :multiple="true"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="fileList"
        accept="image/jpeg,image/png,image/gif,image/webp"
        list-type="picture-card"
      >
        <el-icon class="upload-icon"><Plus /></el-icon>
        <template #tip>
          <div class="upload-tip">
            支持 jpg、png、gif、webp 格式，单张图片不超过 10MB
          </div>
        </template>
        <template #file="{ file }">
          <img class="upload-file-img" :src="file.url" />
          <span class="upload-file-name">{{ file.name }}</span>
        </template>
      </el-upload>

      <div class="upload-footer">
        <div class="file-count">已选择 {{ fileList.length }} 张照片</div>
        <el-button type="primary" :loading="uploading" @click="handleUpload" :disabled="fileList.length === 0">
          开始上传
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { photoApi, albumApi } from '@/api'

const uploadRef = ref()
const fileList = ref([])
const albumList = ref([])
const selectedAlbum = ref(null)
const uploading = ref(false)

const loadAlbums = async () => {
  albumList.value = await albumApi.getList()
}

const handleFileChange = (file, files) => {
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.warning(`${file.name} 超过10MB，已跳过`)
    fileList.value = fileList.value.filter(f => f.uid !== file.uid)
    return
  }
  fileList.value = files
}

const handleFileRemove = (file, files) => {
  fileList.value = files
}

const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的照片')
    return
  }

  uploading.value = true
  try {
    const formData = new FormData()
    fileList.value.forEach(file => {
      formData.append('files', file.raw)
    })
    if (selectedAlbum.value) {
      formData.append('albumId', selectedAlbum.value)
    }

    await photoApi.upload(formData)
    ElMessage.success('上传成功')
    fileList.value = []
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped lang="scss">
.upload-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  h2 {
    margin: 0;
    color: #303133;
  }
}

.upload-card {
  max-width: 900px;
}

.upload-options {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.upload-area {
  :deep(.el-upload) {
    width: 100%;
  }

  :deep(.el-upload--picture-card) {
    width: 140px;
    height: 140px;
    border: 2px dashed #d9d9d9;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
    }
  }

  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 140px;
    height: 140px;
    border-radius: 8px;
  }
}

.upload-icon {
  font-size: 32px;
  color: #8c939d;
}

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
}

.upload-file-img {
  width: 100%;
  height: 100px;
  object-fit: cover;
}

.upload-file-name {
  display: block;
  font-size: 12px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 8px;
}

.upload-footer {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-count {
  color: #909399;
}
</style>
