<template>
  <router-view />
</template>

<script setup>
import { onMounted } from "vue";
import { useRoute } from "vue-router";
import { useuserLoginStore } from "@/store/userLoginStore";

const loginUserStore = useuserLoginStore();
const route = useRoute();

onMounted(() => {
  // if (!route.path.includes("login")) {
  //   // loginUserStore.fetchLoginUser();
  //   // loginUserStore.setLoginUserUnlogin();
  // }
});
</script>

<style></style>
