import axios from "axios";
// import { useuserLoginStore } from "@/store/userLoginStore";
//
// const loginUser = useuserLoginStore();

const apiClient = axios.create({
  baseURL: "http://localhost:8080/information/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

// 添加请求拦截器，自动添加Authorization头
apiClient.interceptors.request.use(
  async (config) => {
    const { useuserLoginStore } = await import("@/store/userLoginStore");
    const loginUserStore = useuserLoginStore();
    const token = loginUserStore.loginUser.token;
    const url = config.url || "";

    if (token && !url.includes("login")) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(function (response) {
  console.log(response);
  if (response.data.code === 401) {
    if (
      !window.location.pathname.includes("/login") &&
      !window.location.pathname.includes("/change-password")
    ) {
      window.location.href = "@/login";
    }
  }
  return response;
});

export default apiClient;
