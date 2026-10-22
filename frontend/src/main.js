/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */import{createApp}from'vue';import App from'./App.vue';import'./style.css';createApp(App).mount('#app');
import AiPracticalWorkbench from './components/AiPracticalWorkbench.vue'
const workbenchHost=document.createElement('div')
workbenchHost.id='zhuatech-practical-workbench'
document.body.appendChild(workbenchHost)
createApp(AiPracticalWorkbench).mount(workbenchHost)
