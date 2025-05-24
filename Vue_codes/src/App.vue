<template>
  <router-view />
</template>

<script setup>
import { useuserLoginStore } from "@/store/userLoginStore";

const loginUserStore = useuserLoginStore();
// 改成fetch 现在没连后端 会报网络错误
// loginUserStore.fetchLoginUser();
loginUserStore.setLoginUserUnlogin();
</script>

<style></style>
