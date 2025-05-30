<template>
  <div>
    <!-- Top Bar -->
    <div class="top-bar">
      <div class="left-section">
        <router-link
          :to="getDashboardUrl()"
          class="back-icon"
          id="backToDashboard"
        >
          <FontAwesomeIcon icon="fas fa-arrow-left" />
        </router-link>
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 课程查询</span>
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
              <label for="courseTeacherId">教师id:</label>
              <input
                type="text"
                id="courseTeacherId"
                v-model="filters.teacher_id"
                placeholder="输入教师id"
              />
            </div>
            <div class="form-group">
              <label for="courseTeacher">授课教师:</label>
              <input
                type="text"
                id="courseTeacher"
                v-model="filters.teacher_name"
                placeholder="输入教师姓名"
              />
            </div>
            <div class="form-group">
              <label for="courseType">课程类别:</label>
              <select id="courseType" v-model="filters.category">
                <option value="">全部类别</option>
                <option value="普通">普通</option>
                <option value="实验">实验</option>
                <option value="体育">体育</option>
                <!--                <option value="specialized_elective">专业选修</option>-->
              </select>
            </div>
            <!--            <div class="form-group">-->
            <!--              <label for="courseDepartment">开课院系:</label>-->
            <!--              <input-->
            <!--                type="text"-->
            <!--                id="courseDepartment"-->
            <!--                v-model="filters.department"-->
            <!--                placeholder="输入院系名称"-->
            <!--              />-->
            <!--            </div>-->
            <div class="form-action-group">
              <button type="submit" class="btn btn-primary">
                <FontAwesomeIcon icon="fas fa-search" /> 查 询
              </button>
              <button type="reset" class="btn btn-secondary" @click="resetForm">
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
                  <!--                  <th>开课院系</th>-->
                </tr>
              </thead>
              <tbody id="courseListBody">
                <template v-if="isLoading">
                  <tr>
                    <td colspan="6" class="loading-placeholder">
                      正在加载数据... <i class="fas fa-spinner fa-spin"></i>
                    </td>
                  </tr>
                </template>
                <template v-else-if="courseNum === 0">
                  <tr>
                    <td colspan="6" class="no-data-placeholder">
                      未查询到符合条件的课程。
                    </td>
                  </tr>
                </template>
                <template v-else>
                  <tr
                    v-for="course in paginatedCourses"
                    :key="course.course_id"
                  >
                    <td>{{ course.course_name }}</td>
                    <td>{{ course.course_description }}</td>
                    <td>{{ course.teacher_name }}</td>
                    <td>{{ course.credit }}</td>
                    <td>{{ course.hours_per_week }}</td>
                    <td>{{ course.category }}</td>
                    <!--                    <td>-->
                    <!--                      {{-->
                    <!--                        course.semester-->
                    <!--                          .replace("-1", " 秋")-->
                    <!--                          .replace("-2", " 春")-->
                    <!--                      }}-->
                    <!--                    </td>-->
                    <!--                    <td>{{ course.department }}</td>-->
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
          <!-- 分页控件 -->
          <div
            class="pagination-controls"
            id="paginationControls"
            :style="{ display: courseNum > 0 ? 'flex' : 'none' }"
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
              {{ courseNum }} 条)</span
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
import { getCourses } from "@/api/course";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useuserLoginStore } from "@/store/userLoginStore";

const loginUserStore = useuserLoginStore();

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
  teacher_id: "",
  teacher_name: "",
  category: "",
});
const courses = ref<Course[]>([]);
const courseNum = ref(0);
const currentPage = ref(1);
const itemsPerPage = ref(10);
const isLoading = ref(false);
const isNotificationVisible = ref(false);
const notificationMessage = ref("");
const notificationType = ref("info");

// 计算总页数
const totalPages = ref(0);
const paginatedCourses = ref<Course[]>([]);

// 根据用户角色获取dashboard的URL
const getDashboardUrl = () => {
  const role = loginUserStore.loginUser.role;
  if (role === "s") {
    return "/student/dashboard";
  } else if (role === "t") {
    return "/teacher/dashboard";
  } else if (role === "a") {
    return "/admin/dashboard";
  } else {
    return "/login"; // 默认返回登录页
  }
};

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
  loginUserStore.setLoginUserUnlogin();
  window.location.href = "../change-password";
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
  // 确保courses.value存在且是数组
  if (courses.value && Array.isArray(courses.value)) {
    paginatedCourses.value = courses.value.slice(startIndex, endIndex);
  } else {
    paginatedCourses.value = [];
  }
  totalPages.value = Math.ceil(courseNum.value / itemsPerPage.value);
};

// const sampleCourses: Course[] = [
//   {
//     course_id: 1001,
//     course_name: "数据结构与算法",
//     course_description:
//       "介绍常用的数据结构和算法，培养学生的算法设计和分析能力。",
//     teacher_id: 2001,
//     teacher_name: "张教授",
//     credit: 4,
//     category: "专业必修",
//     hours_per_week: 4,
//   },
//   {
//     course_id: 1002,
//     course_name: "计算机组成原理",
//     course_description: "讲解计算机硬件系统的基本组成原理和工作机制。",
//     teacher_id: 2002,
//     teacher_name: "李教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1003,
//     course_name: "操作系统",
//     course_description:
//       "研究操作系统的设计原理和实现技术，包括进程管理、内存管理等。",
//     teacher_id: 2003,
//     teacher_name: "王教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1001,
//     course_name: "数据结构与算法",
//     course_description:
//       "介绍常用的数据结构和算法，培养学生的算法设计和分析能力。",
//     teacher_id: 2001,
//     teacher_name: "张教授",
//     credit: 4,
//     category: "专业必修",
//     hours_per_week: 4,
//   },
//   {
//     course_id: 1002,
//     course_name: "计算机组成原理",
//     course_description: "讲解计算机硬件系统的基本组成原理和工作机制。",
//     teacher_id: 2002,
//     teacher_name: "李教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1003,
//     course_name: "操作系统",
//     course_description:
//       "研究操作系统的设计原理和实现技术，包括进程管理、内存管理等。",
//     teacher_id: 2003,
//     teacher_name: "王教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1001,
//     course_name: "数据结构与算法",
//     course_description:
//       "介绍常用的数据结构和算法，培养学生的算法设计和分析能力。",
//     teacher_id: 2001,
//     teacher_name: "张教授",
//     credit: 4,
//     category: "专业必修",
//     hours_per_week: 4,
//   },
//   {
//     course_id: 1002,
//     course_name: "计算机组成原理",
//     course_description: "讲解计算机硬件系统的基本组成原理和工作机制。",
//     teacher_id: 2002,
//     teacher_name: "李教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1003,
//     course_name: "操作系统",
//     course_description:
//       "研究操作系统的设计原理和实现技术，包括进程管理、内存管理等。",
//     teacher_id: 2003,
//     teacher_name: "王教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1001,
//     course_name: "数据结构与算法",
//     course_description:
//       "介绍常用的数据结构和算法，培养学生的算法设计和分析能力。",
//     teacher_id: 2001,
//     teacher_name: "张教授",
//     credit: 4,
//     category: "专业必修",
//     hours_per_week: 4,
//   },
//   {
//     course_id: 1002,
//     course_name: "计算机组成原理",
//     course_description:
//       "讲解计算机硬件系统的基本组成原理和工作机制讲解计算机硬件系统的基本组成原理和工作机制。",
//     teacher_id: 2002,
//     teacher_name: "李教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1001,
//     course_name: "数据结构与算法",
//     course_description:
//       "介绍常用的数据结构和算法，培养学生的算法设计和分析能力。",
//     teacher_id: 2001,
//     teacher_name: "张教授",
//     credit: 4,
//     category: "专业必修",
//     hours_per_week: 4,
//   },
//   {
//     course_id: 1002,
//     course_name: "计算机组成原理",
//     course_description: "讲解计算机硬件系统的基本组成原理和工作机制。",
//     teacher_id: 2002,
//     teacher_name: "李教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1003,
//     course_name: "操作系统",
//     course_description:
//       "研究操作系统的设计原理和实现技术，包括进程管理、内存管理等。",
//     teacher_id: 2003,
//     teacher_name: "王教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1003,
//     course_name: "操作系统",
//     course_description:
//       "研究操作系统的设计原理和实现技术，包括进程管理、内存管理等。",
//     teacher_id: 2003,
//     teacher_name: "王教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1004,
//     course_name: "人工智能导论",
//     course_description:
//       "介绍人工智能的基本概念、方法和应用，包括机器学习、自然语言处理等。",
//     teacher_id: 2004,
//     teacher_name: "赵教授",
//     credit: 3,
//     category: "专业选修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1005,
//     course_name: "计算机网络",
//     course_description: "讲解计算机网络的体系结构、协议和实现技术。",
//     teacher_id: 2005,
//     teacher_name: "孙教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1006,
//     course_name: "数据库系统",
//     course_description: "介绍数据库系统的基本原理、设计方法和应用技术。",
//     teacher_id: 2006,
//     teacher_name: "周教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1007,
//     course_name: "软件工程",
//     course_description:
//       "研究软件开发的工程化方法和技术，培养学生的软件项目管理能力。",
//     teacher_id: 2007,
//     teacher_name: "吴教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1008,
//     course_name: "编译原理",
//     course_description:
//       "讲解编译程序的构造原理和实现技术，包括词法分析、语法分析等。",
//     teacher_id: 2008,
//     teacher_name: "郑教授",
//     credit: 3,
//     category: "专业必修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1009,
//     course_name: "信息安全技术",
//     course_description:
//       "介绍信息安全的基本概念、技术和方法，包括密码学、网络安全等。",
//     teacher_id: 2009,
//     teacher_name: "钱教授",
//     credit: 3,
//     category: "专业选修",
//     hours_per_week: 3,
//   },
//   {
//     course_id: 1010,
//     course_name: "大数据分析技术",
//     course_description:
//       "介绍大数据处理和分析的基本技术和方法，包括Hadoop、Spark等。",
//     teacher_id: 2010,
//     teacher_name: "冯教授",
//     credit: 3,
//     category: "专业选修",
//     hours_per_week: 3,
//   },
// ];
// 查询课程
const queryCourses = async () => {
  // 长度检查
  const maxCourseNameLength = 20;
  const maxTeacherIdLength = 10;
  const maxTeacherNameLength = 20;

  if (filters.value.course_name.length > maxCourseNameLength) {
    showNotification(
      `课程名称长度不能超过 ${maxCourseNameLength} 个字符。`,
      "error"
    );
    return;
  }

  if (filters.value.teacher_id.length > maxTeacherIdLength) {
    showNotification(
      `教师 ID 长度不能超过 ${maxTeacherIdLength} 个字符。`,
      "error"
    );
    return;
  }

  if (filters.value.teacher_name.length > maxTeacherNameLength) {
    showNotification(
      `授课教师姓名长度不能超过 ${maxTeacherNameLength} 个字符。`,
      "error"
    );
    return;
  }

  isLoading.value = true;
  showNotification("正在查询课程...", "info");
  try {
    const response = await getCourses(filters.value);
    courseNum.value = response.data.data.totalItems;
    courses.value = response.data.data.items;
    currentPage.value = 1;
    calculatePaginatedCourses();
    if (response.data.data.totalItems > 0) {
      showNotification(
        `查询到 ${response.data.data.totalItems} 条符合条件的课程。`,
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
const resetForm = () => {
  filters.value = {
    name: "",
    teacher_id: "",
    teacher: "",
    category: "",
  };
  courses.value = [];
  currentPage.value = 1;
  showNotification("筛选条件已重置。", "info");
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
  grid-column: 1 / -1; /* 让按钮组横跨所有列，或根据需要调整 */
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
