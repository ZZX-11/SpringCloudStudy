<template>
  <a-layout id="components-layout-demo-top-side-2">
    <the-header-view></the-header-view>
    <a-layout>
      <the-sider-view></the-sider-view>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>
<script>

import { UserOutlined, LaptopOutlined, NotificationOutlined } from '@ant-design/icons-vue';
import { defineComponent, ref } from 'vue';
import TheHeaderView from "@/components/the-header.vue";
import TheSiderView from "@/components/the-sider.vue";

// useRoute 是当前路由：当前路由的参数。 useRouter 是管理全局路由的.实现路由的跳转
export default defineComponent({
  components: {
    TheSiderView,
    TheHeaderView,
  },
  setup() {
    return {
      collapsed: ref(false),
    };
  },
});
</script>


<style>
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}
</style>