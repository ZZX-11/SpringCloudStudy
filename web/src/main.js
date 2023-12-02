import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {notification} from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import * as Icons from "@ant-design/icons-vue";
import axios from "axios";
import './assets/js/enums';
// createApp(App).use(Antd).use(store).use(router).mount('#app')
// app页面用于和Index下的app标签关联起来
const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app')

// 导入图标
// app.component(i, icons[i]) 是一个 Vue 3 的方法调用，用于注册全局组件。
// 在这段代码中，i 是一个变量，代表图标的名称，icons[i] 则是对应图标的组件。
// 通过循环遍历 icons 对象的属性，将每个图标组件注册为全局组件。
// 这样，在应用程序中的任何地方都可以直接使用这些图标组件，而无需在每个组件中单独导入和注册。
const icons = Icons;
for(const i in icons){
    app.component(i,icons[i]);
}

/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    const _token = store.state.member.token;
    // 对于所有的前端请求，在请求时给header送入token
    if (_token) {
        config.headers.token = _token;
        console.log("请求headers增加token:", _token);
    }
    return config;
}, error => {
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    console.log('返回结果：', response);
    return response;
}, error => {
    console.log('返回错误：', error);
    const response = error.response;
    const status = response.status;
    if (status === 401) {
        // 判断状态码是401 跳转到登录页
        console.log("未登录或登录超时，跳到登录页");
        store.commit("setMember", {});
        notification.error({ description: "未登录或登录超时" });
        router.push('/login');
    }
    return Promise.reject(error);
});
// 配置axios的baseURL
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);