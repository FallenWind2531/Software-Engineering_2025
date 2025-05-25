import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import AdminApproveGradeChange from "@/views/Admin_ApproveGradeChange.vue";
import Login from "@/views/LoGin.vue";
import AdminDashboard from "@/views/Admin_Dashboard.vue";
import AdminCourseManagement from "@/views/Admin_CourseManagement.vue";
import AdminUserManagement from "@/views/Admin_UserManagement.vue";
import AdminProfile from "@/views/Admin-Profile.vue";
import ChangePassword from "@/views/ChangePassword.vue";
import QueryCourse from "@/views/Query_Course.vue";
import StudentDashboard from "@/views/Student_Dashboard.vue";
import StudentGradeAnalysis from "@/views/Student_GradeAnalysis.vue";
import StudentProfile from "@/views/Student_Profile.vue";
import StudentQueryGrades from "@/views/Student_QueryGrades.vue";
import StudentSelectedCourses from "@/views/Student_SelectCourses.vue";
import TeacherSubmitGrades from "@/views/Teacher_SubmitGrades.vue";
import TeacherCourseGradeAnalysis from "@/views/Teacher_CourseGradeAnalysis.vue";
import TeacherDashboard from "@/views/Teacher_Dashboard.vue";
import TeacherEditCourse from "@/views/Teacher_EditCourse.vue";
import TeacherProfile from "@/views/Teacher_Profile.vue";
import TeacherRequestGradeChange from "@/views/Teacher_RequestGradeChange.vue";
import TeacherTaughtCourses from "@/views/Teacher_TaughtCourses.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Home",
    component: AdminDashboard,
  },
  {
    path: "/admin/approve",
    name: "Admin_ApproveGradeChange",
    component: AdminApproveGradeChange,
  },
  {
    path: "/admin/profile",
    name: "Admin_Profile",
    component: AdminProfile,
  },
  {
    path: "/admin/course-management",
    name: "Admin_CourseManagement",
    component: AdminCourseManagement,
  },
  {
    path: "/admin/dashboard",
    name: "Admin_Dashboard",
    component: AdminDashboard,
  },
  {
    path: "/admin/user-management",
    name: "Admin_UserManagement",
    component: AdminUserManagement,
  },
  {
    path: "/find-password",
    name: "Find_Password",
    component: ChangePassword,
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/change-password",
    name: "ChangePassword",
    component: ChangePassword,
  },
  {
    path: "/query-course",
    name: "Query_Course",
    component: QueryCourse,
  },
  {
    path: "/student/dashboard",
    name: "Student_Dashboard",
    component: StudentDashboard,
  },
  {
    path: "/student/grade-analysis",
    name: "Student_GradeAnalysis",
    component: StudentGradeAnalysis,
  },
  {
    path: "/student/profile",
    name: "Student_Profile",
    component: StudentProfile,
  },
  {
    path: "/student/query-grades",
    name: "Student_QueryGrades",
    component: StudentQueryGrades,
  },
  {
    path: "/student/selected-courses",
    name: "Student_SelectedCourses",
    component: StudentSelectedCourses,
  },
  {
    path: "/teacher/submit-grades",
    name: "Submit_Grades",
    component: TeacherSubmitGrades,
  },
  {
    path: "/teacher/course-grade-analysis",
    name: "Teacher_CourseGradeAnalysis",
    component: TeacherCourseGradeAnalysis,
  },
  {
    path: "/teacher/dashboard",
    name: "Teacher_Dashboard",
    component: TeacherDashboard,
  },
  {
    path: "/teacher/edit-course",
    name: "Teacher_EditCourse",
    component: TeacherEditCourse,
  },
  {
    path: "/teacher/profile",
    name: "Teacher_Profile",
    component: TeacherProfile,
  },
  {
    path: "/teacher/request-grade-change",
    name: "Teacher_RequestGradeChange",
    component: TeacherRequestGradeChange,
  },
  {
    path: "/teacher/taught-courses",
    name: "Teacher_TaughtCourses",
    component: TeacherTaughtCourses,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
