<template>
  <div class="layout-container">
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <h1 class="logo">个人相册管理系统</h1>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="User" />
              <span class="username">{{ userStore.user?.username }}</span>
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
      <el-container>
        <el-aside width="200px" class="layout-aside">
          <el-menu
            :default-active="activeMenu"
            router
            class="aside-menu"
          >
            <el-menu-item index="/all-photos">
              <el-icon><Picture /></el-icon>
              <span>全部照片</span>
            </el-menu-item>
            <el-menu-item index="/my-albums">
              <el-icon><Folder /></el-icon>
              <span>我的相册</span>
            </el-menu-item>
            <el-menu-item index="/upload">
              <el-icon><Upload /></el-icon>
              <span>图片管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="layout-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => {
  return route.path
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  width: 100%;
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  
  .header-left {
    .logo {
      font-size: 20px;
      color: #303133;
      font-weight: 500;
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin: 0 8px;
        color: #606266;
      }
    }
  }
}

.layout-aside {
  background: #fff;
  box-shadow: 1px 0 4px rgba(0, 0, 0, 0.1);
  
  .aside-menu {
    height: 100%;
    border-right: none;
  }
}

.layout-main {
  background: #f5f7fa;
  overflow-y: auto;
}
</style>
