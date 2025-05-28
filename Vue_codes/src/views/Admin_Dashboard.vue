<template>
  <div>
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="left-section">
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 管理员主页</span>
      </div>
      <div class="right-section">
        <div class="user-info" @click="toggleUserDropdown">
          <div class="user-avatar">
            <FontAwesomeIcon icon="fas fa-user-graduate" />
          </div>
          <span class="user-name">{{
            JSON.stringify(loginUserStore.loginUser.name)
          }}</span>
          <FontAwesomeIcon
            :icon="
              isUserDropdownOpen
                ? ['fas', 'fa-angle-up']
                : ['fas', 'fa-angle-down']
            "
          />
        </div>
        <div
          class="user-dropdown-menu"
          :style="{ display: isUserDropdownOpen ? 'block' : 'none' }"
        >
          <a @click="logChange">修改密码</a>
          <div class="divider"></div>
          <a @click="logout">退出登录</a>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="page-main">
      <div class="container dashboard-container">
        <div class="welcome-banner admin-banner">
          <h1>欢迎回来，{{ adminData.displayName }}！</h1>
          <p>今天是 {{ currentDate }}</p>
        </div>

        <!-- Quick Actions - Large Card Style -->
        <section class="quick-actions-large">
          <div class="large-actions-grid">
            <router-link
              to="../admin/user-management"
              class="action-card-large card-style-select-course"
            >
              <div>
                <div class="card-large-title">用户管理</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'calendar-check']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="../admin/course-management"
              class="action-card-large card-style-my-courses"
            >
              <div>
                <div class="card-large-title">课程管理</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'list-alt']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="../admin/approve"
              class="action-card-large card-style-search-course"
            >
              <div>
                <div class="card-large-title">审核成绩修改</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'search']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="/admin/profile"
              class="action-card-large card-style-profile"
            >
              <div>
                <div class="card-large-title">个人信息</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'id-card']"
                  class="card-large-icon"
                />
              </div>
            </router-link>
          </div>
        </section>
        <!-- Removed Notifications and Schedule section -->
        <div
          id="notificationArea"
          class="notification"
          :style="{ display: showNotification ? 'block' : 'none' }"
        >
          {{ notificationMessage }}
        </div>
        <!-- Kept for general page notifications -->
      </div>
    </main>

    <!-- Bottom Bar -->
    <div class="bottom-bar">
      <p class="copyright-text">
        版权所有© Copyright 2025 浙江大学 软件工程基础课程 教学服务系统课程设计
        信息管理子系统
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useuserLoginStore } from "@/store/userLoginStore";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useRouter } from "vue-router";

const router = useRouter();
// 响应式数据
const loginUserStore = useuserLoginStore();
const isUserDropdownOpen = ref(false);
const adminData = ref({
  username: "admin_user",
  displayName: "系统管理员",
});
const currentDate = ref("");
const showNotification = ref(false);
const notificationMessage = ref("");

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value;
};

// 退出登录
const logout = () => {
  showMainNotification("正在退出登录...", "info");
  setTimeout(() => {
    loginUserStore.setLoginUserUnlogin();
    window.location.href = "../login";
  }, 1500);
};

const logChange = () => {
  //window.location.href = "../change-password";
  router.push("/change-password");
};

// 显示通知
const showMainNotification = (message: string, type = "info") => {
  notificationMessage.value = message;
  showNotification.value = true;
  setTimeout(() => {
    showNotification.value = false;
  }, 3000);
};

// 获取当前日期
const getCurrentDate = () => {
  const today = new Date();
  const options = {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  };
  currentDate.value = today.toLocaleDateString("zh-CN", options);
};

// // 获取系统统计信息
// const loadSystemStats = async () => {
//   try {
//     const { data } = await getUsers({}); // 这里简单使用 getUsers 示例
//     console.log("系统统计信息:", data);
//     // 可以根据实际需求更新统计信息
//   } catch (error) {
//     console.error("获取系统统计信息失败:", error);
//     showMainNotification("获取系统统计信息失败", "error");
//   }
// };

onMounted(() => {
  getCurrentDate();
  // loadSystemStats();
});
</script>

<style scoped>
/* 这里可以直接复用之前的样式 */
/* admin_dashboard_style.css */

/* ==========================================================================
   CRITICAL: Global, Top Bar, and Bottom Bar Styles - ENSURE CONSISTENCY
   These styles are copied directly from router-link version confirmed to work correctly
   for top-bar icons and bottom-bar centering.
   ========================================================================== */

/* Global Styles */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  margin: 0;
  background-color: #f4f6f8;
  color: #333;
  line-height: 1.6;
  padding-top: 60px; /* For fixed top-bar */
  padding-bottom: 50px; /* For fixed bottom-bar, increased for safety */
  box-sizing: border-box;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

router-link {
  text-decoration: none;
  color: #409eff;
}
router-link:hover {
  text-decoration: none;
  color: #1370eb;
}

/* Top Bar */
.top-bar {
  height: 60px;
  background-color: #409eff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center; /* Vertical centering for left and right sections */
  padding: 0 30px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-sizing: border-box;
}
.top-bar .left-section {
  display: flex;
  align-items: center;
}
.top-bar .back-icon {
  /* Though admin dashboard might not have one */
  color: white;
  font-size: 20px;
  margin-right: 20px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.top-bar .back-icon:hover {
  opacity: 0.7;
}
.top-bar .system-name {
  font-size: 24px;
  font-weight: bold;
  color: white;
}
.top-bar .system-subname {
  font-size: 16px;
  color: white;
  margin-left: 15px;
}
.top-bar .right-section {
  display: flex;
  align-items: center; /* Vertical centering for user-info block */
}
.top-bar .user-info {
  display: flex;
  align-items: center; /* Vertical centering for avatar, name, arrow */
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: background-color 0.2s;
  position: relative;
}
.top-bar .user-info:hover {
  background-color: #3a8ee6; /* Consistent hover color */
}
.top-bar .user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 8px;
  overflow: hidden;
}
.top-bar .user-avatar i {
  /* Font Awesome icon inside avatar */
  font-size: 18px; /* Corrected icon size */
  color: #409eff; /* Icon color matching theme */
  line-height: 1; /* Crucial for vertical alignment */
}
.top-bar .user-name {
  margin: 0 8px 0 0;
  font-size: 14px; /* Consistent font size */
  color: white;
  line-height: 1; /* Crucial for vertical alignment */
}
.top-bar .user-info .fa-angle-down {
  /* Dropdown arrow */
  font-size: 12px; /* Standard size */
  color: white;
  margin-left: 5px;
  transition: transform 0.2s;
}
.top-bar .user-info.is-open .fa-angle-down {
  transform: rotate(180deg);
}
.top-bar .user-dropdown-menu {
  display: none;
  position: absolute;
  top: calc(100% + 5px);
  right: 0;
  background-color: white;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-width: 130px;
  z-index: 1001;
}
.top-bar .user-dropdown-menu a {
  display: block;
  padding: 8px 15px;
  color: #606266;
  font-size: 14px;
}
.top-bar .user-dropdown-menu a:hover {
  background-color: #ecf5ff;
  color: #409eff;
}
.top-bar .user-dropdown-menu .divider {
  height: 1px;
  background-color: #ebeef5;
  margin: 5px 0;
}

/* Bottom Bar - ENSURING CONSISTENCY */
.bottom-bar {
  background-color: #f5f5f5b7;
  padding: 12px 0;
  text-align: center; /* This centers the .copyright-text */
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  z-index: 999;
  box-sizing: border-box;
}
.copyright-text {
  color: #666;
  font-size: 12.5px;
  margin: 0; /* Remove default paragraph margins */
  line-height: 1.5;
}

/* Notification Area (General) */
.notification {
  padding: 10px 15px;
  margin-top: 20px;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
  border: 1px solid transparent;
}
.notification.success {
  background-color: #f0f9eb;
  color: #67c23a;
  border-color: #e1f3d8;
}
.notification.error {
  background-color: #fef0f0;
  color: #f56c6c;
  border-color: #fde2e2;
}
.notification.info {
  background-color: #edf2fc;
  color: #909399;
  border-color: #e4e7ed;
}

/* ==========================================================================
   Page Specific Styles for Admin Dashboard
   ========================================================================== */

.page-main {
  flex-grow: 1;
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
}
.container.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-banner.admin-banner {
  background: linear-gradient(135deg, #5867dd 0%, #7081f0 100%);
  color: white;
  padding: 25px 30px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(88, 103, 221, 0.2);
}
.welcome-banner h1 {
  margin: 0 0 5px 0;
  font-size: 26px;
  font-weight: 500;
}
.welcome-banner p {
  margin: 0;
  font-size: 15px;
  opacity: 0.9;
}

.section-title {
  font-size: 20px;
  color: #303133;
  margin-bottom: 18px;
  margin-top: 30px;
  font-weight: 500;
  display: flex;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}
.section-title i {
  margin-right: 10px;
  color: #409eff;
  font-size: 1.1em;
}
.system-stats .section-title i {
  color: #e6a23c;
}

/* System Stats Overview */
.system-stats {
  margin-bottom: 30px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}
.stat-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #ebeef5;
}
.stat-icon {
  font-size: 2.5em;
  color: #409eff;
  padding: 10px;
  background-color: #ecf5ff;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-card:nth-child(2) .stat-icon {
  color: #67c23a;
  background-color: #f0f9eb;
}
.stat-card:nth-child(3) .stat-icon {
  color: #e6a23c;
  background-color: #fdf6ec;
}
.stat-card:nth-child(4) .stat-icon {
  color: #f56c6c;
  background-color: #fef0f0;
}
.stat-info {
  flex-grow: 1;
}
.stat-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 2px;
}
.stat-label {
  display: block;
  font-size: 14px;
  color: #606266;
}

/* Quick Actions - Large Card Style */
.quick-actions-large {
  margin-bottom: 30px;
}
.large-actions-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
}
.action-card-large {
  height: 300px;
  width: 200px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  text-align: center;
  margin-top: 20px;
  margin-left: 20px;
  margin-right: 20px;
  margin-bottom: 20px;
  padding: 15px 10px 10px 10px;
  background-color: #fff;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-decoration: none;
  color: #303133;
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}
.action-card-large:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px 0 rgba(0, 0, 0, 0.2), 0 9px 30px 0 rgba(0, 0, 0, 0.19);
  border-color: #b3d8ff;
  color: #409eff;
}
.action-card-large > div {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.card-large-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
  color: inherit;
}
.card-large-divider {
  border: none;
  border-top: 1px solid #e4e7ed;
  width: 85%;
  margin: 0 auto 20px auto;
}
.card-large-icon {
  font-size: 100px;
  margin-top: 10px;
  color: inherit;
}

.action-card-large.card-style-user-manage .card-large-icon {
  color: #409eff;
}
.action-card-large.card-style-course-manage .card-large-icon {
  color: #67c23a;
}
.action-card-large.card-style-grade-approve .card-large-icon {
  color: #e6a23c;
}
.action-card-large.card-style-query-all .card-large-icon {
  color: #f56c6c;
}
.action-card-large:hover .card-large-title {
  color: #409eff;
}
.action-card-large:hover .card-large-icon {
  color: #409eff;
}

/* Recent Activity Section (Optional) */
.recent-activity {
  margin-bottom: 30px;
}
.info-card {
  /* Reused for activity */
  background-color: #fff;
  border-radius: 8px;
  padding: 20px 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
}
.recent-activity .section-title i {
  color: #909399;
}
.item-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 250px;
  overflow-y: auto;
}
.item-list li {
  padding: 10px 0;
  border-bottom: 1px dashed #ebeef5;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}
.item-list li:last-child {
  border-bottom: none;
}
.item-list .no-items {
  text-align: center;
  padding: 20px 0;
  color: #909399;
  font-style: italic;
  border-bottom: none;
}
.view-all-link {
  display: block;
  text-align: right;
  font-size: 14px;
  color: #409eff;
  margin-top: 10px;
}
.view-all-link:hover {
  text-decoration: underline;
}

/* Responsive Adjustments */
@media (max-width: 1200px) {
  .large-actions-grid {
    justify-content: center;
  }
}
@media (max-width: 768px) {
  /* .top-bar related responsive styles are now in the common section at the top */
  .page-main {
    padding: 15px;
  }
  .welcome-banner {
    padding: 20px;
  }
  .welcome-banner h1 {
    font-size: 22px;
  }
  .section-title {
    font-size: 18px;
  }
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
  .stat-value {
    font-size: 24px;
  }

  .large-actions-grid {
    justify-content: space-around;
    margin-left: 0;
    margin-right: 0;
  }
  .action-card-large {
    width: calc(50% - 20px);
    height: 260px;
    margin: 10px;
  }
  .card-large-title {
    font-size: 18px;
  }
  .card-large-icon {
    font-size: 80px;
  }
}
@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .action-card-large {
    width: calc(100% - 20px);
    height: auto;
    padding-top: 25px;
    padding-bottom: 25px;
  }
  .card-large-title {
    font-size: 20px;
  }
  .card-large-icon {
    font-size: 90px;
  }
}
</style>
