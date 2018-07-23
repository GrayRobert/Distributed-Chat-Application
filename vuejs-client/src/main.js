import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import "vue-material-design-icons/styles.css"

import MenuIcon from "vue-material-design-icons/menu.vue"
import UserIcon from "vue-material-design-icons/account-outline.vue"

Vue.component("menu-icon", MenuIcon)
Vue.component("account-outline", UserIcon)


Vue.config.productionTip = false

new Vue({
    render: h => h(App),
    el: '#app',
    components: {
        "menu-icon": MenuIcon,
        "account-outline": UserIcon
    }
  })

