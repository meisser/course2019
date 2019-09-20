<template>
  <div class="chart__wrapper">
    Metric:
    <el-dropdown trigger="click" @command="updateMetric">
      <el-button type="primary">{{`${suggestedmetric}`}}<i class="el-icon-caret-bottom el-icon--right"></i>
      </el-button>
      <el-dropdown-menu slot="dropdown">
        <template v-for="option in metrics">
          <el-dropdown-item :command="option">{{ option }}</el-dropdown-item>
        </template>
      </el-dropdown-menu>
    </el-dropdown>
    <el-button v-on:click="download" type="primary">Download</el-button>
    <p>{{`${description}`}}</p>
    <template v-for="(singleSerie,sIndex) in selectedSeries">
      <el-dropdown trigger="click" @command="updateSeries">
        <el-button>{{`${singleSerie}`}}<i class="el-icon-caret-bottom el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <template v-for="option in series">
            <el-dropdown-item :command="[sIndex,option]">{{ option }}</el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </el-dropdown>
    </template>
    <el-button v-if="renderRefresh" v-on:click="updateChart">Refresh</el-button>
    <el-button v-if="renderRemove" v-on:click="removeSeries">Remove</el-button>
    <el-button v-if="renderAdd" v-on:click="addSeries">Add</el-button>
    <div class="chart" id="chart"></div>
  </div>
</template>

<script>
import Vue from 'vue';
import { Button, Dropdown, DropdownMenu, DropdownItem } from 'element-ui';
import Plotly from 'plotly.js/dist/plotly';
import config from '../config';

/* function someFunction() {
  Plotly.newPlot('qweqwe', );
} */

Vue.use(Button);
Vue.use(Dropdown);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);

export default {
  name: 'chart',
  props: ['simulationid', 'suggestedmetric'],
  watch: {
    suggestedmetric() {
      this.selectedSeries = ['Loading...'];
      this.updateChart();
    },
    selectedSeries() {
      this.renderRemove = this.selectedSeries.length > 1;
      this.renderAdd = this.selectedSeries.length < this.series.length;
    },
  },
  data() {
    return {
      metrics: [],
      description: '',
      selectedSeries: ['Loading...'],
      series: [],
      renderAdd: false,
      renderRemove: false,
      renderRefresh: false,
    };
  },
  created() {
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

    this.updateChart();
  },
  mounted() {
    this.initChart();
  },
  methods: {
    updateMetric(item) {
      this.$emit('updateUrl', item);
    },
    updateSeries(item) {
      const index = item[0];
      this.selectedSeries[index] = item[1];
      /* window.alert(item); */
      this.updateChart();
    },
    addSeries() {
      const sizeBefore = this.selectedSeries.length;
      const index = this.series.indexOf(this.selectedSeries[sizeBefore - 1]) + 1;
      const candidate = this.series[index];
      if (index < this.series.length && !this.selectedSeries.includes(candidate)) {
        this.selectedSeries.push(candidate);
      } else {
        this.series.forEach((element) => {
          if (sizeBefore === this.selectedSeries.length) {
            if (!this.selectedSeries.includes(element)) {
              this.selectedSeries.push(element);
            }
          }
        });
      }
      this.updateChart();
    },
    removeSeries() {
      this.selectedSeries.pop();
      this.updateChart();
    },
    download() {
      window.open(`${config.apiURL}/downloadcsv?metric=${this.suggestedmetric}&sim=${this.simulationid}`, '_blank');
    },
    initChart() {
      const layout = {
        autosize: true,
        width: 1000,
        height: 500,
        margin: {
          l: 50,
          r: 50,
          t: 10,
          b: 10,
        },
      };

      const data = [{
        x: [0.0, 100.0],
        y: [1.0, 1.0],
        mode: 'lines',
       /*  line: {
          color: agent.color,
        }, */
        type: 'scatter',
      }];

      Plotly.newPlot('chart', data, layout, { displayModeBar: false });
    },
    updateChart() {
      fetch(
        `${config.apiURL}/chart?sim=${this.simulationid}&metric=${this.suggestedmetric}&row=${this.selectedSeries.join(',')}`,
          config.xhrConfig,
        )
        .then(config.handleFetchErrors)
        .then(response => response.json())
        .then(
          (response) => {
            this.description = response.description;
            this.series = response.options;
            const newSelection = [];
            const data = [];
            response.data.forEach((line) => {
              data.push({
                x: line.xs,
                y: line.ys,
                name: line.name,
                mode: 'lines',
                type: 'scatter',
              });
              newSelection.push(line.name);
            });
            const layout = {
              autosize: true,
              margin: {
                l: 50,
                r: 50,
                t: 10,
                b: 50,
              },
            };
            this.renderRefresh = !response.complete;
            this.selectedSeries = newSelection;
            Plotly.newPlot('chart', data, layout);
          },
        )
        .catch(error => config.alertError(error));
    },
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

.chart

  &__title
    margin: 11px 0
    font: bold 14px/1.4 $avenir
    text-align: center

</style>
