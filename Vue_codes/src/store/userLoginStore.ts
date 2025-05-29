import { defineStore } from "pinia";
import { getCurrentUserProfile } from "@/api/account";
import { Ref, ref } from "vue";
import { AxiosResponse } from "axios";

export const useuserLoginStore = defineStore("userLogin", {
  state: () => {
    const loginUser: Ref<any, any> = ref({
      user_id: "null",
      name: "未登录",
      account: "null",
      role: "null",
      department: "null",
      contact: "null",
      avatar_path: "null",
      token: "null",
    });

    const unLoginUser: Ref<any, any> = ref({
      user_id: "null",
      name: "未登录",
      account: "null",
      role: "null",
      department: "null",
      contact: "null",
      avatar_path: "null",
      token: "null",
    });

    // async function fetchLoginUser() {
    //   const res: AxiosResponse<any, any> = await getCurrentUserProfile();
    //   if (res.data.code === 200 && res.data.user_id) {
    //     loginUser.value.user_id = res.data.userInfo.user_id;
    //     loginUser.value.name = res.data.userInfo.name;
    //     loginUser.value.role = res.data.userInfo.role;
    //     loginUser.value.department = res.data.userInfo.department;
    //     loginUser.value.contact = res.data.userInfo.contact;
    //     loginUser.value.avatar_path = res.data.userInfo.avatar_path;
    //     loginUser.value.token = res.data.token;
    //   } else await setLoginUserUnlogin();
    // }

    async function setLoginUser(response: AxiosResponse<any, any>) {
      const res = response;
      try {
        // 确保响应状态和数据都是有效的
        if (
          res.status === 200 &&
          res.data.code === 200 &&
          res.data.data &&
          res.data.data.userInfo
        ) {
          loginUser.value.user_id = res.data.data.userInfo.user_id;
          loginUser.value.name = res.data.data.userInfo.name;
          loginUser.value.role = res.data.data.userInfo.role;
          loginUser.value.department = res.data.data.userInfo.department;
          loginUser.value.contact = res.data.data.userInfo.contact;
          loginUser.value.avatar_path = res.data.data.userInfo.avatarPath;
          loginUser.value.token = res.data.data.token;
          console.log("用户登录状态已更新:", loginUser.value);
          return true;
        } else {
          console.error("登录响应数据无效:", res.data);
          await setLoginUserUnlogin();
          return false;
        }
      } catch (error) {
        console.error("处理登录数据时出错:", error);
        await setLoginUserUnlogin();
        return false;
      }
    }

    async function setLoginUserUnlogin() {
      loginUser.value = unLoginUser.value;
    }

    return { loginUser, setLoginUser, setLoginUserUnlogin, unLoginUser };
  },
});
