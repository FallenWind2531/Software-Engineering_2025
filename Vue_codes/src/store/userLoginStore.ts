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

    async function fetchLoginUser() {
      const res: AxiosResponse<any, any> = await getCurrentUserProfile();
      if (res.data.code === 200 && res.data.user_id) {
        loginUser.value.user_id = res.data.user_info.user_id;
        loginUser.value.name = res.data.user_info.name;
        loginUser.value.role = res.data.user_info.role;
        loginUser.value.department = res.data.user_info.department;
        loginUser.value.contact = res.data.user_info.contact;
        loginUser.value.avatar_path = res.data.user_info.avatar_path;
        loginUser.value.token = res.data.token;
      } else await setLoginUserUnlogin();
    }

    async function setLoginUserUnlogin() {
      loginUser.value = unLoginUser.value;
    }

    return { loginUser, fetchLoginUser, setLoginUserUnlogin, unLoginUser };
  },
});
