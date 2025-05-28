<template>
  <div class="home">
    <div class="top-bar">
      <div class="left-section">
        <router-link
          to="../admin/dashboard"
          class="back-icon"
          id="backToAdminDashboard"
        >
          <FontAwesomeIcon icon="fas fa-arrow-left" />
        </router-link>
        <span class="system-name">教学服务系统</span>
        <span class="system-subname">信息管理子系统 - 成绩修改审核</span>
      </div>
      <div class="right-section">
        <div class="user-info" id="userInfoToggle" @click="toggleUserDropdown">
          <div class="user-avatar">
            <FontAwesomeIcon icon="fas fa-user-shield" />
          </div>
          <span class="user-name" id="approveGradeAdminName">{{
            JSON.stringify(loginUserStore.loginUser.name)
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
      <div class="container approve-grade-container">
        <div class="card filter-card-approval">
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-filter" /> 筛选待审核申请
          </h2>
          <form
            id="filterApprovalForm"
            class="filter-form-grid"
            @submit.prevent="filterAndDisplayApplications"
          >
            <div class="form-group">
              <label for="filterTeacherNameApproval">申请教师:</label>
              <input
                type="text"
                id="filterTeacherNameApproval"
                v-model="filter.teacher_id"
                placeholder="输入教师姓名或工号"
              />
            </div>
            <!-- <div class="form-group">
              <label for="filterCourseNameApproval">涉及课程:</label>
              <input
                type="text"
                id="filterCourseNameApproval"
                v-model="filter.courseName"
                placeholder="输入课程名称或编号"
              />
            </div> -->
            <div class="form-group">
              <label for="filterApplicationStatus">申请状态:</label>
              <select
                id="filterApplicationStatus"
                v-model="filter.audit_status"
              >
                <option value="0" selected>待审核</option>
                <option value="1">已批准</option>
                <option value="2">已驳回</option>
                <option value="">所有状态</option>
              </select>
            </div>
            <div class="form-action-group">
              <button type="submit" class="btn btn-primary">
                <FontAwesomeIcon icon="fas fa-search" /> 查询申请
              </button>
              <button
                type="reset"
                class="btn btn-secondary"
                id="resetApprovalFiltersBtn"
                @click="resetFilters"
              >
                <FontAwesomeIcon icon="fas fa-undo" /> 重置
              </button>
            </div>
          </form>
        </div>

        <div class="card approval-list-card">
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-tasks" /> 成绩修改申请列表
            <!--            (<span-->
            <!--              id="currentApprovalFilterDisplay"-->
            <!--              >{{ filterText }}</span-->
            <!--            >)-->
          </h2>
          <div class="table-responsive">
            <table id="approvalTable">
              <thead>
                <tr>
                  <th>申请ID</th>
                  <th>申请教师</th>
                  <th>课程名称</th>
                  <th>学生姓名</th>
                  <th>原成绩</th>
                  <th>申请修改为</th>
                  <th>申请时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody id="approvalListBody">
                <template v-if="loading">
                  <tr>
                    <td colspan="9" class="placeholder-row">
                      正在加载待审核列表...
                    </td>
                  </tr>
                </template>
                <template v-else-if="filteredApplications.length === 0">
                  <tr>
                    <td colspan="9" class="no-applications-row">
                      没有符合条件的成绩修改申请。
                    </td>
                  </tr>
                </template>
                <template v-else>
                  <tr v-for="app in filteredApplications" :key="app.id">
                    <td class="application-id">{{ app.id }}</td>
                    <td>{{ app.teacherName }}</td>
                    <td>{{ app.courseName }}</td>
                    <td>{{ app.studentName }}</td>
                    <td>{{ app.originalGrade }}</td>
                    <td>{{ app.newGrade }}</td>
                    <td>{{ app.applicationTime }}</td>
                    <td class="application-audit_status">
                      <span :class="'audit_status-' + app.audit_status">{{
                        statusText(app.audit_status)
                      }}</span>
                    </td>
                    <td class="actions-cell">
                      <button
                        class="btn btn-sm btn-primary view-details-btn"
                        @click="openModal(app)"
                        title="查看/审核详情"
                      >
                        <i class="fas fa-eye"></i> 详情
                      </button>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
          <!-- Pagination (Optional) -->
          <div
            class="pagination-controls"
            id="approvalPaginationControls"
            :style="{
              display: filteredApplications.length > 0 ? 'block' : 'none',
            }"
          >
            <button
              class="btn btn-sm btn-default"
              id="prevApprovalPageBtn"
              :disabled="currentPage === 1"
              @click="prevPage"
            >
              « 上一页
            </button>
            <span id="approvalPageInfo"
              >第 {{ currentPage }} / {{ totalPages }} 页</span
            >
            <button
              class="btn btn-sm btn-default"
              id="nextApprovalPageBtn"
              :disabled="currentPage === totalPages"
              @click="nextPage"
            >
              下一页 »
            </button>
          </div>
        </div>

        <!-- Modal for Viewing Application Details & Approving/Rejecting -->
        <div
          class="modal"
          id="approvalModal"
          :style="{ display: isModalOpen ? 'flex' : 'none' }"
        >
          <div class="modal-content card">
            <div class="modal-header">
              <h3 class="modal-title" id="approvalModalTitle">
                审核成绩修改申请 - {{ currentEditingApplication?.id }}
              </h3>
              <button
                type="button"
                class="close-modal-btn"
                id="closeApprovalModalBtn"
                @click="closeModal"
              >
                ×
              </button>
            </div>
            <div class="modal-body">
              <div class="application-details">
                <p>
                  <strong>申请ID:</strong>
                  <span id="modalAppId">{{
                    currentEditingApplication?.id
                  }}</span>
                </p>
                <p>
                  <strong>申请教师:</strong>
                  <span id="modalTeacherName">{{
                    currentEditingApplication?.teacherName
                  }}</span>
                </p>
                <p>
                  <strong>课程:</strong>
                  <span id="modalCourseInfo">{{
                    currentEditingApplication?.courseName
                  }}</span>
                </p>
                <p>
                  <strong>学生:</strong>
                  <span id="modalStudentInfo"
                    >{{ currentEditingApplication?.studentName }}
                  </span>
                </p>
                <p>
                  <strong>原成绩:</strong>
                  <span
                    id="modalOriginalGrade"
                    class="grade-highlight original"
                    >{{ currentEditingApplication?.originalGrade }}</span
                  >
                </p>
                <p>
                  <strong>申请修改为:</strong>
                  <span id="modalNewGrade" class="grade-highlight new">{{
                    currentEditingApplication?.newGrade
                  }}</span>
                </p>
                <p>
                  <strong>申请时间:</strong>
                  <span id="modalApplicationTime">{{
                    currentEditingApplication?.applicationTime
                  }}</span>
                </p>
                <p><strong>修改原因:</strong></p>
                <div class="reason-box" id="modalReason">
                  {{ currentEditingApplication?.reason }}
                </div>
              </div>
              <hr />
              <div class="form-group" id="adminRemarksGroup">
                <label for="adminRemarks">审核备注 (驳回时必填):</label>
                <textarea
                  id="adminRemarks"
                  v-model="adminRemarks"
                  rows="3"
                  placeholder="请输入审核意见或驳回原因..."
                  :readonly="currentEditingApplication?.audit_status !== 0"
                ></textarea>
              </div>
            </div>
            <div class="modal-actions">
              <button
                type="button"
                class="btn btn-success"
                id="approveBtn"
                :style="{
                  display:
                    currentEditingApplication?.audit_status === 0
                      ? 'inline-flex'
                      : 'none',
                }"
                @click="approveApplication"
              >
                <FontAwesomeIcon icon="fas fa-check-circle" /> 批准申请
              </button>
              <button
                type="button"
                class="btn btn-danger"
                id="rejectBtn"
                :style="{
                  display:
                    currentEditingApplication?.audit_status === 0
                      ? 'inline-flex'
                      : 'none',
                }"
                @click="rejectApplication"
              >
                <FontAwesomeIcon icon="fas fa-times-circle" /> 驳回申请
              </button>
              <button
                type="button"
                class="btn btn-default"
                id="cancelApprovalModalBtn"
                @click="closeModal"
              >
                关闭
              </button>
            </div>
          </div>
        </div>

        <div
          id="notificationArea"
          class="notification"
          :style="{ display: notification.message ? 'block' : 'none' }"
          :class="`notification-${notification.type}`"
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
import { getAdminGradeApplies, reviewGradeApply } from "@/api/admin";
import { useuserLoginStore } from "@/store/userLoginStore";
import { useRouter } from "vue-router";

// 定义申请记录的类型接口
interface Application {
  id?: number;
  apply_id: number;
  teacherName?: string;
  courseName?: string;
  studentName?: string;
  studentId?: string;
  originalGrade?: number;
  newGrade?: number;
  applicationTime?: string;
  audit_status: number | string;
  reason?: string;
  adminRemarks?: string;
}

// 定义后端返回的数据结构
interface ApplyItem {
  apply: {
    applyId: number;
    teacherId: number;
    adminId: number | null;
    gradeId: number;
    oldScore: number;
    newScore: number;
    reason: string;
    auditStatus: number;
    applyTime: number;
    reviewTime: number | null;
  };
  course_name: string;
  teacher_name: string;
  student_name: string;
}

const router = useRouter();
const loginUserStore = useuserLoginStore();
// 响应式数据
const userDropdownVisible = ref(false);
const filter = ref({
  teacher_id: "",
  audit_status: 0,
});
// const filterText = ref("待审核");
const loading = ref(true);
const filteredApplications = ref<Application[]>([]);
const isModalOpen = ref(false);
const currentEditingApplication = ref<Application | null>(null);
const adminRemarks = ref("");
const notification = ref({
  message: "",
  type: "info",
});
const currentPage = ref(1);
const itemsPerPage = ref(10);

// 计算总页数
const totalPages = ref(0);

// 生命周期钩子
onMounted(() => {
  filterAndDisplayApplications();
});

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  userDropdownVisible.value = !userDropdownVisible.value;
};

// 处理退出登录
const handleLogout = () => {
  showNotification("正在退出登录...", "info");
  setTimeout(() => {
    loginUserStore.setLoginUserUnlogin();
    router.push("/login");
  }, 1500);
};

// 处理修改密码
const handleChangePassword = () => {
  //window.location.href = "../change-password";
  router.push("/change-password");
};

const filterAndDisplayApplications = async () => {
  loading.value = true;
  showNotification("正在查询申请列表...", "info");
  try {
    const response = await getAdminGradeApplies(filter.value);
    // 确保response.data存在且是一个数组
    filteredApplications.value = Array.isArray(response.data.data)
      ? response.data.data.map((item: ApplyItem) => ({
          id: item.apply.applyId,
          apply_id: item.apply.applyId,
          teacherName: item.teacher_name,
          courseName: item.course_name,
          studentName: item.student_name,
          studentId: item.apply.gradeId.toString(), // 可能需要替换为实际的学生ID
          originalGrade: item.apply.oldScore,
          newGrade: item.apply.newScore,
          applicationTime: new Date(item.apply.applyTime).toLocaleString(),
          audit_status: item.apply.auditStatus,
          reason: item.apply.reason,
        }))
      : [];

    totalPages.value = Math.ceil(
      filteredApplications.value.length / itemsPerPage.value
    );
    loading.value = false;
    if (filteredApplications.value.length > 0) {
      showNotification("申请列表加载完毕。", "success");
    } else {
      showNotification("没有找到符合条件的申请。", "info");
    }
  } catch (error) {
    loading.value = false;
    filteredApplications.value = []; // 确保在出错时设置为空数组
    totalPages.value = 0; // 重置页数
    showNotification("查询申请列表失败，请稍后重试。", "error");
    console.error(error);
  }
};

const resetFilters = () => {
  filter.value = {
    teacher_id: "",
    audit_status: 0,
  };
  setTimeout(filterAndDisplayApplications, 100);
};

const openModal = (application: Application) => {
  currentEditingApplication.value = application;
  adminRemarks.value = application.adminRemarks || "";
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  currentEditingApplication.value = null;
  adminRemarks.value = "";
};

const approveApplication = async () => {
  if (!currentEditingApplication.value) return;
  const remarks = adminRemarks.value.trim();
  try {
    const data = { audit_status: 1, review_comment: remarks };
    // 确保apply_id是字符串
    const applyId = String(currentEditingApplication.value.apply_id);
    await reviewGradeApply(applyId, data);
    showNotification(`申请已批准。`, "success");
    closeModal();
    await filterAndDisplayApplications();
  } catch (error) {
    showNotification("批准申请失败，请稍后重试。", "error");
    console.error(error);
  }
};

const rejectApplication = async () => {
  if (!currentEditingApplication.value) return;
  const remarks = adminRemarks.value.trim();
  if (!remarks) {
    showNotification("驳回申请必须填写审核备注！", "error");
    return;
  }
  try {
    const data = { audit_status: 2, review_comment: remarks };
    // 确保apply_id是字符串
    const applyId = String(currentEditingApplication.value.apply_id);
    await reviewGradeApply(applyId, data);
    showNotification(`申请已驳回。`, "info");
    closeModal();
    await filterAndDisplayApplications();
  } catch (error) {
    showNotification("驳回申请失败，请稍后重试。", "error");
    console.error(error);
  }
};

const showNotification = (message: string, type: "info") => {
  notification.value = {
    message,
    type,
  };
  setTimeout(() => {
    notification.value = {
      message: "",
      type: "info",
    };
  }, 3000);
};

const statusText = (audit_status: number | string): string => {
  if (audit_status === 0 || audit_status === "0") return "待审核";
  if (audit_status === 1 || audit_status === "1") return "已批准";
  return "已驳回";
};

// const downloadEvidence = (file) => {
//   alert(`模拟下载证明文件: ${file}`);
// };

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};
</script>

<style>
/* admin_approve_grade_change_style.css */

/* ==========================================================================
   CRITICAL: Global, Top Bar, and Bottom Bar Styles - FROM router-link KNOWN GOOD VERSION
   ========================================================================== */
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  margin: 0;
  background-color: #f4f6f8;
  color: #333;
  line-height: 1.6;
  padding-top: 60px;
  padding-bottom: 50px;
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
  font-size: 18px;
  color: #409eff;
  line-height: 1;
}
.top-bar .user-name {
  margin: 0 8px 0 0;
  font-size: 14px;
  color: white;
  line-height: 1;
}
.top-bar .user-info .fa-angle-down {
  font-size: 12px;
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

/* General Button Styles */
.btn {
  padding: 8px 15px;
  border: 1px solid #dcdfe6;
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
  justify-content: center;
  gap: 6px;
  background-color: #fff;
  color: #606266;
}
.btn i {
  font-size: 1em;
  line-height: 1;
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
.btn-success {
  background-color: #67c23a;
  border-color: #67c23a;
  color: white;
}
.btn-success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}
.btn-danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
  color: white;
}
.btn-danger:hover {
  background-color: #f78989;
  border-color: #f78989;
}
.btn-default:hover {
  border-color: #409eff;
  color: #409eff;
  background-color: #ecf5ff;
}
.btn-sm {
  padding: 5px 10px;
  font-size: 12px;
}

/* Notification Area (General) */
.notification {
  /* ... (Same as previous) ... */
}
.notification.success {
  /* ... */
}
.notification.error {
  /* ... */
}
.notification.info {
  /* ... */
}

/* ==========================================================================
   Page Specific Styles for Grade Change Approval
   ========================================================================== */

.page-main {
  flex-grow: 1;
  padding: 25px;
  width: 100%;
  box-sizing: border-box;
}
.container.approve-grade-container {
  max-width: 1200px;
  margin: 0 auto;
}

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
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
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
@media (min-width: 992px) {
  .filter-form-grid .form-action-group {
    grid-column: auto;
    align-self: end;
    margin-top: 0;
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
.form-group input[type="text"],
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
}
.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #409eff;
  outline: 0;
}
.form-group textarea {
  resize: vertical;
  line-height: 1.5;
}
.form-group select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg class='icon' viewBox='0 0 1024 1024' xmlns='http://www.w3.org/2000/svg' width='12' height='12'%3E%3Cpath d='M512 714.666667c-8.533333 0-17.066667-2.133333-23.466667-8.533334l-307.2-268.8c-12.8-10.666667-14.933333-29.866667-4.266666-42.666666 10.666667-12.8 29.866667-14.933333 42.666666-4.266667l292.266667 256 292.266667-256c12.8-10.666667 32-8.533333 42.666667 4.266667s8.533333 32-4.266667 42.666666l-307.2 268.8c-6.4 4.266667-12.8 8.533333-21.333333 8.533334z' fill='%23C0C4CC'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
  padding-right: 30px;
}

/* Approval Table */
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
  padding: 10px 12px;
  text-align: left;
  vertical-align: middle;
  white-space: normal;
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
.no-applications-row td {
  text-align: center;
  color: #909399;
  padding: 30px 15px;
  font-style: italic;
}
td.application-id {
  font-weight: bold;
  color: #409eff;
}
td.application-audit_status span {
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.85em;
  font-weight: 500;
  display: inline-block;
}
td.application-audit_status .audit_status-pending {
  background-color: #fdf6ec;
  color: #e6a23c;
}
td.application-audit_status .audit_status-approved {
  background-color: #f0f9eb;
  color: #67c23a;
}
td.application-audit_status .audit_status-rejected {
  background-color: #fef0f0;
  color: #f56c6c;
}
td.actions-cell .btn {
  margin-right: 5px;
} /* Re-apply .btn for table action buttons */
td.actions-cell .btn:last-child {
  margin-right: 0;
}

/* Pagination Controls */
.pagination-controls {
  /* ... */
}
.pagination-controls #approvalPageInfo {
  /* ... */
}

/* Modal Styles */
.modal {
  display: none;
  position: fixed;
  z-index: 1050;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.5);
  justify-content: center;
  align-items: center; /* For centering */
}
.modal-content {
  background-color: #fff;
  margin: auto;
  padding: 0;
  width: 90%;
  max-width: 700px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  animation: modal-appear 0.3s ease-out;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  max-height: 90vh;
}
@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 25px;
  border-bottom: 1px solid #ebeef5;
}
.modal-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 500;
}
.close-modal-btn {
  background: none;
  border: none;
  font-size: 1.8rem;
  font-weight: bold;
  line-height: 1;
  color: #909399;
  opacity: 0.7;
  cursor: pointer;
  padding: 0;
}
.close-modal-btn:hover {
  opacity: 1;
  color: #000;
}
.modal-body {
  padding: 20px 25px;
  overflow-y: auto;
  flex-grow: 1;
}
.application-details p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}
.application-details p strong {
  color: #303133;
  min-width: 100px;
  display: inline-block;
}
.reason-box {
  background-color: #f9fafc;
  border: 1px solid #ebeef5;
  padding: 10px;
  border-radius: 4px;
  margin-top: 5px;
  margin-bottom: 15px;
  white-space: pre-wrap;
  max-height: 150px;
  overflow-y: auto;
  font-size: 14px;
  color: #303133;
}
.grade-highlight.original {
  color: #f56c6c;
  font-weight: bold;
}
.grade-highlight.new {
  color: #67c23a;
  font-weight: bold;
}
#modalEvidenceLink router-link {
  word-break: break-all;
}
#modalEvidenceLink:empty::after {
  content: "无";
  color: #909399;
  font-style: italic;
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 25px;
  border-top: 1px solid #ebeef5;
  margin-top: auto;
  background-color: #f9fafc;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  /* .top-bar responsive styles are at the top */
  .page-main {
    padding: 15px;
  }
  .filter-form-grid {
    grid-template-columns: 1fr;
  }
  .filter-form-grid .form-action-group {
    flex-direction: column;
    gap: 10px;
  }
  .filter-form-grid .form-action-group .btn {
    width: 100%;
  }
  .modal-content {
    width: 95%;
  }
  .application-details p strong {
    display: block;
    margin-bottom: 3px;
  }
}
</style>
