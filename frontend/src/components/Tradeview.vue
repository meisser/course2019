 <template>
  <div class="tradeview">
    <h1>Tradeview</h1>
    <p>This view visualizes the trading of goods between consumers and firms in a sequence economy.</p>
    <div class="tradeview__wrapper" v-if="loaded">
      <div class="tradeview__main">
        <div class="controls">
          <el-button type="primary" v-if="simDay < simLength" @click="playing = !playing">
            <svg v-if="playing" width="9" height="9" viewBox="0 0 6 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M0 0h2v9H0zM4 0h2v9H4z"/></svg>
            <svg v-if="!playing" width="9" height="9" viewBox="0 0 9 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M.986 0v8.935L9 4.077z" /></svg>
          </el-button>
          <el-slider v-model="simDay" :max="simLength" :step="simStep" show-input></el-slider>
          <el-select v-model="simStep">
            <el-option v-for="item in config.stepSizeOptions" :key="item" :value="item"></el-option>
          </el-select>
        </div>
        <!-- <div class="controls controls--ur">
          <el-dropdown trigger="click" @command="handleDownload">
            <el-button type="primary">
              Download Metric<i class="el-icon-caret-bottom el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <template v-for="option in metrics">
                <el-dropdown-item :command="option">{{ option }}</el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </el-dropdown>
        </div> -->
        <tradegraph class="tradeview__tradechart" :graphdata="tradeGraphData" :selectednode="selectedNode" @simstopped="handleSimStopped" @nodeclicked="handleNodeClicked" @addminichart="handleAddMinichart" @showinfo="handleShowInfo" @showchildren="handleShowChildSelection" @hidecontextmenus="hideContextMenus"></tradegraph>
      </div>
      <div class="tradeview__side">
        <div class="tradeview__minicharts">
          <div class="tradeview__minicharts-wrapper">
            <minicharts :agents="miniCharts" :simulationday="simDay" :simulationid="simId"></minicharts>
          </div>
        </div>
      </div>
      <childselection :show.sync="showChildSelection" :childrenof="childrenOf" :activenodes="simAgents" :simulationday="simDay" :simulationid="simId" @setactivenodes="handleSetActiveNodes" :style="{left: `${childSelectionLeft}px`, top: `${childSelectionTop}px`}"></childselection>
      <nodeinfo :show.sync="showNodeInfo" :agent="infoOf" :simulationday="simDay" :simulationid="simId" :style="{left: `${infoLeft}px`, top: `${infoTop}px`}"></nodeinfo>
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import { Button, Dropdown, DropdownMenu, DropdownItem, Option, Select, Slider } from 'element-ui';
// import Plotly from 'plotly.js/dist/plotly';
import Tradegraph from '@/components/Tradegraph';
import Childselection from '@/components/Childselection';
import Nodeinfo from '@/components/Nodeinfo';
import Minicharts from '@/components/Minicharts';
import config from '../config';

Vue.use(Button);
Vue.use(Dropdown);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);
Vue.use(Option);
Vue.use(Select);
Vue.use(Slider);

export default {
  name: 'tradeview',
  components: {
    Tradegraph,
    Childselection,
    Nodeinfo,
    Minicharts,
  },
  data() {
    return {
      config,
      tradeGraphData: null,
      loaded: false,
      playing: false,
      playInterval: null,
      metrics: [],
      selectedNode: this.$route.query.selected,
      miniCharts: [],
      showNodeInfo: false,
      infoOf: null,
      infoLeft: -10000,
      infoTop: 0,
      showChildSelection: false,
      childSelectionLeft: -10000,
      childSelectionTop: 0,
      childrenOf: null,
      simId: this.$route.query.sim,
      simDay: parseInt(this.$route.query.day, 10),
      simAgents: this.$route.query.selection,
      simStep: parseInt(this.$route.query.step, 10),
      simLength: null,
    };
  },
  created() {
    // get simulation data
    this.fetchData();

    // get length of simulation
    fetch(
      `${config.apiURL}/info?sim=${this.simId}`,
      config.xhrConfig,
    )
    .then(config.handleFetchErrors)
    .then(response => response.json())
    .then(
      (response) => {
        this.simLength = response.days;
      },
    )
    .catch(error => config.alertError(error));

    // get download options
    fetch(
      `${config.apiURL}/metrics`,
      config.xhrConfig,
    )
    .then(config.handleFetchErrors)
    .then(response => response.json())
    .then(
      (response) => {
        response.metrics.forEach((element) => {
          this.metrics.push(element);
        });
      },
    )
    .catch(error => config.alertError(error));
  },
  watch: {
    // call fetchData when the route changes
    $route: 'fetchData',
    playing() {
      if (this.playing) {
        this.playInterval = setInterval(this.nextStep, 100);
      } else {
        clearInterval(this.playInterval);
      }
    },
    simDay() {
      this.goToNewURL();
    },
    simAgents() {
      this.goToNewURL();
    },
    simStep() {
      this.goToNewURL();
    },
    showNodeInfo() {
      if (!this.showNodeInfo) {
        this.infoLeft = -10000;
        this.selectedNode = false;
        this.goToNewURL();
      }
    },
    showChildSelection() {
      if (!this.showChildSelection) {
        this.childSelectionLeft = -10000;
        this.selectedNode = false;
        this.goToNewURL();
      }
    },
  },
  methods: {
    nextStep() {
      this.simDay = Math.min(this.simDay + this.simStep, this.simLength);
      if (this.simDay === this.simLength) {
        this.playing = false;
      }
    },
    prevStep() {
      this.simDay = Math.max(this.simDay - this.simStep, 0);
    },
    goToNewURL() {
      this.hideContextMenus();
      this.$router.replace({
        name: 'trades',
        query: {
          sim: this.simId,
          day: this.simDay,
          selection: this.simAgents,
          step: this.simStep,
          selected: this.selectedNode,
        },
      });
    },
    fetchData() {
      // fetchData has all needed state data in URL
      fetch(
        `${config.apiURL}/tradegraph?sim=${this.simId}&day=${this.simDay}&selection=${this.simAgents}&step=${this.simStep}`,
        config.xhrConfig,
      )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
        (response) => {
          this.tradeGraphData = response;
          this.loaded = true;

          // check if we got new hints on what nodes to add
          if (response.hint.length > 0) {
            this.simAgents += `,${response.hint.join()}`;
          }
        },
      )
      .catch(error => config.alertError(error));
    },
    handleDownload(item) {
      window.open(`${config.apiURL}/downloadcsv?metric=${item}&sim=${this.simId}&day=${this.simDay}&selection=${this.simAgents}&step=${this.simStep}`, '_blank');
    },
    handleNodeClicked(node) {
      this.playing = false;
      if (this.selectedNode && this.selectedNode === node) {
        this.selectedNode = null;
        this.showchildren = false;
        this.showinfo = false;
      } else {
        this.selectedNode = node;
      }
      this.goToNewURL();
    },
    handleAddMinichart(node, coloring) {
      // remove chart of node if it is already there
      this.miniCharts = this.miniCharts.filter(el => el.id !== node);
      // remove last chart if there would be more than configured
      if (this.miniCharts.length >= config.miniCharts.noOfChartsInSidebar) {
        this.miniCharts.pop();
      }
      // add chart to the top of the list
      this.miniCharts.unshift({ id: node, color: coloring });
    },
    handleShowInfo(node, coordinates) {
      this.hideContextMenus();

      this.infoOf = node;
      this.infoLeft = coordinates.x;
      this.infoTop = coordinates.y;
      this.showNodeInfo = true;
    },
    handleShowChildSelection(node, coordinates) {
      this.hideContextMenus();

      this.childrenOf = node;
      this.childSelectionLeft = coordinates.x;
      this.childSelectionTop = coordinates.y;
      this.showChildSelection = true;
    },
    handleSetActiveNodes(nodes) {
      this.simAgents = nodes;
    },
    handleSimStopped() {
      this.playing = false;
    },
    hideContextMenus() {
      this.showNodeInfo = false;
      this.showChildSelection = false;
    },
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

$white:                                                 #fff

.tradeview
  display: flex
  flex-direction: column

  &__wrapper
    display: flex

  &__main
    position: relative
    z-index: 10
    flex-basis: 0
    flex-grow: 1

  &__side
    position: relative
    z-index: 20
    flex-basis: 300px

  &__minicharts

    &-wrapper
      min-height: 300px
      padding: 0 10px 20px
      border: 1px solid $border-color
      border-radius: 5px
      background-color: rgba($white, .95)

  &__tradechart
    position: fixed
    left: 0
    right: 0
    top: 0
    bottom: 0
    width: 100%
    height: 100%

.sidebar
  flex-basis: 300px
  box-sizing: border-box
  border: 3px solid black

.controls
  position: relative
  z-index: 20
  display: flex

  > div,
  > button
    margin-right: 10px

  .el-select input
    width: 80px

  .el-input--small .el-input__inner
    height: 36px

  &__title
    margin: 11px 0
    font: 500 14px/1.4 $avenir

  &--ur
    position: fixed
    z-index: 20
    right: 50px
    top: 25px

    > div,
    > button
      margin-right: 0

.el-slider
  width: 413px

  &__input
    margin-top: 0

.el-input-number--small .el-input-number__decrease,
.el-input-number--small .el-input-number__increase
  line-height: 34px
</style>
