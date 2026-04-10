<template>
  <el-container class="main-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="32"><Picture /></el-icon>
        <span>相册管理</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/photos">
          <el-icon><Picture /></el-icon>
          <span>全部照片</span>
        </el-menu-item>
        
        <el-menu-item index="/albums">
          <el-icon><Folder /></el-icon>
          <span>我的相册</span>
        </el-menu-item>
        
        <el-menu-item index="/upload">
          <el-icon><Upload /></el-icon>
          <span>上传照片</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  }
}
</script>

<style scoped lang="scss">
.main-layout {
  height: 100vh;
  
  .sidebar {
    background-color: #304156;
    
    .logo {
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      border-bottom: 1px solid #1f2d3d;
      
      .el-icon {
        margin-right: 12px;
      }
    }
    
    .sidebar-menu {
      border-right: none;
      
      .el-menu-item {
        height: 50px;
        line-height: 50px;
        
        &:hover {
          background-color: #263445 !important;
        }
        
        .el-icon {
          margin-right: 12px;
        }
      }
    }
  }
  
  .header {
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: flex-end;
    
    .header-right {
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 8px 12px;
        border-radius: 4px;
        transition: background-color 0.3s;
        
        &:hover {
          background-color: #f5f7fa;
        }
        
        .username {
          margin: 0 8px;
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
  
  .main-content {
    background-color: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
  }
}
</style>
