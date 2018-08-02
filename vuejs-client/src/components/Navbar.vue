<template>
    <div class="navbar sticky-top box">
        <ul>
            <li class="left"><a href="#"><menu-icon fillColor="#FF0000" /></a></li>
            <li class="title"><a href="#">
                <object data="/img/icons/chatappus-379x349-transp.svg" type="image/svg+xml" height="30" style="vertical-align:middle">
                    <img src="yourfallback.jpg" />
                </object>
                <span>ChatAppUs</span>
                </a></li>
            <li class="right"><a href="#"><account-outline fillColor="#FF0000" /></a></li>
        </ul>
    </div>
</template>

<script>
    export default {
        name: 'navbar',
    }
</script>


<style lang="less">

.navbar {
    background-color: #9b4dca;
    height:30px;
    z-index: 999;
}

.navbar ul {
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
    background-color: #9b4dca;
    position: fixed;
    top: 0;
    width: 100%;
}

.navbar .left {
    float: left;
}

.navbar .right {
    float: right;
}

.navbar .title {
    font-family: 'Kirang Haerang', cursive;
    float: left;
    margin-left:20px;
    font-size: 20px;
}

.navbar .title span {
    margin: 0 0 0 10px;
}

.navbar li a {
    display: block;
    color: white;
    text-align: center;
    padding: 10px;
    text-decoration: none;
}

.navbar li i {
    display: block;
    color: white;
    text-align: center;
    text-decoration: none;
}

.sticky-top {
  position: fixed;
  top: 0;
  width: 100%
}

#app > div.navbar.sticky-top > ul > li.left > a > span > svg {
    height:30px;
    width:30px;
    bottom:-14px !important;
    left: 5px;
}

#app > div.navbar.sticky-top > ul > li.right > a > span > svg {
    height:30px;
    width:30px;
    bottom:-14px !important;
    right:10px;
}

.box {
   display: flex;
   align-items:center;
}
 
</style>