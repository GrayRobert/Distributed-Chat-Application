<template>
  <div id="app">
    <Nav/>
    <Chat/>
  </div>
</template>
<script>
import Chat from './components/Chat.vue'
import Nav from './components/Nav.vue'
export default {
  name: 'app',
  components: {
      'Nav': Nav,
      'Chat': Chat,
  }
}
</script>