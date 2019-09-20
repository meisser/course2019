<template>
  <div id="childselection" :class="{context: true, childselection: true, in: show}">

    <h2 class="childselection__title">{{ childrenof }} on day {{ simulationday }}</h2>

    <div v-if="loading">Loading ...</div>

    <div v-if="!loading">
      <label><input type="checkbox" v-model="allSelected"></input> all</label>
      <ul class="nolist childselection__list">
        <li class="childselection__item" v-for="child in children">
          <label><input type="checkbox" :value="child" v-model="activeChildren"></input> {{ child }}</label>
        </li>
      </ul>
    </div>

    <el-button class="childselection__btn" v-if="!loading" @click="saveSelection">Save</el-button>
    <el-button class="childselection__btn" @click="cancelSelection">Cancel</el-button>
  </div>
</template>

<script>
import Vue from 'vue';
import { Button } from 'element-ui';
import config from '../config';

Vue.use(Button);

export default {
  name: 'childrenselection',
  props: ['show', 'childrenof', 'activenodes', 'simulationday', 'simulationid'],
  data() {
    return {
      loading: true,
      children: [],
      selectedChildren: [],
      allSelected: false,
    };
  },
  computed: {
    activeNodeArray() {
      return this.activenodes.split(',');
    },
  },
  watch: {
    show() {
      if (this.show) {
        // get children
        this.loading = true;

        fetch(
          `${config.apiURL}/children?sim=${this.simulationid}&day=${this.simulationday}&selection=${this.childrenof}`,
          config.xhrConfig,
        )
        .then(config.handleFetchErrors)
        .then(response => response.json())
        .then(
          (response) => {
            // set children
            this.children = [];
            response.children.forEach(child => this.children.push(child.label));

            // set activeChildren
            this.activeChildren = this.children.filter(x => this.activeNodeArray.includes(x));

            // set proper state of toggle all checkbox
            if (this.activeChildren.length === this.children.length) {
              this.allSelected = true;
            } else {
              this.allSelected = false;
            }

            this.loading = false;
          },
        )
        .catch(error => config.alertError(error));
      }
    },
    allSelected() {
      if (this.allSelected) {
        this.activeChildren = this.children;
      } else {
        this.activeChildren = [];
      }
    },
  },
  methods: {
    saveSelection() {
      // construct new set of active nodes
      // take all active nodes without children
      let newActiveNodes = this.activeNodeArray.filter(x => !this.children.includes(x));
      // push active children to new set
      newActiveNodes = newActiveNodes.concat(this.activeChildren);

      this.$emit('setactivenodes', newActiveNodes.join());

      this.$emit('update:show', false);
    },
    cancelSelection() {
      this.$emit('update:show', false);
    },
  },
};
</script>
<style lang="sass">
.childselection
  min-width: 280px
  padding: 20px 30px

  &__title
    margin: 0 0 10px

  &__list
    display: flex
    flex-wrap: wrap

  &__item
    display: inline-block

  &__btn
    margin-top: 20px
</style>
