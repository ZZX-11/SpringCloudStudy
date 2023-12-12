<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :openKeys="['batch', 'base', 'business','member']"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          <user-outlined /> &nbsp; 关于
        </router-link>
      </a-menu-item>
      <a-sub-menu key="member">
        <template #title>
          <span>
            <UnorderedListOutlined />
            会员管理
          </span>
        </template>
        <a-menu-item key="/member/ticket">
          <router-link to="/member/ticket">
            <MenuUnfoldOutlined /> 会员车票
          </router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="business">
        <template #title>
          <span>
            <UnorderedListOutlined />
            业务管理
          </span>
        </template>
        <a-menu-item key="/business/confirm-order">
          <router-link to="/business/confirm-order">
            <user-outlined /> &nbsp; 订单信息
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-ticket">
          <router-link to="/business/daily-train-ticket">
            <user-outlined /> &nbsp; 余票信息
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train">
          <router-link to="/business/daily-train">
            <user-outlined /> &nbsp; 每日车次
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-station">
          <router-link to="/business/daily-train-station">
            <user-outlined /> &nbsp; 每日车站
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-carriage">
          <router-link to="/business/daily-train-carriage">
            <user-outlined /> &nbsp; 每日车厢
          </router-link>
        </a-menu-item>
        <a-menu-item key="/business/daily-train-seat">
          <router-link to="/business/daily-train-seat">
            <user-outlined /> &nbsp; 每日座位
          </router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="base">
        <template #title>
          <span>
            <UnorderedListOutlined />
            基础数据
          </span>
        </template>
        <a-menu-item key="/base/station">
          <router-link to="/base/station">
            <user-outlined /> &nbsp; 车站管理
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train">
          <router-link to="/base/train">
            <user-outlined /> &nbsp; 火车管理
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train-station">
          <router-link to="/base/train-station">
            <user-outlined /> &nbsp; 火车与车站
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train-carriage">
          <router-link to="/base/train-carriage">
            <user-outlined /> &nbsp; 火车车厢
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train-seat">
          <router-link to="/base/train-seat">
            <user-outlined /> &nbsp; 火车座位
          </router-link>
        </a-menu-item>

      </a-sub-menu>
      <a-sub-menu key="batch">
        <template #title>
          <span>
            <UnorderedListOutlined />
            跑批管理
          </span>
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <MenuUnfoldOutlined /> &nbsp; 任务管理
          </router-link>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import {LaptopOutlined, NotificationOutlined, UserOutlined} from "@ant-design/icons-vue";
import router from "@/router";

export default defineComponent({
  name: "the-sider-view",
  components: {NotificationOutlined, UserOutlined, LaptopOutlined},
  //给组件命名.该组件可以在别的地方通过<the-header-view></the-header-view> 这样的方式使用
  setup() {
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      selectedKeys,
      openKeys: ref(['sub1']),
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
