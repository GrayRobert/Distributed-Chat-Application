<template>
  <div id="app">
    <Navbar/>
    <Chat/>
  </div>
</template>
<script>
import Chat from './components/Chat.vue'
import Navbar from './components/Navbar.vue'
export default {
  name: 'app',
  components: {
      'Navbar': Navbar,
      'Chat': Chat,
  }
}
</script>