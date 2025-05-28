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
        <span class="system-subname">信息管理子系统 - 修改课程信息</span>
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
      <div class="container edit-course-container">
        <div class="card course-selection-card">
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-tasks" /> 选择要修改的课程
          </h2>
          <form id="selectCourseToEditForm" class="filter-form-grid">
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
            <div class="form-action-group">
              <button
                type="button"
                class="btn btn-primary"
                :disabled="!selectedCourse"
                @click="loadCourseDetails"
              >
                <FontAwesomeIcon icon="fas fa-cogs" /> 加载课程详情
              </button>
            </div>
          </form>
        </div>

        <div
          class="card course-details-card"
          id="courseDetailsCard"
          :style="{ display: showCourseDetails ? 'block' : 'none' }"
        >
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-book-open" /> 课程《<span
              id="editingCourseNameTitle"
              >{{ currentCourse.course_name }}</span
            >》信息编辑
          </h2>
          <form id="editCourseDetailsForm" @submit.prevent="saveCourseDetails">
            <div class="form-group">
              <label for="courseCodeDisplay">课程名称:</label>
              <input
                type="text"
                id="courseCodeDisplay"
                :value="currentCourse.course_name"
              />
            </div>
            <div class="form-group">
              <label for="courseCreditsDisplay">学分:</label>
              <input
                type="text"
                id="courseCreditsDisplay"
                :value="currentCourse.credit"
              />
            </div>
            <div class="form-group">
              <label for="courseIntroduction">课程类别:</label>
              <select
                id="courseCategory"
                name="Category"
                v-model="currentCourse.category"
                readonly
              >
                <option>{{ currentCourse.category }}</option>
                <option>体育</option>
                <option>普通</option>
                <option>实验</option>
              </select>
            </div>

            <hr class="section-divider" />

            <div class="form-group">
              <label for="courseIntroduction">课程简介:</label>
              <textarea
                id="courseIntroduction"
                name="introduction"
                rows="5"
                v-model="currentCourse.course_description"
                placeholder="请输入课程简介..."
              ></textarea>
            </div>

            <div class="form-actions">
              <button
                type="submit"
                class="btn btn-primary"
                @click="saveCourseDetails"
              >
                <FontAwesomeIcon icon="fas fa-save" /> 保存并提交修改
              </button>
              <button
                type="button"
                class="btn btn-default"
                @click="resetCourseEdit"
              >
                <FontAwesomeIcon icon="fas fa-circle" /> 重置
              </button>
              <button
                type="button"
                class="btn btn-default"
                @click="cancelCourseEdit"
              >
                <FontAwesomeIcon icon="fas fa-times" /> 取消
              </button>
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
            <div class="form-action-group">
              <button
                type="button"
                class="btn btn-primary"
                :disabled="!selectedSection"
                @click="loadSectionDetails"
              >
                <FontAwesomeIcon icon="fas fa-cogs" /> 加载开课详情
              </button>
            </div>
          </form>
        </div>

        <div
          class="card course-details-card"
          id="courseDetailsCard"
          :style="{ display: showSectionDetails ? 'block' : 'none' }"
        >
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-book-open" /> 课程《<span
              id="editingCourseNameTitle"
              >{{ currentCourse.course_name }}</span
            >》开课信息编辑
          </h2>
          <form id="editCourseDetailsForm" @submit.prevent="saveSectionDetails">
            <div class="form-group">
              <label for="courseCodeDisplay">所在学年:</label>
              <input
                type="text"
                id="courseYearDisplay"
                :value="currentSection.sec_year"
              />
            </div>
            <div class="form-group">
              <label for="courseCreditsDisplay">所在学期</label>
              <select
                id="courseSemesterDisplay"
                name="Category"
                v-model="currentSection.semester"
              >
                <option>{{ currentSection.semester }}</option>
                <option>春夏</option>
                <option>秋冬</option>
              </select>
            </div>
            <div class="form-group">
              <label for="courseCreditsDisplay">上课时间:</label>
              <input
                type="text"
                id="courseTimeDisplay"
                :value="currentSection.sec_time"
              />
            </div>
            <div class="form-group">
              <label for="courseCreditsDisplay">课程容量:</label>
              <input
                type="text"
                id="courseCapacityDisplay"
                :value="currentSection.capacity"
              />
            </div>
            <div class="form-group">
              <label for="courseCreditsDisplay">教室地址:</label>
              <input
                type="text"
                id="courseClassroomDisplay"
                readonly
                :value="currentSection.classroom_location"
              />
            </div>
            <div class="form-group">
              <label for="courseCreditsDisplay">剩余容量:</label>
              <input
                type="text"
                id="courseAvailableCapacityDisplay"
                readonly
                :value="currentSection.available_capacity"
              />
            </div>

            <div class="form-actions">
              <button
                type="submit"
                class="btn btn-primary"
                @click="saveSectionDetails"
              >
                <FontAwesomeIcon icon="fas fa-save" /> 保存并提交修改
              </button>
              <button
                type="button"
                class="btn btn-default"
                @click="resetSectionEdit"
              >
                <FontAwesomeIcon icon="fas fa-circle" /> 重置
              </button>
              <button
                type="button"
                class="btn btn-default"
                @click="cancelSectionEdit"
              >
                <FontAwesomeIcon icon="fas fa-times" /> 取消
              </button>
            </div>
          </form>
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
import { ref, onMounted } from "vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useuserLoginStore } from "@/store/userLoginStore";
import {
  getMyCourses,
  getMyCourseSections,
  updateCourse,
  updateSection,
} from "@/api/teacher";
import { getSectionDetails } from "@/api/section";
import { getCourseDetails } from "@/api/course";

const loginUserStore = useuserLoginStore();
const userDropdownVisible = ref(false);
const selectedCourse = ref("");
const selectedSection = ref("");
const courses = ref<any[]>([]);
const sections = ref<any[]>([]);
const currentCourse = ref({});
const showCourseDetails = ref(false);
const currentSection = ref({});
const showSectionDetails = ref(false);
const notification = ref({
  message: "",
  type: "info",
});

const toggleUserDropdown = () => {
  userDropdownVisible.value = !userDropdownVisible.value;
};

// 处理退出登录
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

// const MockCourses = {
//   item: [
//     {
//       course_id: "121",
//       course_name: "课1",
//       course_description: "adwd",
//       category: "adawd",
//       credit: 12,
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
// const MockCoursesDetails = {
//   data: {
//     course_id: "121",
//     course_name: "课1",
//     course_description: "adwd",
//     category: "adawd",
//     credit: 12,
//   },
// };
//
// const getCourseDetails = async (params: any) => {
//   return MockCoursesDetails;
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
// const MockSectionsDetails = {
//   data: {
//     section_id: "212",
//     sec_year: 1999,
//     semester: "awdawd",
//     sec_time: "awdawd",
//     classroom_location: "wdawd",
//     capacity: 21,
//     available_capacity: 12,
//   },
// };
//
// const getSectionDetails = async (params: any) => {
//   return MockSectionsDetails;
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

const populateCourseSelect = async () => {
  courses.value = [];
  sections.value = [];
  selectedCourse.value = "";
  selectedSection.value = "";
  showCourseDetails.value = false;

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

const populateSectionSelect = async () => {
  sections.value = [];
  selectedSection.value = "";
  showCourseDetails.value = false;

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

const loadCourseDetails = async () => {
  try {
    const response = await getCourseDetails(selectedCourse.value.course_id);
    currentCourse.value = response.data;
    showCourseDetails.value = true;
    showNotification(
      `已加载课程《${currentCourse.value.course_name}》的详细信息。`,
      "info"
    );
  } catch (error) {
    showNotification("无法加载课程详情。", "error");
    showCourseDetails.value = false;
    console.error(error);
  }
};

const saveCourseDetails = async () => {
  if (
    !currentCourse.value.course_name ||
    !currentCourse.value.course_description ||
    !currentCourse.value.credit
  ) {
    showNotification("课程名称,简介,学分不能为空", "error");
    return;
  }

  try {
    showNotification("正在保存开课信息...", "info");
    const response = await updateCourse(currentCourse.value.course_id, {
      course_name: currentCourse.value.course_name,
      course_description: currentCourse.value.course_description,
      credit: currentCourse.value.credit,
      category: currentCourse.value.category,
      hours_per_week: currentCourse.value.hours_per_week,
    });
    showNotification(
      `课程《${currentCourse.value.course_name}》的开课信息已成功更新！`,
      "success"
    );
  } catch (error) {
    showNotification("保存开课信息失败，请稍后重试。", "error");
    console.error(error);
  }
};

const resetCourseEdit = () => {
  loadCourseDetails();
  showNotification("修改已取消。", "info");
};

const cancelCourseEdit = () => {
  showCourseDetails.value = false;
};

const loadSectionDetails = async () => {
  try {
    const response = await getSectionDetails(selectedSection.value.section_id);
    currentSection.value = response.data;
    showSectionDetails.value = true;
    showNotification(
      `已加载课程《${currentCourse.value.course_name}》的开课信息。`,
      "info"
    );
  } catch (error) {
    showNotification("无法加载开课详情。", "error");
    showSectionDetails.value = false;
    console.error(error);
  }
};

const saveSectionDetails = async () => {
  if (
    !currentSection.value.sec_year ||
    !currentSection.value.semester ||
    !currentSection.value.sec_time ||
    !currentSection.value.capacity
  ) {
    showNotification("开课学年,开课学期,上课时间,课程容量不能为空！", "error");
    return;
  }

  try {
    showNotification("正在保存开课信息...", "info");
    const response = await updateSection(selectedSection.value.section_id, {
      classroom_id: selectedSection.value.classroom_id,
      capacity: selectedSection.value.capacity,
      semester: selectedSection.value.semester,
      sec_year: selectedSection.value.sec_year,
      sec_time: selectedSection.value.sec_time,
    });
    showNotification(
      `课程《${currentSection.value.name}》的开课信息已成功更新！`,
      "success"
    );
  } catch (error) {
    showNotification("保存开课信息失败，请稍后重试。", "error");
    console.error(error);
  }
};

const resetSectionEdit = () => {
  loadSectionDetails();
  showNotification("修改已取消。", "info");
};

const cancelSectionEdit = () => {
  showSectionDetails.value = false;
};

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

onMounted(() => {
  populateCourseSelect();
});
</script>

<style scoped>
/* 这里可以复用原有的 CSS 样式 */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  margin: 0;
  background-color: #f4f6f8;
  color: #333;
  line-height: 1.6;
  padding-top: 60px;
  padding-bottom: 50px; /* Corrected bottom padding */
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

/* Top Bar - ENSURING CONSISTENCY */
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
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: background-color 0.2s;
  position: relative;
}

.top-bar .user-info:hover {
  background-color: #3a8ee6;
} /* Consistent hover */

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
  font-size: 18px;
  color: #409eff;
  line-height: 1;
} /* Corrected icon style */

.top-bar .user-name {
  margin: 0 8px 0 0;
  font-size: 14px;
  color: white;
  line-height: 1;
} /* Corrected name style */

.top-bar .user-info .fa-angle-down {
  font-size: 12px;
  color: white;
  margin-left: 5px;
  transition: transform 0.2s;
} /* Corrected arrow */

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

.container.edit-course-container {
  max-width: 900px;
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

#editingCourseNameTitle {
  color: #e6a23c;
  font-weight: bold;
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
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

@media (min-width: 800px) {
  .filter-form-grid .form-action-group {
    grid-column: auto;
    align-self: end;
  }
}

/* Forms */
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
.form-group input[type="file"],
.form-group textarea {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  background-color: #fff;
  line-height: 1.5;
}

.form-group textarea {
  resize: vertical;
}

.form-group input[readonly] {
  background-color: #f5f7fa;
  color: #909399;
  cursor: default;
  border-color: #e4e7ed;
}

.form-group select:focus,
.form-group input[type="text"]:focus,
.form-group input[type="file"]:focus,
.form-group textarea:focus {
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
.form-group input[disabled],
.form-group button[disabled] {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
  cursor: not-allowed;
}

.section-divider {
  grid-column: 1 / -1;
  border: none;
  border-top: 1px dashed #ebeef5;
  margin: 15px 0;
}

.form-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
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

.btn-default {
  background-color: #fff;
  border-color: #dcdfe6;
  color: #606266;
}

.btn-default:hover {
  border-color: #409eff;
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

/* Bottom Bar - ENSURING CONSISTENCY */
.bottom-bar {
  background-color: #f5f5f5b7;
  padding: 12px 0;
  text-align: center; /* Ensures the .copyright-text is centered */
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
    display: none;
  }

  .page-main {
    padding: 15px;
  }

  .filter-form-grid {
    grid-template-columns: 1fr;
  }

  .filter-form-grid .form-action-group {
    justify-content: center;
  }

  .form-actions {
    justify-content: center;
  }
}
</style>
