<template>
  <div>

    <template v-for="agent in agents">
      <div class="minichart__wrapper" v-if="agent" :key="`minichart-${agent.id}`">
        <div class="minichart__title">{{ graphTitle[agent.id] }}</div>
        <div class="minichart" :id="`minichart-${agent.id}`"></div>
      </div>
    </template>

  </div>
</template>

<script>
// import * as d3 from 'd3';
import Plotly from 'plotly.js/dist/plotly';
import config from '../config';

export default {
  name: 'minicharts',
  props: ['agents', 'simulationday', 'simulationid'],
  data() {
    return {
      chartData: {},
      graphTitle: {},
    };
  },
  watch: {
    agents(newVal, oldVal) {
      // find new agents
      const newCharts = newVal.filter(i => oldVal.indexOf(i) < 0);
      // wrap in timeout to make sure that template is updated with new nodes
      // before we init graphs on them
      setTimeout(() => {
        newCharts.forEach(agent => this.initChart(agent));
        this.fetchCharts();
      }, 1);
    },
    simulationday() {
      this.fetchCharts();
    },
  },
  methods: {
    initChart(agent) {
      const layout = {
        autosize: true,
        width: 300,
        height: config.miniCharts.internalHeight,
        margin: {
          l: 40,
          r: 30,
          t: 10,
          b: 30,
        },
      };

      const data = [{
        x: [],
        y: [],
        mode: 'lines',
        line: {
          color: agent.color,
        },
        type: 'scatter',
      }];

      Plotly.newPlot(`minichart-${agent.id}`, data, layout, { displayModeBar: false });
    },
    fetchCharts() {
      // clear previous chartData
      this.chartData = {};

      // get chartData for each active agent
      this.agents.forEach((agent) => {
        fetch(
          `${config.apiURL}/minichart?sim=${this.simulationid}&day=${this.simulationday}&selection=${agent.id}&height=${config.miniCharts.height}`,
          config.xhrConfig,
        )
        .then(config.handleFetchErrors)
        .then(response => response.json())
        .then(
          (response) => {
            // we have to use vue's $set in order to trigger reactive updates in the view
            this.$set(this.chartData, agent.id, response);
            this.$set(this.graphTitle, agent.id, response.name);
          },
        )
        .then(
          () => {
            // rescale to max/min range
            const invheight = 1.0 / config.miniCharts.height;
            const range = this.chartData[agent.id].max - this.chartData[agent.id].min;
            const data = {
              x: [
                [...Array(this.chartData[agent.id].data.length).keys()].map(
                  i => this.simulationday - this.chartData[agent.id].data.length + 1 + i,
                ),
              ],
              y: [
                this.chartData[agent.id].data.map(
                  datapoint => (range * datapoint * invheight) + this.chartData[agent.id].min,
                ),
              ],
            };

            Plotly.update(`minichart-${agent.id}`, data);
          },
        )
        .catch(error => config.alertError(error));
      });
    },
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

.minichart

  &__title
    margin: 11px 0
    font: bold 14px/1.4 $avenir
    text-align: center

</style>
