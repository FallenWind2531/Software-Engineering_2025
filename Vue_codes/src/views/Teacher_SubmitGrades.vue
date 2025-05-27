<template>
  <div>
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="left-section">
        <router-link
          to="../teacher/dashboard"
          class="back-icon"
          id="backToTeacherDashboard"
        >
          <FontAwesomeIcon icon="fas fa-arrow-left" />
        </router-link>
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 成绩提交</span>
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
      <div class="container">
        <div class="card select-course-card">
          <h2 class="card-title">选择课程进行成绩提交</h2>
          <form id="selectCourseForm">
            <div class="form-row">
              <div class="form-group">
                <label for="requestCourse">选择课程:</label>
                <select
                  id="requestCourse"
                  name="courseId"
                  v-model="selectedCourse"
                  @change="populateSectionSelect"
                >
                  <option
                    v-for="course in courses"
                    :key="course.course_id"
                    :value="course.course_id"
                  >
                    {{ course.course_name }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="requestStudent">选择开课时间:</label>
                <select
                  id="requestStudent"
                  name="studentId"
                  v-model="selectedSection"
                  :disabled="!selectedCourse"
                >
                  <option
                    v-for="section in sections"
                    :key="section.section_id"
                    :value="section.section_id"
                  >
                    {{ section.sec_year }} 学年 {{ section.semester }} 学期
                  </option>
                </select>
              </div>
            </div>
            <div class="form-action-group">
              <button
                type="button"
                class="btn btn-primary"
                id="loadStudentsBtn"
                :disabled="!selectedSection"
                @click="loadStudents"
              >
                <FontAwesomeIcon icon="fas fa-search" /> 加载学生名单
              </button>
            </div>
          </form>
        </div>

        <div
          class="card grade-entry-card"
          id="gradeEntryCard"
          :style="{ display: showGradeEntryCard ? 'block' : 'none' }"
        >
          <h2 class="card-title" id="gradeEntryTitle">成绩录入</h2>
          <div class="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>序号</th>
                  <th>姓名</th>
                  <th>总评成绩(自动计算)</th>
                </tr>
              </thead>
              <tbody id="studentListBody">
                <tr v-if="loadingStudents">
                  <td colspan="3" style="text-align: center; padding: 20px">
                    正在加载学生数据... <i class="fas fa-spinner fa-spin"></i>
                  </td>
                </tr>
                <tr
                  v-else
                  v-for="(studentGrade, index) in studentGrades"
                  :key="studentGrade.student_id"
                >
                  <td>{{ index + 1 }}</td>
                  <td>{{ studentGrade.name }}</td>
                  <td>{{ studentGrade.score }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="action-buttons">
            <button
              type="button"
              class="btn btn-success"
              id="submitAllGradesBtn"
              @click="submitAllGrades"
            >
              <FontAwesomeIcon icon="fas fa-check-circle" /> 全部提交
            </button>
            <!--            <button-->
            <!--              type="button"-->
            <!--              class="btn btn-secondary"-->
            <!--              id="saveDraftBtn"-->
            <!--              @click="saveDraft"-->
            <!--            >-->
            <!--              <FontAwesomeIcon icon="fas fa-save" /> 暂存草稿-->
            <!--            </button>-->
          </div>
        </div>

        <div
          id="notificationArea"
          class="notification"
          :class="notification.type"
          :style="{ display: notification.message ? 'block' : 'none' }"
        >
          {{ notification.message }}
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
import { ref, onMounted, computed } from "vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  getMyCourses,
  getMyCourseSections,
  getSectionGrades,
  submitStudentGrades,
} from "@/api/teacher";
import { useuserLoginStore } from "@/store/userLoginStore";

const loginUserStore = useuserLoginStore();
// 响应式数据
const userDropdownVisible = ref(false);
const selectedCourse = ref("");
const selectedSection = ref("");
const courses = ref([]);
const sections = ref([]);
const students = ref([]);
const grades = ref([]);
const loadingStudents = ref(false);
const showGradeEntryCard = ref(false);
const notification = ref({
  message: "",
  type: "info",
});

// const MockCourses = {
//   item: [
//     {
//       course_id: "121",
//       course_name: "课1",
//     },
//     {
//       course_id: "122",
//       course_name: "课2",
//     },
//     {
//       course_id: "123",
//       course_name: "课3",
//     },
//   ],
// };
//
// const getMyCourses = async (params: any) => {
//   return MockCourses;
// };
//
// const MockSections = {
//   data: [
//     {
//       section_id: "212",
//       sec_year: 1999,
//       semester: "awdawd",
//     },
//     {
//       section_id: "awdwd",
//       sec_year: 2000,
//       semester: "aw22awd",
//     },
//   ],
// };
//
// const getMyCourseSections = async (params: any) => {
//   return MockSections;
// };
//
// const MockSectionGrades = {
//   data: {
//     student_info: [
//       {
//         user_id: "11",
//         name: "wwad",
//       },
//       {
//         user_id: "12",
//         name: "wwa123d",
//       },
//     ],
//     grade_info: [
//       {
//         grade_base: {
//           score: 111,
//         },
//       },
//     ],
//   },
// };
//
// const getSectionGrades = async (params: any) => {
//   return MockSectionGrades;
// };

// 生命周期钩子
onMounted(() => {
  populateCourseSelect();
});

const studentGrades = computed(() => {
  return students.value.map((student, index) => {
    const grade = grades.value[index];
    return {
      student_id: student.user_id,
      name: student.name,
      score: grade.grade_base.score,
      gpa: grade.grade_base.gpa,
    };
  });
});

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  userDropdownVisible.value = !userDropdownVisible.value;
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
  loginUserStore.setLoginUserUnlogin();
  window.location.href = "../change-password";
};

const populateCourseSelect = async () => {
  courses.value = [];
  sections.value = [];
  students.value = [];
  grades.value = [];
  selectedCourse.value = "";
  selectedSection.value = "";
  showGradeEntryCard.value = false;

  try {
    const response = await getMyCourses({
      page: null,
      size: null,
      course_name: "",
      teacher_id: "",
      teacher_name: "",
      category: "",
    });
    courses.value = response.item;
  } catch (error) {
    showNotification("获取课程列表失败，请稍后重试。", "error");
    console.error(error);
  }
};

// 选择课程后填充开课列表
const populateSectionSelect = async () => {
  sections.value = [];
  students.value = [];
  grades.value = [];
  selectedSection.value = "";
  showGradeEntryCard.value = false;

  try {
    const response = await getMyCourseSections(selectedCourse.value.course_id, {
      semester: "",
      sec_year: null,
    });
    sections.value = response.data;
  } catch (error) {
    showNotification("获取开课列表失败，请稍后重试。", "error");
    console.error(error);
  }
};

// 加载学生名单
const loadStudents = async () => {
  loadingStudents.value = true;
  showNotification("正在加载学生名单...", "info");
  try {
    const response = await getSectionGrades(selectedSection.value.section_id, {
      student_id: "",
      student_name: "",
    });
    students.value = response.data.student_info;
    grades.value = response.data.grade_info;
    showGradeEntryCard.value = true;
    showNotification("学生名单加载完毕。", "success");
  } catch (error) {
    showNotification("加载学生名单失败，请稍后重试。", "error");
    console.error(error);
  } finally {
    loadingStudents.value = false;
  }
};

// 全部提交
const submitAllGrades = async () => {
  try {
    for (const studentGrade of studentGrades.value) {
      const data = {
        student_id: studentGrade.student_id,
        score: studentGrade.score,
        gpa: studentGrade.gpa,
      };
      await submitStudentGrades(selectedSection.value.section_id, data);
    }

    showNotification("所有成绩已成功提交！", "success");
    showGradeEntryCard.value = false;
    selectedSection.value = "";
    courses.value = [];
    sections.value = [];
    students.value = [];
    grades.value = [];
    selectedCourse.value = "";
  } catch (error) {
    showNotification("提交成绩失败，请稍后重试。", "error");
    console.error(error);
  }
};

// // 暂存草稿
// const saveDraft = async () => {
//   try {
//     const data = {
//       courseId: selectedCourse.value,
//       students: studentGrades.value,
//     };
//     await saveGradeDraft(data);
//     showNotification("成绩草稿已保存。", "info");
//   } catch (error) {
//     showNotification("保存草稿失败，请稍后重试。", "error");
//     console.error(error);
//   }
// };

// 显示通知
const showNotification = (message, type = "info") => {
  notification.value = {
    message,
    type,
  };
  setTimeout(() => {
    notification.value = {
      message: "",
      type: "info",
    };
  }, 5000);
};
</script>

<style scoped>
/* 这里可以直接复用原有的 CSS 样式 */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji",
    "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
  margin: 0;
  background-color: #f8f9fa;
  color: #333;
  line-height: 1.6;
  padding-top: 60px; /* For fixed top-bar */
  padding-bottom: 40px; /* For fixed bottom-bar */
  box-sizing: border-box;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

a {
  text-decoration: none;
  color: #409eff;
}
a:hover {
  text-decoration: underline;
  color: #1370eb;
}

/* Top Bar (与 login_style.css 中的 top-bar 样式一致) */
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
.top-bar .back-icon {
  /* 返回按钮样式 */
  color: white;
  font-size: 20px; /* 调整图标大小 */
  margin-right: 20px; /* 与系统名称的间距 */
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
  font-size: 22px; /* 调整图标大小 */
  width: 32px; /* 调整头像容器大小 */
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
  margin: 0 8px 0 0; /* 调整右边距 */
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
  top: calc(100% + 5px); /* 调整与用户信息的间距 */
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
  padding: 25px; /* 调整主内容区padding */
  width: 100%;
  box-sizing: border-box;
}
.container {
  max-width: 1100px; /* 调整容器最大宽度以适应表格 */
  margin: 0 auto;
}

/* Cards (与 login_style.css 中的 card 样式类似，可微调) */
.card {
  background-color: white;
  border-radius: 6px; /* 细微调整圆角 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 20px 25px;
  margin-bottom: 25px;
}
.card-title {
  font-size: 18px; /* 调整卡片标题大小 */
  color: #303133;
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5; /* 添加分隔线 */
}

/* Forms (与 login_style.css 中的 form 样式类似) */
.form-row {
  /* 用于横向排列表单组 */
  display: flex;
  gap: 20px; /* 表单组之间的间隙 */
  margin-bottom: 15px;
}
.form-row .form-group {
  flex: 1; /* 让表单组平分空间 */
  margin-bottom: 0; /* 移除单个表单组的底部外边距 */
}
.form-group {
  margin-bottom: 18px;
}
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}
.form-group select,
.form-group input[type="text"],
.form-group input[type="number"] {
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
.form-group input[type="text"]:focus,
.form-group input[type="number"]:focus {
  border-color: #409eff;
  outline: 0;
}
.form-group select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg class='icon' viewBox='0 0 1024 1024' xmlns='http://www.w3.org/2000/svg' width='12' height='12'%3E%3Cpath d='M512 714.666667c-8.533333 0-17.066667-2.133333-23.466667-8.533334l-307.2-268.8c-12.8-10.666667-14.933333-29.866667-4.266666-42.666666 10.666667-12.8 29.866667-14.933333 42.666666-4.266667l292.266667 256 292.266667-256c12.8-10.666667 32-8.533333 42.666667 4.266667s8.533333 32-4.266667 42.666666l-307.2 268.8c-6.4 4.266667-12.8 8.533333-21.333333 8.533334z' fill='%23C0C4CC'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
  padding-right: 30px;
}
.form-group select[disabled],
.form-group input[disabled] {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
  cursor: not-allowed;
}
.form-action-group {
  text-align: right; /* 使按钮靠右 */
  margin-top: 10px;
}

/* Buttons (与 login_style.css 中的 btn 样式类似) */
.btn {
  padding: 9px 18px; /* 调整按钮padding */
  border: 1px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: 0.1s;
  text-align: center;
  user-select: none;
  white-space: nowrap;
  display: inline-flex; /* 用于图标和文字对齐 */
  align-items: center;
  gap: 6px; /* 图标和文字的间距 */
}
.btn i {
  font-size: 1em; /* 使图标与文字大小一致 */
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
.btn-primary:active {
  background-color: #3a8ee6;
  border-color: #3a8ee6;
}
.btn-primary[disabled] {
  background-color: #a0cfff;
  border-color: #a0cfff;
  cursor: not-allowed;
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
.btn-success {
  /* 新增 Element Plus success 按钮样式 */
  background-color: #67c23a;
  border-color: #67c23a;
  color: white;
}
.btn-success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}
.action-buttons {
  margin-top: 25px;
  text-align: right;
}
.action-buttons .btn {
  margin-left: 10px;
}

/* Tables (Element Plus 风格表格) */
.table-responsive {
  overflow-x: auto;
  margin-top: 15px;
  border: 1px solid #ebeef5; /* 表格外边框 */
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
  border-bottom: 1px solid #ebeef5; /* 行分隔线 */
  padding: 12px 15px; /* 单元格内边距 */
  text-align: left;
  vertical-align: middle;
}
thead {
  background-color: #f5f7fa; /* 表头背景 */
  color: #909399; /* 表头文字颜色 */
}
th {
  font-weight: 500;
}
tbody tr:hover {
  background-color: #f5f7fa; /* 悬停行背景 */
}
tbody td input[type="number"],
tbody td input[type="text"] {
  width: calc(100% - 10px); /* 适应单元格 */
  padding: 6px 8px; /* 输入框内边距 */
  font-size: 13px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
}
tbody td input[type="number"]:focus,
tbody td input[type="text"]:focus {
  border-color: #409eff;
}
tbody td input[readonly] {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
  cursor: not-allowed;
}

/* Notification Area (与 login_style.css 中的一致) */
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

/* Bottom Bar (与 login_style.css 中的一致) */
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
    font-size: 13px;
    margin-left: 10px;
  }
  .top-bar .user-name {
    display: none;
  } /* 小屏幕隐藏用户名，只留头像 */
  .top-bar .user-avatar {
    margin-right: 0;
  }

  .page-main {
    padding: 15px;
  }
  .container {
    max-width: 100%;
  }
  .card-title {
    font-size: 16px;
  }
  .form-row {
    flex-direction: column;
    gap: 0;
  } /* 小屏幕下筛选条件垂直排列 */
  .form-row .form-group {
    margin-bottom: 15px;
  }
  .action-buttons .btn {
    font-size: 13px;
    padding: 8px 12px;
  }
  th,
  td {
    padding: 10px 8px;
    font-size: 13px;
  }
  tbody td input[type="number"],
  tbody td input[type="text"] {
    font-size: 12px;
    padding: 5px;
  }
}
</style>
