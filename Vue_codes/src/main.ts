import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { far } from "@fortawesome/free-regular-svg-icons";
import { fab } from "@fortawesome/free-brands-svg-icons";
import "@fortawesome/fontawesome-svg-core/styles.css";

const app = createApp(App);
const pinia = createPinia();

// 添加需要的图标
library.add(
  ...Object.values(fas),
  ...Object.values(far),
  ...Object.values(fab)
);

app
  .use(pinia)
  .use(router)
  .component("FontAwesomeIcon", FontAwesomeIcon)
  .mount("#app");
