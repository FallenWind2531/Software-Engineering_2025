<template>
  <div>
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="left-section">
        <router-link
          to="../student/dashboard"
          class="back-icon"
          id="backToDashboard"
        >
          <FontAwesomeIcon icon="fas fa-arrow-left" />
        </router-link>
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 已选课程</span>
      </div>
      <div class="right-section">
        <div class="user-info" id="userInfoToggle" @click="toggleUserDropdown">
          <div class="user-avatar">
            <FontAwesomeIcon icon="fas fa-user-graduate" />
          </div>
          <span class="user-name" id="selectedCoursesUserName">{{
            loginUserStore.loginUser.name
          }}</span>
          <FontAwesomeIcon
            :icon="
              userDropdownVisible ? 'fas fa-angle-up' : 'fas fa-angle-down'
            "
          />
        </div>
        <div
          class="user-dropdown-menu"
          id="userDropdown"
          :style="{ display: userDropdownVisible ? 'block' : 'none' }"
        >
          <a @click="handleChangePassword">修改密码</a>
          <div class="divider"></div>
          <a @click="handleLogout">退出登录</a>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="page-main">
      <div class="container selected-courses-container">
        <div class="card filter-card">
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-filter" /> 筛选已选课程
          </h2>
          <form id="filterSelectedCoursesForm" class="filter-form-grid">
            <div class="form-group">
              <label for="filterSemester">选择学期:</label>
              <select id="filterSemester" name="semester">
                <option value="all">全部学期</option>
                <option value="2024-2025-1" selected>2024-2025 秋季学期</option>
                <!-- Default to current or latest -->
                <option value="2023-2024-2">2023-2024 春季学期</option>
                <option value="2023-2024-1">2023-2024 秋季学期</option>
              </select>
            </div>
            <div class="form-group">
              <label for="filterCourseType">课程类别:</label>
              <select id="filterCourseType" name="courseType">
                <option value="all">全部类别</option>
                <option value="compulsory">公共必修</option>
                <option value="specialized_compulsory">专业必修</option>
                <option value="elective">通识选修</option>
                <option value="specialized_elective">专业选修</option>
              </select>
            </div>
            <div class="form-action-group">
              <button type="submit" class="btn btn-primary">
                <FontAwesomeIcon icon="fas fa-search" /> 应用筛选
              </button>
              <button
                type="reset"
                class="btn btn-secondary"
                id="resetFiltersBtn"
              >
                <FontAwesomeIcon icon="fas fa-undo" /> 重置筛选
              </button>
            </div>
          </form>
        </div>

        <div class="card courses-list-card">
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-list-ul" /> 我的课程列表 (<span
              id="currentFilterDisplay"
              >当前学期</span
            >)
          </h2>
          <div class="table-responsive">
            <table id="selectedCoursesTable">
              <thead>
                <tr>
                  <th>课程编号</th>
                  <th>课程名称</th>
                  <th>授课教师</th>
                  <th>学分</th>
                  <th>课程类别</th>
                  <th>选课学期</th>
                  <th>上课时间与地点</th>
                  <th>成绩</th>
                  <!-- <th>操作</th> -->
                </tr>
              </thead>
              <tbody id="selectedCoursesListBody">
                <!-- JS动态填充 -->
                <tr>
                  <td colspan="8" class="placeholder-row">
                    正在加载已选课程...
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="summary-info">
            总计学分：<span id="totalCredits">0</span>
          </div>
        </div>
        <div
          id="notificationArea"
          class="notification"
          style="display: none"
        ></div>
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
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useuserLoginStore } from "@/store/userLoginStore";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const loginUserStore = useuserLoginStore();
const userDropdownVisible = ref(false);

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  userDropdownVisible.value = !userDropdownVisible.value;
};

// 处理退出登录
const handleLogout = () => {
  loginUserStore.setLoginUserUnlogin();
  router.push("/login");
};

// 处理修改密码
const handleChangePassword = () => {
  router.push("/change-password");
};

// 跳转到个人信息页面
const goToProfile = () => {
  router.push("/student/profile");
};
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

/* Top Bar (与之前页面一致) */
.top-bar {
  /* ... (与 student_profile_style.css 中的 .top-bar 样式完全一致) ... */
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
.top-bar .back-icon {
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
  align-items: center;
}
.top-bar .user-info {
  /* ... */
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
  /* ... */
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
  /* ... */
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
.top-bar .user-dropdown-menu router-link {
  display: block;
  padding: 8px 15px;
  color: #606266;
  font-size: 14px;
}
.top-bar .user-dropdown-menu router-link:hover {
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
  padding: 25px;
  width: 100%;
  box-sizing: border-box;
}
.container.selected-courses-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* Card Styles */
.card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  padding: 20px 25px;
  margin-bottom: 25px;
}
.card-title {
  font-size: 18px;
  color: #303133;
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
  display: flex;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}
.card-title i {
  margin-right: 10px;
  color: #409eff;
  font-size: 1.05em;
}

/* Filter Form Grid */
.filter-form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px 20px;
  align-items: end;
}
.filter-form-grid .form-group {
  margin-bottom: 0;
}
.filter-form-grid .form-action-group {
  grid-column: 1 / -1; /* Buttons span all columns on larger screens */
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
@media (min-width: 800px) {
  /* Adjust breakpoint as needed */
  .filter-form-grid .form-action-group {
    grid-column: auto; /* Allow button group to be router-link regular grid item */
    align-self: end; /* Align with other form elements */
  }
}

/* Forms (Similar to previous pages) */
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}
.form-group select {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg class='icon' viewBox='0 0 1024 1024' xmlns='http://www.w3.org/2000/svg' width='12' height='12'%3E%3Cpath d='M512 714.666667c-8.533333 0-17.066667-2.133333-23.466667-8.533334l-307.2-268.8c-12.8-10.666667-14.933333-29.866667-4.266666-42.666666 10.666667-12.8 29.866667-14.933333 42.666666-4.266667l292.266667 256 292.266667-256c12.8-10.666667 32-8.533333 42.666667 4.266667s8.533333 32-4.266667 42.666666l-307.2 268.8c-6.4 4.266667-12.8 8.533333-21.333333 8.533334z' fill='%23C0C4CC'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
  padding-right: 30px;
}
.form-group select:focus {
  border-color: #409eff;
  outline: 0;
}

/* Buttons (Similar to previous pages) */
.btn {
  /* ... */
  padding: 9px 18px;
  border: 1px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: 0.1s;
  text-align: center;
  user-select: none;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.btn i {
  font-size: 1em;
}
.btn-primary {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}
.btn-primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}
.btn-secondary {
  background-color: #909399;
  border-color: #909399;
  color: white;
}
.btn-secondary:hover {
  background-color: #a6a9ad;
  border-color: #a6a9ad;
}

/* Courses Table (Similar to query_course.html) */
.table-responsive {
  overflow-x: auto;
  margin-top: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
table {
  width: 100%;
  border-collapse: collapse;
  color: #606266;
  font-size: 14px;
}
th,
td {
  border-bottom: 1px solid #ebeef5;
  padding: 10px 12px; /* Slightly less padding for more info */
  text-align: left;
  vertical-align: middle;
  white-space: nowrap; /* Prevent long text wrapping aggressively */
}
thead {
  background-color: #f5f7fa;
  color: #909399;
}
th {
  font-weight: 500;
}
tbody tr:hover {
  background-color: #f5f7fa;
}
.placeholder-row td,
.no-courses-row td {
  text-align: center;
  color: #909399;
  padding: 30px 15px;
  font-style: italic;
}
td.grade-cell {
  font-weight: bold;
} /* Style for grade cell */
td.grade-cell.pass {
  color: #67c23a;
}
td.grade-cell.fail {
  color: #f56c6c;
}
td.grade-cell.pending {
  color: #e6a23c;
}

/* Summary Info */
.summary-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  text-align: right;
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}
.summary-info #totalCredits {
  color: #409eff;
  font-size: 1.1em;
}

/* Notification Area */
.notification {
  /* ... (Same as previous pages) ... */
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
  /* ... (Same as previous pages) ... */
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
@media (max-width: 992px) {
  .filter-form-grid {
    grid-template-columns: 1fr;
  } /* Stack filters vertically */
  .filter-form-grid .form-action-group {
    justify-content: center;
  }
  .filter-form-grid .form-action-group .btn {
    flex-grow: 1;
    max-width: 200px;
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
  th,
  td {
    padding: 8px 10px;
    font-size: 13px;
  }
  .summary-info {
    text-align: center;
  }
}
</style>
