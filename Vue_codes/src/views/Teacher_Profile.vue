<template>
  <div>
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
        <span class="system-subname">信息管理子系统 - 个人信息</span>
      </div>
      <div class="right-section">
        <div class="user-info" id="userInfoToggle" @click="toggleUserDropdown">
          <!-- 显示头像 -->
          <img
            :src="studentProfileData.avatar_path || defaultAvatar"
            alt="User Avatar"
            class="user-avatar"
          />
          <span class="user-name" id="profileUserName">{{
            studentProfileData.name
          }}</span>
          <FontAwesomeIcon icon="fas fa-angle-down" />
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
      <div class="container profile-container">
        <div class="card profile-card">
          <h2 class="card-title">
            <FontAwesomeIcon icon="fas fa-id-badge" /> 个人基本信息
          </h2>
          <form id="studentProfileForm">
            <div class="profile-grid">
              <div class="form-group">
                <label for="name">姓名:</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  :value="studentProfileData.name"
                  @input="studentProfileData.name = $event.target.value"
                  :readonly="!isEditing"
                  placeholder="请输入您的姓名"
                />
              </div>
              <div class="form-group">
                <label for="role">身份:</label>
                <input
                  type="text"
                  id="role"
                  name="role"
                  readonly
                  :value="studentProfileData.role"
                />
              </div>
              <div class="form-group">
                <label for="department">部门/院系:</label>
                <input
                  type="text"
                  id="department"
                  name="department"
                  :value="studentProfileData.department"
                  @input="studentProfileData.department = $event.target.value"
                  :readonly="!isEditing"
                  placeholder="请输入您的部门/院系"
                />
              </div>
              <div class="form-group">
                <label for="contact">联系方式:</label>
                <input
                  type="text"
                  id="contact"
                  name="contact"
                  :value="studentProfileData.contact"
                  @input="studentProfileData.contact = $event.target.value"
                  :readonly="!isEditing"
                  placeholder="请输入您的联系方式"
                />
              </div>
              <div class="form-group">
                <label for="avatar_path">头像:</label>
                <div class="avatar-wrapper">
                  <img
                    :src="studentProfileData.avatar_path || defaultAvatar"
                    alt="User Avatar"
                    class="avatar-preview"
                  />
                  <div v-if="isUploading" class="uploading-overlay">
                    <FontAwesomeIcon icon="fas fa-spinner" spin />
                  </div>
                </div>
                <!-- 上传头像按钮 -->
                <input
                  type="file"
                  id="avatarUpload"
                  name="avatarUpload"
                  @change="handleAvatarUpload"
                  style="display: none"
                />
                <button
                  type="button"
                  class="btn btn-secondary"
                  @click="triggerAvatarUpload"
                  :disabled="isUploading"
                >
                  <FontAwesomeIcon icon="fas fa-upload" />
                  {{ isUploading ? "上传中..." : "上传头像" }}
                </button>
              </div>
            </div>

            <div class="form-actions">
              <button
                type="button"
                class="btn btn-secondary"
                id="editModeBtn"
                @click="enterEditMode"
                v-if="!isEditing"
              >
                <FontAwesomeIcon icon="fas fa-edit" /> 编辑信息
              </button>
              <button
                type="button"
                class="btn btn-primary"
                id="saveChangesBtn"
                @click="saveChanges"
                v-if="isEditing"
              >
                <FontAwesomeIcon icon="fas fa-save" /> 保存更改
              </button>
              <button
                type="button"
                class="btn btn-default"
                id="cancelEditBtn"
                @click="cancelEdit"
                v-if="isEditing"
              >
                <FontAwesomeIcon icon="fas fa-times" /> 取消编辑
              </button>
            </div>
          </form>
        </div>
        <div
          id="notificationArea"
          class="notification"
          :style="{ display: notificationVisible ? 'block' : 'none' }"
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
import {
  getCurrentUserProfile,
  updateCurrentUserProfile,
  uploadAvatar,
} from "@/api/account";
import { useuserLoginStore } from "@/store/userLoginStore";

const loginUserStore = useuserLoginStore();
// 响应式数据
const studentProfileData = ref({
  user_id: "",
  name: "",
  account: "",
  role: "",
  department: "",
  contact: "",
  avatar_path: "",
});
const originalEditableData = ref({
  name: "",
  department: "",
  contact: "",
});
const isEditing = ref(false);
const userDropdownVisible = ref(false);
const notificationVisible = ref(false);
const notificationMessage = ref("");
const isUploading = ref(false);
const defaultAvatar = "path/to/default/avatar.png"; // 替换为默认头像的路径

// 获取当前用户信息
const fetchUserProfile = async () => {
  try {
    const response = await getCurrentUserProfile();
    studentProfileData.value = response.data; // 假设响应数据结构为 { data: {...} }
  } catch (error) {
    showNotification("获取用户信息失败，请稍后重试。", "error");
  }
};

// 进入编辑模式
const enterEditMode = () => {
  originalEditableData.value = {
    name: studentProfileData.value.name,
    department: studentProfileData.value.department,
    contact: studentProfileData.value.contact,
  };
  isEditing.value = true;
  showNotification("您现在可以编辑联系信息了。", "info");
};

// 取消编辑
const cancelEdit = () => {
  studentProfileData.value.name = originalEditableData.value.name;
  studentProfileData.value.department = originalEditableData.value.department;
  studentProfileData.value.contact = originalEditableData.value.contact;
  isEditing.value = false;
  showNotification("编辑已取消。", "info");
};

// 保存更改
const saveChanges = async () => {
  const updatedData = {
    name: studentProfileData.value.name.trim(),
    department: studentProfileData.value.department.trim(),
    contact: studentProfileData.value.contact.trim(),
  };

  // 基本验证
  if (!updatedData.name || !updatedData.department || !updatedData.contact) {
    showNotification("姓名、部门/院系和联系方式不能为空！", "error");
    return;
  }

  showNotification("正在保存更改...", "info");

  try {
    await updateCurrentUserProfile(updatedData);
    showNotification("个人信息已成功更新！", "success");
    isEditing.value = false;
  } catch (error) {
    showNotification("保存更改失败，请稍后重试。", "error");
  }
};

// 显示通知
const showNotification = (message: any, type = "info") => {
  notificationMessage.value = message;
  notificationVisible.value = true;
  setTimeout(() => {
    notificationVisible.value = false;
  }, 3000);
};

// 切换用户下拉菜单
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

const triggerAvatarUpload = () => {
  const avatarUpload = document.getElementById(
    "avatarUpload"
  ) as HTMLInputElement;
  avatarUpload.click();
};

// 处理头像上传
const handleAvatarUpload = async (event: Event) => {
  const files = (event.target as HTMLInputElement).files;
  if (files && files.length > 0) {
    const file = files[0];
    const formData = new FormData();
    formData.append("avatar", file);
    isUploading.value = true;

    try {
      const response = await uploadAvatar(formData);
      showNotification("头像上传成功。", "success");
      console.log("头像上传成功:", response);
      // 假设响应中包含更新后的头像路径
      studentProfileData.value.avatar_path = response.data.avatar_path;
    } catch (error) {
      showNotification("头像上传失败，请稍后重试。", "error");
      console.error("头像上传失败:", error);
    } finally {
      isUploading.value = false;
    }
  }
};

// 页面挂载时获取用户信息
onMounted(() => {
  fetchUserProfile();
});
</script>

<style scoped>
/* 样式保持不变 */
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
  /* ... (与 student_dashboard_style.css 中的 .top-bar 样式完全一致) ... */
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
  padding: 25px;
  width: 100%;
  box-sizing: border-box;
}
.container.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

/* Profile Card */
.card.profile-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 25px 30px;
}
.card-title {
  font-size: 20px;
  color: #303133;
  margin-top: 0;
  margin-bottom: 25px;
  font-weight: 500;
  display: flex;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}
.card-title i {
  margin-right: 10px;
  color: #409eff;
}

/* Profile Form Grid */
.profile-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px 30px; /* row-gap column-gap */
}
.form-group {
  margin-bottom: 0; /* Grid gap handles spacing */
}
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}
.form-group input[type="text"],
.form-group input[type="email"],
.form-group input[type="tel"] {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  background-color: #fff; /* Default background */
}
.form-group input[readonly] {
  background-color: #f5f7fa; /* Readonly fields slightly different background */
  color: #909399;
  cursor: default;
  border-color: #e4e7ed;
}
.form-group input:not([readonly]):focus {
  border-color: #409eff;
  outline: 0;
}
.full-width-group {
  /* For address or other full-width fields */
  grid-column: 1 / -1;
}
.section-divider {
  grid-column: 1 / -1;
  border: none;
  border-top: 1px dashed #ebeef5;
  margin: 15px 0;
}

/* Form Actions */
.form-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  text-align: right; /* Align buttons to the right */
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.btn {
  /* Basic button style, can be from router-link global sheet */
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
  background-color: #fff;
  border-color: #dcdfe6;
  color: #606266;
}
.btn-default:hover {
  border-color: #409eff;
  color: #409eff;
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
  .profile-grid {
    grid-template-columns: 1fr;
  } /* Stack fields vertically */
  .form-actions {
    justify-content: center;
  } /* Center buttons */
  .form-actions .btn {
    flex-grow: 1;
    max-width: 150px;
  }
}

.avatar-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 10px;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  margin-bottom: 10px;
}

.uploading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}
</style>

<!--<template>-->
<!--  <div>-->
<!--    &lt;!&ndash; Top Bar &ndash;&gt;-->
<!--    <div class="top-bar">-->
<!--      <div class="left-section">-->
<!--        <router-link-->
<!--          to="../teacher/dashboard"-->
<!--          class="back-icon"-->
<!--          id="backToTeacherDashboard"-->
<!--        >-->
<!--          <FontAwesomeIcon icon="fas fa-arrow-left" />-->
<!--        </router-link>-->
<!--        <span class="system-name">教学服务系统</span>-->
<!--        <span class="system-subname">信息管理子系统 - 个人信息</span>-->
<!--      </div>-->
<!--      <div class="right-section">-->
<!--        <div class="user-info" id="userInfoToggle" @click="toggleUserDropdown">-->
<!--          <div class="user-avatar">-->
<!--            <FontAwesomeIcon icon="fas fa-chalkboard-teacher" />-->
<!--          </div>-->
<!--          <span class="user-name" id="profileTeacherName">{{-->
<!--            JSON.stringify(loginUserStore.loginUser.name)-->
<!--          }}</span>-->
<!--          <FontAwesomeIcon-->
<!--            icon="fas fa-angle-down"-->
<!--            :class="{ 'is-open': isUserDropdownOpen }"-->
<!--          />-->
<!--        </div>-->
<!--        <div-->
<!--          class="user-dropdown-menu"-->
<!--          id="userDropdown"-->
<!--          :style="{ display: isUserDropdownOpen ? 'block' : 'none' }"-->
<!--        >-->
<!--          <a @click="handleChangePassword">修改密码</a>-->
<!--          <div class="divider"></div>-->
<!--          <a @click="handleLogout">退出登录</a>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

<!--    &lt;!&ndash; Main Content &ndash;&gt;-->
<!--    <main class="page-main">-->
<!--      <div class="container profile-container">-->
<!--        <div class="card profile-card">-->
<!--          <h2 class="card-title">-->
<!--            <FontAwesomeIcon icon="fas fa-user-tie" /> 教师基本信息-->
<!--          </h2>-->
<!--          <form id="teacherProfileForm">-->
<!--            <div class="profile-grid">-->
<!--              <div class="form-group">-->
<!--                <label for="teacherId">教工号:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="teacherId"-->
<!--                  name="teacherId"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.teacherId"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="teacherName">姓名:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="teacherName"-->
<!--                  name="teacherName"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.name"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="gender">性别:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="gender"-->
<!--                  name="gender"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.gender"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="idCardNumber">身份证号:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="idCardNumber"-->
<!--                  name="idCardNumber"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.idCardNumber"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="department">所属院系:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="department"-->
<!--                  name="department"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.department"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="title">职称:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="title"-->
<!--                  name="title"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.title"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="hireDate">入职日期:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="hireDate"-->
<!--                  name="hireDate"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.hireDate"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group">-->
<!--                <label for="researchArea">研究方向:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="researchArea"-->
<!--                  name="researchArea"-->
<!--                  readonly-->
<!--                  :value="teacherProfile.researchArea"-->
<!--                />-->
<!--              </div>-->

<!--              <hr class="section-divider" />-->

<!--              <div class="form-group editable-group">-->
<!--                <label for="email">办公邮箱:</label>-->
<!--                <input-->
<!--                  type="email"-->
<!--                  id="email"-->
<!--                  name="email"-->
<!--                  :value="teacherProfile.email"-->
<!--                  :readonly="!isEditing"-->
<!--                  @input="handleInputChange('email', $event.target.value)"-->
<!--                  placeholder="请输入您的办公邮箱"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group editable-group">-->
<!--                <label for="phoneNumber">办公电话:</label>-->
<!--                <input-->
<!--                  type="tel"-->
<!--                  id="phoneNumber"-->
<!--                  name="phoneNumber"-->
<!--                  :value="teacherProfile.phoneNumber"-->
<!--                  :readonly="!isEditing"-->
<!--                  @input="handleInputChange('phoneNumber', $event.target.value)"-->
<!--                  placeholder="请输入您的办公电话"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group editable-group full-width-group">-->
<!--                <label for="officeLocation">办公地点:</label>-->
<!--                <input-->
<!--                  type="text"-->
<!--                  id="officeLocation"-->
<!--                  name="officeLocation"-->
<!--                  :value="teacherProfile.officeLocation"-->
<!--                  :readonly="!isEditing"-->
<!--                  @input="-->
<!--                    handleInputChange('officeLocation', $event.target.value)-->
<!--                  "-->
<!--                  placeholder="请输入您的办公地点"-->
<!--                />-->
<!--              </div>-->
<!--              <div class="form-group editable-group full-width-group">-->
<!--                <label for="personalHomepage">个人主页 (选填):</label>-->
<!--                <input-->
<!--                  type="url"-->
<!--                  id="personalHomepage"-->
<!--                  name="personalHomepage"-->
<!--                  :value="teacherProfile.personalHomepage"-->
<!--                  :readonly="!isEditing"-->
<!--                  @input="-->
<!--                    handleInputChange('personalHomepage', $event.target.value)-->
<!--                  "-->
<!--                  placeholder="请输入您的个人主页链接"-->
<!--                />-->
<!--              </div>-->
<!--            </div>-->

<!--            <div class="form-actions">-->
<!--              <button-->
<!--                type="button"-->
<!--                class="btn btn-secondary"-->
<!--                id="editModeBtn"-->
<!--                @click="enterEditMode"-->
<!--                v-if="!isEditing"-->
<!--              >-->
<!--                <FontAwesomeIcon icon="fas fa-edit" /> 编辑信息-->
<!--              </button>-->
<!--              <button-->
<!--                type="button"-->
<!--                class="btn btn-primary"-->
<!--                id="saveChangesBtn"-->
<!--                @click="saveChanges"-->
<!--                v-if="isEditing"-->
<!--              >-->
<!--                <FontAwesomeIcon icon="fas fa-save" /> 保存更改-->
<!--              </button>-->
<!--              <button-->
<!--                type="button"-->
<!--                class="btn btn-default"-->
<!--                id="cancelEditBtn"-->
<!--                @click="cancelEdit"-->
<!--                v-if="isEditing"-->
<!--              >-->
<!--                <FontAwesomeIcon icon="fas fa-times" /> 取消编辑-->
<!--              </button>-->
<!--            </div>-->
<!--          </form>-->
<!--        </div>-->
<!--        <div-->
<!--          id="notificationArea"-->
<!--          class="notification"-->
<!--          :class="{-->
<!--            success: notificationType === 'success',-->
<!--            error: notificationType === 'error',-->
<!--            info: notificationType === 'info',-->
<!--          }"-->
<!--          :style="{ display: showNotification ? 'block' : 'none' }"-->
<!--        >-->
<!--          {{ notificationMessage }}-->
<!--        </div>-->
<!--      </div>-->
<!--    </main>-->

<!--    &lt;!&ndash; Bottom Bar &ndash;&gt;-->
<!--    <div class="bottom-bar">-->
<!--      <p class="copyright-text">-->
<!--        版权所有© Copyright 2025 浙江大学 软件工程基础课程 教学服务系统课程设计-->
<!--        信息管理子系统-->
<!--      </p>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup lang="ts">-->
<!--import { ref, onMounted } from "vue";-->
<!--import { getCurrentUserProfile, updateCurrentUserProfile } from "@/api/account";-->
<!--import { useuserLoginStore } from "@/store/userLoginStore";-->

<!--const loginUserStore = useuserLoginStore();-->

<!--type TeacherProfile = {-->
<!--  name: string;-->
<!--  title: string;-->
<!--  teacherId: string;-->
<!--  gender: string;-->
<!--  idCardNumber: string;-->
<!--  department: string;-->
<!--  hireDate: string;-->
<!--  researchArea: string;-->
<!--  email: string;-->
<!--  phoneNumber: string;-->
<!--  officeLocation: string;-->
<!--  personalHomepage: string;-->
<!--};-->

<!--const teacherProfile = ref<TeacherProfile>({-->
<!--  name: "",-->
<!--  title: "",-->
<!--  teacherId: "",-->
<!--  gender: "",-->
<!--  idCardNumber: "",-->
<!--  department: "",-->
<!--  hireDate: "",-->
<!--  researchArea: "",-->
<!--  email: "",-->
<!--  phoneNumber: "",-->
<!--  officeLocation: "",-->
<!--  personalHomepage: "",-->
<!--});-->
<!--const originalEditableData = ref({-->
<!--  email: "",-->
<!--  phoneNumber: "",-->
<!--  officeLocation: "",-->
<!--  personalHomepage: "",-->
<!--});-->

<!--const isUserDropdownOpen = ref(false);-->
<!--const isEditing = ref(false);-->
<!--const showNotification = ref(false);-->
<!--const notificationMessage = ref("");-->
<!--const notificationType = ref("info");-->

<!--const toggleUserDropdown = () => {-->
<!--  isUserDropdownOpen.value = !isUserDropdownOpen.value;-->
<!--};-->

<!--const handleChangePassword = () => {-->
<!--  loginUserStore.setLoginUserUnlogin();-->
<!--  window.location.href = "../change-password";-->
<!--};-->

<!--const handleLogout = async () => {-->
<!--  showNotificationMessage("正在退出登录...", "info");-->
<!--  setTimeout(() => {-->
<!--    loginUserStore.setLoginUserUnlogin();-->
<!--    window.location.href = "../login";-->
<!--  }, 1500);-->
<!--};-->

<!--const populateProfileForm = () => {-->
<!--  if (teacherProfile.value) {-->
<!--    const profileTeacherNameDisplay =-->
<!--      document.getElementById("profileTeacherName");-->
<!--    if (profileTeacherNameDisplay) {-->
<!--      profileTeacherNameDisplay.textContent = `${teacherProfile.value.name} ${teacherProfile.value.title}`;-->
<!--    }-->
<!--  }-->
<!--};-->

<!--const fetchTeacherProfile = async () => {-->
<!--  try {-->
<!--    const res = await getCurrentUserProfile();-->
<!--    if (res.data.code === 200) {-->
<!--      teacherProfile.value = res.data;-->
<!--      populateProfileForm();-->
<!--    }-->
<!--  } catch (error) {-->
<!--    showNotificationMessage("获取教师信息失败", "error");-->
<!--  }-->
<!--};-->

<!--const enterEditMode = () => {-->
<!--  originalEditableData.value = {-->
<!--    email: teacherProfile.value.email,-->
<!--    phoneNumber: teacherProfile.value.phoneNumber,-->
<!--    officeLocation: teacherProfile.value.officeLocation,-->
<!--    personalHomepage: teacherProfile.value.personalHomepage,-->
<!--  };-->
<!--  isEditing.value = true;-->
<!--  showNotificationMessage("您现在可以编辑联系信息了。", "info");-->
<!--};-->

<!--const cancelEdit = () => {-->
<!--  teacherProfile.value.email = originalEditableData.value.email;-->
<!--  teacherProfile.value.phoneNumber = originalEditableData.value.phoneNumber;-->
<!--  teacherProfile.value.officeLocation =-->
<!--    originalEditableData.value.officeLocation;-->
<!--  teacherProfile.value.personalHomepage =-->
<!--    originalEditableData.value.personalHomepage;-->
<!--  isEditing.value = false;-->
<!--  showNotificationMessage("编辑已取消。", "info");-->
<!--};-->

<!--const handleInputChange = (field: keyof TeacherProfile, value: string) => {-->
<!--  teacherProfile.value[field] = value;-->
<!--};-->

<!--const saveChanges = async () => {-->
<!--  const updatedData = {-->
<!--    email: teacherProfile.value.email.trim(),-->
<!--    phoneNumber: teacherProfile.value.phoneNumber.trim(),-->
<!--    officeLocation: teacherProfile.value.officeLocation.trim(),-->
<!--    personalHomepage: teacherProfile.value.personalHomepage.trim(),-->
<!--  };-->

<!--  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;-->
<!--  if (!updatedData.email || !emailRegex.test(updatedData.email)) {-->
<!--    showNotificationMessage("请输入有效的办公邮箱格式。", "error");-->
<!--    return;-->
<!--  }-->

<!--  showNotificationMessage("正在保存更改...", "info");-->

<!--  try {-->
<!--    const res = await updateCurrentUserProfile(updatedData);-->
<!--    if (res.data.code === 200) {-->
<!--      showNotificationMessage("个人信息已成功更新！", "success");-->
<!--      isEditing.value = false;-->
<!--    } else {-->
<!--      showNotificationMessage("保存更改失败", "error");-->
<!--    }-->
<!--  } catch (error) {-->
<!--    showNotificationMessage("保存更改失败", "error");-->
<!--  }-->
<!--};-->

<!--const showNotificationMessage = (message: string, type: string) => {-->
<!--  notificationMessage.value = message;-->
<!--  notificationType.value = type;-->
<!--  showNotification.value = true;-->
<!--  setTimeout(() => {-->
<!--    showNotification.value = false;-->
<!--  }, 3000);-->
<!--};-->

<!--onMounted(() => {-->
<!--  fetchTeacherProfile();-->
<!--});-->
<!--</script>-->

<!--<style scoped>-->
<!--/* 样式保持不变 */-->
<!--/* Global Styles */-->
<!--body {-->
<!--  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,-->
<!--    "Helvetica Neue", Arial, "Noto Sans", sans-serif;-->
<!--  margin: 0;-->
<!--  background-color: #f4f6f8;-->
<!--  color: #333;-->
<!--  line-height: 1.6;-->
<!--  padding-top: 60px;-->
<!--  padding-bottom: 50px;-->
<!--  box-sizing: border-box;-->
<!--  min-height: 100vh;-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--}-->
<!--router-link {-->
<!--  text-decoration: none;-->
<!--  color: #409eff;-->
<!--}-->
<!--router-link:hover {-->
<!--  text-decoration: none;-->
<!--  color: #1370eb;-->
<!--}-->

<!--/* Top Bar (Consistent with previous pages) */-->
<!--.top-bar {-->
<!--  height: 60px;-->
<!--  background-color: #409eff;-->
<!--  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  align-items: center;-->
<!--  padding: 0 30px;-->
<!--  position: fixed;-->
<!--  top: 0;-->
<!--  left: 0;-->
<!--  right: 0;-->
<!--  z-index: 1000;-->
<!--  box-sizing: border-box;-->
<!--}-->
<!--.top-bar .left-section {-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--}-->
<!--.top-bar .back-icon {-->
<!--  color: white;-->
<!--  font-size: 20px;-->
<!--  margin-right: 20px;-->
<!--  cursor: pointer;-->
<!--  transition: opacity 0.2s;-->
<!--}-->
<!--.top-bar .back-icon:hover {-->
<!--  opacity: 0.7;-->
<!--}-->
<!--.top-bar .system-name {-->
<!--  font-size: 24px;-->
<!--  font-weight: bold;-->
<!--  color: white;-->
<!--}-->
<!--.top-bar .system-subname {-->
<!--  font-size: 16px;-->
<!--  color: white;-->
<!--  margin-left: 15px;-->
<!--}-->

<!--.top-bar .right-section {-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--}-->
<!--.top-bar .user-info {-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  cursor: pointer;-->
<!--  padding: 5px 10px;-->
<!--  border-radius: 20px;-->
<!--  transition: background-color 0.2s;-->
<!--  position: relative;-->
<!--}-->
<!--.top-bar .user-info:hover {-->
<!--  background-color: #3a8ee6;-->
<!--}-->
<!--.top-bar .user-avatar {-->
<!--  width: 32px;-->
<!--  height: 32px;-->
<!--  border-radius: 50%;-->
<!--  background-color: #fff;-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--  margin-right: 8px;-->
<!--  overflow: hidden;-->
<!--}-->
<!--.top-bar .user-avatar i {-->
<!--  font-size: 18px;-->
<!--  color: #409eff;-->
<!--  line-height: 1;-->
<!--}-->
<!--.top-bar .user-name {-->
<!--  margin: 0 8px 0 0;-->
<!--  font-size: 14px;-->
<!--  color: white;-->
<!--  line-height: 1;-->
<!--}-->
<!--.top-bar .user-info .fa-angle-down {-->
<!--  font-size: 12px;-->
<!--  color: white;-->
<!--  margin-left: 5px;-->
<!--  transition: transform 0.2s;-->
<!--}-->
<!--.top-bar .user-info.is-open .fa-angle-down {-->
<!--  transform: rotate(180deg);-->
<!--}-->
<!--.top-bar .user-dropdown-menu {-->
<!--  display: none;-->
<!--  position: absolute;-->
<!--  top: calc(100% + 5px);-->
<!--  right: 0;-->
<!--  background-color: white;-->
<!--  border: 1px solid #ebeef5;-->
<!--  border-radius: 4px;-->
<!--  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);-->
<!--  min-width: 130px;-->
<!--  z-index: 1001;-->
<!--}-->
<!--.top-bar .user-dropdown-menu a {-->
<!--  display: block;-->
<!--  padding: 8px 15px;-->
<!--  color: #606266;-->
<!--  font-size: 14px;-->
<!--}-->
<!--.top-bar .user-dropdown-menu a:hover {-->
<!--  background-color: #ecf5ff;-->
<!--  color: #409eff;-->
<!--}-->
<!--.top-bar .user-dropdown-menu .divider {-->
<!--  height: 1px;-->
<!--  background-color: #ebeef5;-->
<!--  margin: 5px 0;-->
<!--}-->

<!--/* Main Content (Similar to student_profile_style.css) */-->
<!--.page-main {-->
<!--  flex-grow: 1;-->
<!--  padding: 25px;-->
<!--  width: 100%;-->
<!--  box-sizing: border-box;-->
<!--}-->
<!--.container.profile-container {-->
<!--  max-width: 900px;-->
<!--  margin: 0 auto;-->
<!--}-->

<!--.card.profile-card {-->
<!--  background-color: white;-->
<!--  border-radius: 8px;-->
<!--  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);-->
<!--  padding: 25px 30px;-->
<!--}-->
<!--.card-title {-->
<!--  font-size: 20px;-->
<!--  color: #303133;-->
<!--  margin-top: 0;-->
<!--  margin-bottom: 25px;-->
<!--  font-weight: 500;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  padding-bottom: 15px;-->
<!--  border-bottom: 1px solid #ebeef5;-->
<!--}-->
<!--.card-title i {-->
<!--  margin-right: 10px;-->
<!--  color: #409eff;-->
<!--}-->

<!--.profile-grid {-->
<!--  display: grid;-->
<!--  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));-->
<!--  gap: 20px 30px;-->
<!--}-->
<!--.form-group {-->
<!--  margin-bottom: 0;-->
<!--}-->
<!--.form-group label {-->
<!--  display: block;-->
<!--  margin-bottom: 6px;-->
<!--  font-weight: 500;-->
<!--  color: #606266;-->
<!--  font-size: 14px;-->
<!--}-->
<!--.form-group input[type="text"],-->
<!--.form-group input[type="email"],-->
<!--.form-group input[type="tel"],-->
<!--.form-group input[type="url"] {-->
<!--  /* Added type="url" */-->
<!--  width: 100%;-->
<!--  padding: 9px 12px;-->
<!--  border: 1px solid #dcdfe6;-->
<!--  border-radius: 4px;-->
<!--  box-sizing: border-box;-->
<!--  font-size: 14px;-->
<!--  color: #606266;-->
<!--  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);-->
<!--  background-color: #fff;-->
<!--}-->
<!--.form-group input[readonly] {-->
<!--  background-color: #f5f7fa;-->
<!--  color: #909399;-->
<!--  cursor: default;-->
<!--  border-color: #e4e7ed;-->
<!--}-->
<!--.form-group input:not([readonly]):focus {-->
<!--  border-color: #409eff;-->
<!--  outline: 0;-->
<!--}-->
<!--.full-width-group {-->
<!--  grid-column: 1 / -1;-->
<!--}-->
<!--.section-divider {-->
<!--  grid-column: 1 / -1;-->
<!--  border: none;-->
<!--  border-top: 1px dashed #ebeef5;-->
<!--  margin: 15px 0;-->
<!--}-->

<!--.form-actions {-->
<!--  margin-top: 30px;-->
<!--  padding-top: 20px;-->
<!--  border-top: 1px solid #ebeef5;-->
<!--  text-align: right;-->
<!--  display: flex;-->
<!--  justify-content: flex-end;-->
<!--  gap: 10px;-->
<!--}-->
<!--.btn {-->
<!--  padding: 9px 18px;-->
<!--  border: 1px solid transparent;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--  font-size: 14px;-->
<!--  font-weight: 500;-->
<!--  transition: 0.1s;-->
<!--  text-align: center;-->
<!--  user-select: none;-->
<!--  white-space: nowrap;-->
<!--  display: inline-flex;-->
<!--  align-items: center;-->
<!--  gap: 6px;-->
<!--}-->
<!--.btn i {-->
<!--  font-size: 1em;-->
<!--}-->
<!--.btn-primary {-->
<!--  background-color: #409eff;-->
<!--  border-color: #409eff;-->
<!--  color: white;-->
<!--}-->
<!--.btn-primary:hover {-->
<!--  background-color: #66b1ff;-->
<!--  border-color: #66b1ff;-->
<!--}-->
<!--.btn-secondary {-->
<!--  background-color: #909399;-->
<!--  border-color: #909399;-->
<!--  color: white;-->
<!--}-->
<!--.btn-secondary:hover {-->
<!--  background-color: #a6a9ad;-->
<!--  border-color: #a6a9ad;-->
<!--}-->
<!--.btn-default {-->
<!--  background-color: #fff;-->
<!--  border-color: #dcdfe6;-->
<!--  color: #606266;-->
<!--}-->
<!--.btn-default:hover {-->
<!--  border-color: #409eff;-->
<!--  color: #409eff;-->
<!--}-->

<!--/* Notification Area */-->
<!--.notification {-->
<!--  padding: 10px 15px;-->
<!--  margin-top: 20px;-->
<!--  border-radius: 4px;-->
<!--  text-align: center;-->
<!--  font-size: 14px;-->
<!--  border: 1px solid transparent;-->
<!--}-->
<!--.notification.success {-->
<!--  background-color: #f0f9eb;-->
<!--  color: #67c23a;-->
<!--  border-color: #e1f3d8;-->
<!--}-->
<!--.notification.error {-->
<!--  background-color: #fef0f0;-->
<!--  color: #f56c6c;-->
<!--  border-color: #fde2e2;-->
<!--}-->
<!--.notification.info {-->
<!--  background-color: #edf2fc;-->
<!--  color: #909399;-->
<!--  border-color: #e4e7ed;-->
<!--}-->

<!--/* Bottom Bar */-->
<!--.bottom-bar {-->
<!--  background-color: #f5f5f5b7;-->
<!--  padding: 12px 0;-->
<!--  text-align: center;-->
<!--  width: 100%;-->
<!--  position: fixed;-->
<!--  bottom: 0;-->
<!--  left: 0;-->
<!--  z-index: 999;-->
<!--  box-sizing: border-box;-->
<!--}-->
<!--.copyright-text {-->
<!--  color: #666;-->
<!--  font-size: 12.5px;-->
<!--  margin: 0;-->
<!--  line-height: 1.5;-->
<!--}-->

<!--/* Responsive Adjustments */-->
<!--@media (max-width: 768px) {-->
<!--  .top-bar {-->
<!--    padding: 0 15px;-->
<!--    height: 50px;-->
<!--  }-->
<!--  body {-->
<!--    padding-top: 50px;-->
<!--  }-->
<!--  .top-bar .system-name {-->
<!--    font-size: 18px;-->
<!--  }-->
<!--  .top-bar .system-subname {-->
<!--    display: none;-->
<!--  }-->
<!--  .page-main {-->
<!--    padding: 15px;-->
<!--  }-->
<!--  .profile-grid {-->
<!--    grid-template-columns: 1fr;-->
<!--  }-->
<!--  .form-actions {-->
<!--    justify-content: center;-->
<!--  }-->
<!--  .form-actions .btn {-->
<!--    flex-grow: 1;-->
<!--    max-width: 150px;-->
<!--  }-->
<!--}-->
<!--</style>-->
