<template>
  <div id="app">
    <ul class="homenav">
      <li><router-link :to="{name: 'home'}">Simulations</router-link></li>
    </ul>

    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'app',
};
</script>

<style lang="sass">
@import 'assets/sass/vars'
@import 'assets/sass/mixins'

body
  margin: 0
  font-family: 'Avenir', Helvetica, Arial, sans-serif
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
  color: #111

h1
  font: bold 40px/1 $avenir
  text-align: left

#app
  padding: 25px 50px

.nolist
  padding: 0
  margin: 0
  list-style-type: none

.homenav
  position: relative
  z-index: 20
  width: 50%
  padding: 0
  margin: 0
  list-style-type: none

  a
    color: $blue
    text-decoration: none
    &:hover
      color: $blue-hover

  li
    text-align: left

.context
  position: absolute
  left: -1000em
  top: 0
  z-index: 20
  display: inline-block
  max-width: 500px
  padding: 10px
  box-shadow: 0 2px 4px 0 rgba(0,0,0,.12), 0 0 6px 0 rgba(0,0,0,.04)
  border-radius: 3px
  background-color: white
  opacity: 0
  transition: opacity .2s
  text-align: left
  &.in
    opacity: 1

.form-control
  padding: .5rem .75rem
  font-size: 1rem
  line-height: 1.25
  color: #464a4c
  background-color: #fff
  background-image: none
  background-clip: padding-box
  border: 1px solid rgba(0,0,0,.15)
  border-radius: .25rem
  transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s
  &:focus
    color: #464a4c
    background-color: #fff
    border-color: #5cb3fd
    outline: 0

.linklist
  padding: 0
  margin: 0
  list-style-type: none

  li
    text-align: left

  a
    +link-styling
</style>
