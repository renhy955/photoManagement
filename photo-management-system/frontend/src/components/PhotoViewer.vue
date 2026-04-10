<template>
  <Teleport to="body">
    <div v-if="visible" class="photo-viewer-overlay" @click="close">
      <div class="photo-viewer-container" @click.stop>
        <div class="viewer-header">
          <span class="photo-title">{{ currentPhoto?.name }}</span>
          <div class="viewer-actions">
            <el-button
              type="primary"
              circle
              size="small"
              @click="handleRename"
            >
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button
              type="danger"
              circle
              size="small"
              @click="handleDelete"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
            <el-button
              circle
              size="small"
              @click="close"
            >
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
        
        <div class="viewer-content">
          <el-button
            v-if="photos.length > 1"
            class="nav-btn prev"
            circle
            size="large"
            @click="prevPhoto"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          
          <div class="photo-container">
            <img
              :src="currentPhoto?.url"
              :alt="currentPhoto?.name"
              @click.stop
            />
          </div>
          
          <el-button
            v-if="photos.length > 1"
            class="nav-btn next"
            circle
            size="large"
            @click="nextPhoto"
          >
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div class="viewer-footer">
          <span>{{ currentIndex + 1 }} / {{ photos.length }}</span>
          <span v-if="currentPhoto?.width && currentPhoto?.height">
            {{ currentPhoto.width }} x {{ currentPhoto.height }}
          </span>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, Close, Edit, Delete } from '@element-plus/icons-vue'
import { renamePhoto, deletePhoto } from '@/api/photo'

const props = defineProps({
  visible: Boolean,
  photos: {
    type: Array,
    default: () => []
  },
  initialIndex: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['update:visible', 'refresh'])

const currentIndex = defineModel('currentIndex', { default: 0 })

const currentPhoto = computed(() => {
  return props.photos[currentIndex.value] || null
})

const close = () => {
  emit('update:visible', false)
}

const prevPhoto = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  } else {
    currentIndex.value = props.photos.length - 1
  }
}

const nextPhoto = () => {
  if (currentIndex.value < props.photos.length - 1) {
    currentIndex.value++
  } else {
    currentIndex.value = 0
  }
}

const handleRename = async () => {
  if (!currentPhoto.value) return
  
  try {
    const { value } = await ElMessageBox.prompt('请输入新名称', '重命名', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: currentPhoto.value.name,
      inputValidator: (value) => {
        if (!value || value.trim() === '') {
          return '名称不能为空'
        }
        return true
      }
    })
    
    await renamePhoto(currentPhoto.value.id, { name: value.trim() })
    ElMessage.success('重命名成功')
    currentPhoto.value.name = value.trim()
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleDelete = async () => {
  if (!currentPhoto.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deletePhoto(currentPhoto.value.id)
    ElMessage.success('删除成功')
    
    if (props.photos.length <= 1) {
      close()
    } else {
      props.photos.splice(currentIndex.value, 1)
      if (currentIndex.value >= props.photos.length) {
        currentIndex.value = props.photos.length - 1
      }
    }
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleKeydown = (e) => {
  if (!props.visible) return
  
  switch (e.key) {
    case 'Escape':
      close()
      break
    case 'ArrowLeft':
      prevPhoto()
      break
    case 'ArrowRight':
      nextPhoto()
      break
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    currentIndex.value = props.initialIndex
  }
})

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped lang="scss">
.photo-viewer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.photo-viewer-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.viewer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.5);
  
  .photo-title {
    color: #fff;
    font-size: 16px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 60%;
  }
  
  .viewer-actions {
    display: flex;
    gap: 8px;
  }
}

.viewer-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 20px;
  
  .photo-container {
    max-width: 90%;
    max-height: 90%;
    
    img {
      max-width: 100%;
      max-height: 80vh;
      object-fit: contain;
    }
  }
  
  .nav-btn {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: #fff;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
    
    &.prev {
      left: 20px;
    }
    
    &.next {
      right: 20px;
    }
  }
}

.viewer-footer {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding: 16px;
  color: #fff;
  font-size: 14px;
  background: rgba(0, 0, 0, 0.5);
}
</style>
