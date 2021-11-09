import { createApp } from 'vue';
import App from './App.vue';
import localizeFilter from './filters/localizeFilter.js';
import router from './router';


const app = createApp(App).use(router);

app.config.globalProperties.$filters = {
    localize(key) {
        return localizeFilter(key)
    }
}
app.mount("#app");
