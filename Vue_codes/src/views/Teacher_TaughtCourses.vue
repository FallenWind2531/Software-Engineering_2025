<template>
  <div>
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="left-section">
        <router-link
          to="../teacher/dashboard"
          class="back-icon"
          id="backToDashboard"
        >
          <FontAwesomeIcon icon="fas fa-arrow-left" />
        </router-link>
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 我的课程</span>
      </div>
      <div class="right-section">
        <div class="user-info" id="userInfoToggle" @click="toggleUserDropdown">
          <div class="user-avatar">
            <FontAwesomeIcon icon="fas fa-user-shield" />
          </div>
          <span class="user-name" id="approveGradeAdminName">{{
            loginUserStore.loginUser.name
          }}</span>
          <FontAwesomeIcon
            :icon="
              isUserDropdownVisible ? 'fas fa-angle-up' : 'fas fa-angle-down'
            "
          />
        </div>
        <div
          class="user-dropdown-menu"
          id="userDropdown"
          :style="{ display: isUserDropdownVisible ? 'block' : 'none' }"
        >
          <a @click="handleChangePassword">修改密码</a>
          <div class="divider"></div>
          <a @click="handleLogout">退出登录</a>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="page-main">
      <div class="container">
        <div class="card query-filter-card">
          <h2 class="card-title">课程筛选条件</h2>
          <form
            @submit.prevent="queryCourses"
            id="queryCourseForm"
            class="query-form-grid"
          >
            <div class="form-group">
              <label for="courseName">课程名称:</label>
              <input
                type="text"
                id="courseName"
                v-model="filters.course_name"
                placeholder="输入课程名关键词"
              />
            </div>
            <div class="form-group">
              <label for="courseType">课程类别:</label>
              <select id="courseType" v-model="filters.category">
                <option value="">全部类别</option>
                <option value="普通">普通</option>
                <option value="实验">实验</option>
                <option value="体育">体育</option>
              </select>
            </div>
            <div class="form-action-group">
              <button type="submit" class="btn btn-primary">
                <FontAwesomeIcon icon="fas fa-search" /> 查 询
              </button>
              <button
                type="reset"
                class="btn btn-secondary"
                @click="resetFilterForm"
              >
                <FontAwesomeIcon icon="fas fa-undo" /> 重 置
              </button>
            </div>
          </form>
        </div>

        <div class="card course-results-card" id="courseResultsCard">
          <h2 class="card-title">查询结果</h2>
          <div class="table-responsive">
            <table id="courseTable">
              <thead>
                <tr>
                  <th>课程名称</th>
                  <th>课程描述</th>
                  <th>授课教师</th>
                  <th>学分</th>
                  <th>周学时</th>
                  <th>课程类别</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody id="courseListBody">
                <template v-if="isLoading">
                  <tr>
                    <td colspan="7" class="loading-placeholder">
                      正在加载数据... <i class="fas fa-spinner fa-spin"></i>
                    </td>
                  </tr>
                </template>
                <template v-else-if="courses.length === 0">
                  <tr>
                    <td colspan="7" class="no-data-placeholder">
                      未查询到符合条件的课程。
                    </td>
                  </tr>
                </template>
                <template v-else>
                  <tr v-for="course in paginatedCourses" :key="course.id">
                    <td>{{ course.course_name }}</td>
                    <td>{{ course.course_description }}</td>
                    <td>{{ course.teacher_name }}</td>
                    <td>{{ course.credit }}</td>
                    <td>{{ course.hours_per_week }}</td>
                    <td>{{ course.category }}</td>
                    <td>
                      <button
                        class="btn btn-sm btn-danger"
                        @click="deleteTheCourse(course.course_id)"
                      >
                        <FontAwesomeIcon icon="fas fa-trash" /> 删除
                      </button>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
          <!-- 分页控件 -->
          <div
            class="pagination-controls"
            id="paginationControls"
            :style="{ display: courses.length > 0 ? 'flex' : 'none' }"
          >
            <button
              class="btn btn-sm btn-default"
              id="prevPageBtn"
              :disabled="currentPage === 1"
              @click="prevPage"
            >
              « 上一页
            </button>
            <span id="pageInfo"
              >第 {{ currentPage }} / {{ totalPages }} 页 (共
              {{ courses.length }} 条)</span
            >
            <button
              class="btn btn-sm btn-default"
              id="nextPageBtn"
              :disabled="currentPage === totalPages || totalPages === 0"
              @click="nextPage"
            >
              下一页 »
            </button>
          </div>
        </div>
        <div class="container">
          <div class="card create-course-card">
            <h2 class="card-title">新建课程</h2>
            <form
              @submit.prevent="createTheCourse"
              id="createCourseForm"
              class="create-form-grid"
            >
              <div class="form-group">
                <label for="newCourseName">课程名称:</label>
                <input
                  type="text"
                  id="newCourseName"
                  v-model="newCourse.course_name"
                  placeholder="输入课程名称"
                  required
                />
              </div>
              <div class="form-group">
                <label for="newCourseDescription">课程描述:</label>
                <textarea
                  id="newCourseDescription"
                  v-model="newCourse.course_description"
                  placeholder="输入课程描述"
                ></textarea>
              </div>
              <div class="form-group">
                <label for="newCourseTeacher">授课教师:</label>
                <input
                  type="text"
                  id="newCourseTeacher"
                  v-model="newCourse.teacher_name"
                  placeholder="输入授课教师姓名"
                  required
                />
              </div>
              <div class="form-group">
                <label for="newCourseTeacher">学分:</label>
                <input
                  type="text"
                  id="newCourseTeacher"
                  v-model="newCourse.credit"
                  placeholder="输入课程学分"
                  required
                />
              </div>
              <div class="form-group">
                <label for="newCourseTeacher">周学时:</label>
                <input
                  type="text"
                  id="newCourseTeacher"
                  v-model="newCourse.hours_per_week"
                  placeholder="输入周学时(整数)"
                  required
                />
              </div>
              <div class="form-group">
                <label for="newCourseCategory">课程类别:</label>
                <select id="newCourseCategory" v-model="newCourse.category">
                  <option value="普通">普通</option>
                  <option value="实验">实验</option>
                  <option value="体育">体育</option>
                </select>
              </div>
              <div class="form-action-group">
                <button type="submit" class="btn btn-primary">
                  <FontAwesomeIcon icon="fas fa-plus" /> 创 建
                </button>
                <button
                  type="reset"
                  class="btn btn-secondary"
                  @click="resetCreateForm"
                >
                  <FontAwesomeIcon icon="fas fa-undo" /> 重 置
                </button>
              </div>
            </form>
          </div>
        </div>
        <div
          id="notificationArea"
          class="notification"
          :style="{ display: isNotificationVisible ? 'block' : 'none' }"
          :class="notificationType"
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
import { getMyCourses, createCourse, deleteCourse } from "@/api/teacher";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useuserLoginStore } from "@/store/userLoginStore";
import { useRouter } from "vue-router";

const loginUserStore = useuserLoginStore();
const router = useRouter();

type Course = {
  course_id: number;
  course_name: string;
  course_description: string;
  teacher_id: number;
  teacher_name: string; // 联查得到
  credit: number;
  category: string;
  hours_per_week: number;
};

const isUserDropdownVisible = ref(false);
const filters = ref({
  course_name: "",
  category: "",
});
const courses = ref<Course[]>([]);
const currentPage = ref(1);
const itemsPerPage = ref(10);
const isLoading = ref(false);
const isNotificationVisible = ref(false);
const notificationMessage = ref("");
const notificationType = ref("info");

// 计算总页数
const totalPages = ref(0);
const paginatedCourses = ref([]);

const showCreateCourseForm = ref(false);
const newCourse = ref<Partial<Course>>({});

// 切换用户下拉菜单显示状态
const toggleUserDropdown = () => {
  isUserDropdownVisible.value = !isUserDropdownVisible.value;
};

const handleLogout = () => {
  showNotification("正在退出登录...", "info");
  setTimeout(() => {
    loginUserStore.setLoginUserUnlogin();
    window.location.href = "../login";
  }, 1500);
};

// 处理修改密码
const handleChangePassword = () => {
  //window.location.href = "../change-password";
  router.push("/change-password");
};

// 显示通知消息
const showNotification = (message: string, type = "info") => {
  notificationMessage.value = message;
  notificationType.value = type;
  isNotificationVisible.value = true;
  setTimeout(() => {
    isNotificationVisible.value = false;
  }, 3000);
};

// 计算分页数据
const calculatePaginatedCourses = () => {
  const startIndex = (currentPage.value - 1) * itemsPerPage.value;
  const endIndex = startIndex + itemsPerPage.value;
  paginatedCourses.value = courses.value.slice(startIndex, endIndex);
  totalPages.value = Math.ceil(courses.value.length / itemsPerPage.value);
};

// 查询课程
const queryCourses = async () => {
  isLoading.value = true;
  showNotification("正在查询课程...", "info");
  try {
    const response = await getMyCourses(filters.value);
    courses.value = response.data.data.items;
    currentPage.value = 1;
    calculatePaginatedCourses();
    if (courses.value.length > 0) {
      showNotification(
        `查询到 ${courses.value.length} 条符合条件的课程。`,
        "success"
      );
    } else {
      showNotification("未查询到符合条件的课程。", "info");
    }
  } catch (error) {
    showNotification("查询课程失败，请稍后重试。", "error");
    console.error("查询课程失败:", error);
  } finally {
    isLoading.value = false;
  }
};

// 重置表单
const resetFilterForm = () => {
  filters.value = {
    course_name: "",
    category: "",
  };
  courses.value = [];
  currentPage.value = 1;
  showNotification("筛选条件已重置。", "info");
};

const resetCreateForm = () => {
  newCourse.value = {
    course_name: "",
    course_description: "",
    credit: null,
    hours_per_week: null,
    category: "",
  };
  showNotification("新建课程已重置。", "info");
};

// 上一页
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    calculatePaginatedCourses();
  }
};

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    calculatePaginatedCourses();
  }
};

//删除课程
const deleteTheCourse = async (courseId: number) => {
  if (confirm("确定要删除该课程吗？")) {
    isLoading.value = true;
    showNotification("正在删除课程...", "info");
    try {
      await deleteCourse(courseId);
      courses.value = courses.value.filter(
        (course) => course.course_id !== courseId
      );
      calculatePaginatedCourses();
      showNotification("课程删除成功。", "success");
    } catch (error) {
      showNotification("删除课程失败，请稍后重试。", "error");
      console.error("删除课程失败:", error);
    } finally {
      isLoading.value = false;
    }
  }
};

// 新增：新建课程
const createTheCourse = async () => {
  isLoading.value = true;
  showNotification("正在创建课程...", "info");
  try {
    const response = await createCourse(newCourse.value);
    courses.value.push(response.data);
    calculatePaginatedCourses();
    showCreateCourseForm.value = false;
    newCourse.value = {};
    showNotification("课程创建成功。", "success");
  } catch (error) {
    showNotification("创建课程失败，请稍后重试。", "error");
    console.error("创建课程失败:", error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", (event) => {
    const userInfoToggle = document.getElementById("userInfoToggle");
    const userDropdown = document.getElementById("userDropdown");
    if (
      isUserDropdownVisible.value &&
      userInfoToggle &&
      userDropdown &&
      !userInfoToggle.contains(event.target as Node) &&
      !userDropdown.contains(event.target as Node)
    ) {
      isUserDropdownVisible.value = false;
    }
  });
  queryCourses();
});
</script>

<style scoped>
/* 原有的样式代码保持不变 */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  margin: 0;
  background-color: #f8f9fa;
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
  text-decoration: underline;
  color: #1370eb;
}

/* Top Bar (与之前页面一致) */
.top-bar {
  /* ... (与 submit_grades_style.css 中的 .top-bar 样式完全一致) ... */
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
  /* ... (与 submit_grades_style.css 中的 .user-info 样式完全一致) ... */
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
.top-bar .user-dropdown-menu a {
  display: block;
  padding: 8px 15px;
  color: #606266;
  font-size: 14px;
}
.top-bar .user-dropdown-menu a:hover {
  background-color: #ecf5ff;
  color: #409eff;
  text-decoration: none;
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
.container {
  max-width: 1200px;
  margin: 0 auto;
} /* 稍宽一些以容纳更多列 */

/* Cards */
.card {
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 20px 25px;
  margin-bottom: 25px;
}
.card-title {
  font-size: 18px;
  color: #303133;
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.create-form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); /* 响应式列 */
  gap: 15px 20px; /* 行间隙 列间隙 */
  align-items: end; /* 使按钮与最后一行的输入框底部对齐 */
}
.create-form-grid .form-group {
  margin-bottom: 0;
} /* grid的gap处理间距 */
.create-form-grid .form-action-group {
  grid-column: auto; /* 让按钮组横跨所有列，或根据需要调整 */
  display: flex;
  justify-content: flex-end; /* 按钮靠右 */
  gap: 10px;
  margin-top: 10px; /* 与上方筛选条件的一些间距 */
}

/* Query Form Grid */
.query-form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); /* 响应式列 */
  gap: 15px 20px; /* 行间隙 列间隙 */
  align-items: end; /* 使按钮与最后一行的输入框底部对齐 */
}
.query-form-grid .form-group {
  margin-bottom: 0;
} /* grid的gap处理间距 */
.query-form-grid .form-action-group {
  //grid-column: 1 / -1; /* 让按钮组横跨所有列，或根据需要调整 */
  display: flex;
  justify-content: flex-end; /* 按钮靠右 */
  gap: 10px;
  margin-top: 10px; /* 与上方筛选条件的一些间距 */
}

/* Forms (与之前页面类似) */
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}
.form-group select,
.form-group input[type="text"] {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
}
.form-group select:focus,
.form-group input[type="text"]:focus {
  border-color: #409eff;
  outline: 0;
}
.form-group select {
  /* ... (与 submit_grades_style.css 中 select 样式一致) ... */
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg class='icon' viewBox='0 0 1024 1024' xmlns='http://www.w3.org/2000/svg' width='12' height='12'%3E%3Cpath d='M512 714.666667c-8.533333 0-17.066667-2.133333-23.466667-8.533334l-307.2-268.8c-12.8-10.666667-14.933333-29.866667-4.266666-42.666666 10.666667-12.8 29.866667-14.933333 42.666666-4.266667l292.266667 256 292.266667-256c12.8-10.666667 32-8.533333 42.666667 4.266667s8.533333 32-4.266667 42.666666l-307.2 268.8c-6.4 4.266667-12.8 8.533333-21.333333 8.533334z' fill='%23C0C4CC'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
  padding-right: 30px;
}

/* Buttons (与之前页面类似) */
.btn {
  /* ... (与 submit_grades_style.css 中的 .btn 样式一致) ... */
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
.btn-default {
  /* 用于分页按钮 */
  background-color: #fff;
  border-color: #dcdfe6;
  color: #606266;
}
.btn-default:hover {
  border-color: #409eff;
  color: #409eff;
}
.btn[disabled] {
  cursor: not-allowed;
  opacity: 0.6;
}
.btn-sm {
  padding: 5px 10px;
  font-size: 12px;
}

/* Tables (与 submit_grades_style.css 中的 table 样式一致) */
.table-responsive {
  /* ... */
  overflow-x: auto;
  margin-top: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
table {
  /* ... */
  width: 100%;
  border-collapse: collapse;
  color: #606266;
  font-size: 14px;
}
th,
td {
  /* ... */
  border-bottom: 1px solid #ebeef5;
  padding: 12px 15px;
  text-align: left;
  vertical-align: middle;
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
.no-data-placeholder td,
.loading-placeholder td {
  /* 用于表格内提示 */
  text-align: center;
  color: #909399;
  padding: 30px 15px;
  font-style: italic;
}

/* Pagination Controls */
.pagination-controls {
  margin-top: 20px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}
.pagination-controls #pageInfo {
  font-size: 14px;
  color: #606266;
}

/* Notification Area (与之前页面一致) */
.notification {
  /* ... */
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

/* Bottom Bar (与之前页面一致) */
.bottom-bar {
  /* ... */
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
  /* 调整断点以适应更多筛选条件 */
  .query-form-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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
  } /* 小屏幕隐藏子系统名 */
  .page-main {
    padding: 15px;
  }
  .query-form-grid .form-action-group {
    justify-content: center;
  }
  .query-form-grid .form-action-group .btn {
    flex-grow: 1;
    max-width: 150px;
  }
  th,
  td {
    padding: 10px 8px;
    font-size: 13px;
  }
}
</style>
