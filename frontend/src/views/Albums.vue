<template>
  <div class="albums-container">
    <div class="page-header">
      <h2>我的相册</h2>
      <el-button type="primary" @click="showCreateDialog">创建相册</el-button>
    </div>

    <div class="albums-grid">
      <div
        v-for="album in albumList"
        :key="album.id"
        class="album-card"
        @click="$router.push(`/albums/${album.id}`)"
      >
        <div class="album-cover">
          <img v-if="album.cover" :src="album.cover" alt="" />
          <el-icon v-else class="default-cover"><FolderOpened /></el-icon>
        </div>
        <div class="album-info">
          <h3 class="album-name">{{ album.name }}</h3>
          <p class="album-desc">{{ album.description || '暂无描述' }}</p>
          <div class="album-actions" @click.stop>
            <el-button type="primary" link size="small" @click="showEditDialog(album)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(album.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="albumList.length === 0" class="empty-state">
      <el-empty description="暂无相册，点击上方按钮创建" />
    </div>

    <el-dialog v-model="albumDialogVisible" :title="isEdit ? '编辑相册' : '创建相册'" width="400px">
      <el-form :model="albumForm" label-width="80px">
        <el-form-item label="相册名称">
          <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="albumForm.description" type="textarea" :rows="3" placeholder="请输入相册描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="albumDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAlbum">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { albumApi } from '@/api'

const albumList = ref([])
const albumDialogVisible = ref(false)
const isEdit = ref(false)
const albumForm = ref({ id: null, name: '', description: '' })

const loadAlbums = async () => {
  const data = await albumApi.getList()
  albumList.value = data
}

const showCreateDialog = () => {
  isEdit.value = false
  albumForm.value = { id: null, name: '', description: '' }
  albumDialogVisible.value = true
}

const showEditDialog = (album) => {
  isEdit.value = true
  albumForm.value = { id: album.id, name: album.name, description: album.description }
  albumDialogVisible.value = true
}

const handleSaveAlbum = async () => {
  if (!albumForm.value.name.trim()) {
    ElMessage.warning('请输入相册名称')
    return
  }
  if (isEdit.value) {
    await albumApi.update(albumForm.value.id, albumForm.value)
    ElMessage.success('编辑成功')
  } else {
    await albumApi.create(albumForm.value)
    ElMessage.success('创建成功')
  }
  albumDialogVisible.value = false
  loadAlbums()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个相册吗？', '提示', { type: 'warning' })
    await albumApi.delete(id)
    ElMessage.success('删除成功')
    loadAlbums()
  } catch {
  }
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped lang="scss">
.albums-container {
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

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.album-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }
}

.album-cover {
  height: 160px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .default-cover {
    font-size: 64px;
    color: #c0c4cc;
  }
}

.album-info {
  padding: 16px;

  .album-name {
    margin: 0 0 8px 0;
    font-size: 16px;
    color: #303133;
  }

  .album-desc {
    margin: 0 0 12px 0;
    font-size: 14px;
    color: #909399;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .album-actions {
    display: flex;
    gap: 8px;
  }
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}
</style>
