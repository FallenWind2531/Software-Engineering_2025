<template>
  <div>
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="left-section">
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 学生主页</span>
      </div>
      <div class="right-section">
        <div class="user-info" @click="toggleUserDropdown">
          <div class="user-avatar">
            <font-awesome-icon :icon="['fas', 'user-graduate']" />
          </div>
          <span class="user-name">{{ loginUserStore.loginUser.name }}</span>
          <font-awesome-icon
            :icon="
              userDropdownOpen
                ? ['fas', 'fa-angle-up']
                : ['fas', 'fa-angle-down']
            "
          />
        </div>
        <div
          class="user-dropdown-menu"
          :style="{ display: userDropdownOpen ? 'block' : 'none' }"
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
        <div class="welcome-banner">
          <h1>欢迎回来，{{ loginUserStore.loginUser.name }}同学！</h1>
          <p>今天是 {{ currentDate }}</p>
        </div>

        <!-- Quick Actions - Large Card Style -->
        <section class="quick-actions-large">
          <div class="large-actions-grid">
            <router-link
              to="/query-course"
              class="action-card-large card-style-select-course"
            >
              <div>
                <div class="card-large-title">去选课</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'calendar-check']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="/student/selected-courses"
              class="action-card-large card-style-my-courses"
            >
              <div>
                <div class="card-large-title">已选课程</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'list-alt']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="/student/query-grades"
              class="action-card-large card-style-grades"
            >
              <div>
                <div class="card-large-title">成绩查询</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'graduation-cap']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="../query-course"
              class="action-card-large card-style-search-course"
            >
              <div>
                <div class="card-large-title">课程查询</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'search']"
                  class="card-large-icon"
                />
              </div>
            </router-link>

            <router-link
              to="/student/profile"
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

            <router-link
              to="/student/grade-analysis"
              class="action-card-large card-style-analysis"
            >
              <div>
                <div class="card-large-title">学情分析</div>
                <hr class="card-large-divider" />
                <font-awesome-icon
                  :icon="['fas', 'chart-pie']"
                  class="card-large-icon"
                />
              </div>
            </router-link>
          </div>
        </section>
        <div
          id="notificationArea"
          class="notification"
          :class="notificationType"
          v-if="showNotification"
        >
          {{ notificationMessage }}
        </div>
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
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useuserLoginStore } from "@/store/userLoginStore";
import { useRouter } from "vue-router";
// 响应式数据
// 状态管理
const loginUserStore = useuserLoginStore();
const studentName = ref("学生姓名");
const currentDate = ref("");
const userDropdownOpen = ref(false);
const showNotification = ref(false);
const notificationMessage = ref("");
const notificationType = ref("");
const router = useRouter();
// 切换用户下拉菜单
const toggleUserDropdown = () => {
  userDropdownOpen.value = !userDropdownOpen.value;
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

// 显示通知消息
const showMainNotification = (message: string, type = "info") => {
  notificationMessage.value = message;
  showNotification.value = true;
  setTimeout(() => {
    showNotification.value = false;
  }, 3000);
};

const getCurrentDate = () => {
  const today = new Date();
  const options: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  };
  currentDate.value = today.toLocaleDateString("zh-CN", options);
};

// 初始化数据
onMounted(async () => {
  getCurrentDate();
});
</script>

<style scoped>
/* Global Styles */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  margin: 0;
  background-color: #f4f6f8;
  color: #333;
  line-height: 1.6;
  padding-top: 60px;
  padding-bottom: 40px;
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
  align-items: center;
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
  align-items: center;
}
.top-bar .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.2s;
  position: relative;
}
.top-bar .user-info:hover {
  background-color: #1370eb;
}
.top-bar .user-avatar {
  font-size: 22px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #fff;
  color: #409eff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 8px;
}
.top-bar .user-name {
  margin: 0 8px 0 0;
  font-size: 15px;
  color: white;
}
.top-bar .user-info .fa-angle-down {
  font-size: 12px;
  color: white;
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

/* Main Content */
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

.welcome-banner {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  padding: 25px 30px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.2);
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

/* Quick Actions - Large Card Style */
.quick-actions-large {
  margin-bottom: 30px;
  margin-top: 20px;
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

.action-card-large.card-style-select-course .card-large-icon {
  color: #67c23a;
}
.action-card-large.card-style-my-courses .card-large-icon {
  color: #303133;
}
.action-card-large.card-style-grades .card-large-icon {
  color: #e6a23c;
}
.action-card-large.card-style-search-course .card-large-icon {
  color: #409eff;
}
.action-card-large.card-style-profile .card-large-icon {
  color: #909399;
}
.action-card-large.card-style-analysis .card-large-icon {
  color: #f56c6c;
}

.action-card-large:hover .card-large-title {
  color: #409eff;
}
.action-card-large:hover .card-large-icon {
  color: #409eff;
}

/* Notification Area */
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

/* Bottom Bar */
.bottom-bar {
  background-color: #f5f5f5b7;
  padding: 12px 0;
  text-align: center;
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
  margin: 0;
  line-height: 1.5;
}

/* Responsive Adjustments */
@media (max-width: 1200px) {
  .large-actions-grid {
    justify-content: center;
  }
}
@media (max-width: 768px) {
  .top-bar {
    padding: 0 15px;
    height: 50px;
  }
  body {
    padding-top: 50px;
  }
  .top-bar .system-name {
    font-size: 18px;
  }
  .top-bar .system-subname {
    display: none;
  }
  .page-main {
    padding: 15px;
  }
  .welcome-banner {
    padding: 20px;
  }
  .welcome-banner h1 {
    font-size: 22px;
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
