import { createStore } from 'vuex'

const MEMBER = "MEMBER";
// store可以用来保存vue前端的全局变量
export default createStore({
  state: {
    member: window.SessionStorage.get(MEMBER) || {}
  //   它的初始值是从 window.SessionStorage 中获取的 MEMBER 值，如果没有获取到，则默认为空对象 {}。
  },
  getters: {
  },
  mutations: {
    setMember (state, _member) {
      state.member = _member;
      window.SessionStorage.set(MEMBER, _member);
    }
  },
  actions: {
  },
  modules: {
  //   这个里面可以定义多组值
  }
})
