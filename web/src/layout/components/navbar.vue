<template>
  <div class='navbar-container'>
    <el-dropdown>
      <div class="avatar-wrapper">
        <img src="@/assets/images/avatar.jpg" class="user-avatar" alt="">
        <span class="username"> 管理员 </span>
        <i class="el-icon-caret-bottom"/>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item>
            <span> 个人中心 </span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout"><span>退出登录</span></el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
const logout = () => {
};
</script>

<style scoped lang='scss'>
.navbar-container {
  display: flex;
  // 子元素向右对齐
  justify-content: flex-end;
  // 垂直居中
  align-items: center;
  height: 50px;

  .avatar-wrapper {
    // 头像图片
    .user-avatar {
      cursor: pointer;
      width: 35px;
      height: 35px;
      border-radius: 50%;
    }

    &.hover {
      // 鼠标指针变成手势
      cursor: pointer;
    }

    .username {
      padding: 0 10px;
    }
  }

  // el-dropdown 鼠标移入会有一个蓝色边框, 这里给他去掉
  :focus-visible {
    outline: none;
  }
}
</style>