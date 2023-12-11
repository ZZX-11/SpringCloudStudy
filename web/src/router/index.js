import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
{
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
    {
    path: '/',
    name: 'main',
    component: () => import('../views/main.vue'),
    //  在Vue的路由中，meta是一个用于存储额外信息的对象。它可以在路由配置中的meta字段中定义，并且可以在路由导航过程中访问。
    //  meta对象可以包含任意的自定义属性，用于描述路由的特性或其他相关信息。常见的用法是在路由中定义权限信息，以便在导航守卫中进行权限验证。
    meta: {
        loginRequire: true
    },
    children: [{
//  二级路由或者说是子路由。拼接上一个路由的路径。比如上一级是/main，这一级在它后面拼接/main/welcome
        path: 'welcome',
        component: () => import('../views/main/welcome.vue'),
    },{
        path: 'passenger',
        component: () => import('../views/main/passenger.vue'),
    },{
        path: 'ticket',
        component: () => import('../views/main/ticket.vue'),
    }, {
        path: 'order',
        component: () => import('../views/main/order.vue'),
    }]
   },
    {
//  访问定向 ： 默认打开welcome页面
        path: '',
        redirect: '/welcome'
    }
]
// 配置了基本路由信息
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由登录拦截
// 对每一个前端的页面，在打开它时，必须要有token，否则不能打开,这个功能通过 meta实现
router.beforeEach((to, from, next) => {
    // 要不要对meta.loginRequire属性做监控拦截
    // 在导航守卫中，to参数表示即将导航到的目标路由对象。to.matched是一个数组，包含了与目标路由路径匹配的所有路由记录。
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登录校验：", item.meta.loginRequire || false);
        return item.meta.loginRequire
    })) {
        const _member = store.state.member;
        console.log("页面登录校验开始：", _member);
        if (!_member.token) {
            console.log("用户未登录或登录超时！");
            notification.error({ description: "未登录或登录超时" });
            next('/login');
        } else {
            // 恢复到to原本的
            next();
        }
    } else {
        next();
    }
});
export default router
