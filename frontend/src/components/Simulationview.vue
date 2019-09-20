<template>
  <div>
    <h1>Simulation '{{ this.$route.query.sim }}'</h1>
    <div v-if="loading">Loading...</div>
    <div v-if="!loading">This simulation is based on the
      <a :href="simInfo.configurationSourceURL">{{simInfo.configurationName}} configuration</a> and runs for {{simInfo.days}} days.</div>
    <h2>Ranking</h2>   
    <consumerranking :simulationid="simId"></consumerranking>
    <firmranking :simulationid="simId"></firmranking>
    <h2>Visualization</h2>
    <ul class="linklist" v-if="!loading">
      <li>
        <router-link :to="{name: 'trades', query: {sim: this.$route.query.sim, day: 0, selection: 'consumers,firms', step: 1}}">Trade</router-link>
      </li>
    </ul>
    <h2>Data</h2>
    <div class="chart-wrapper">
      <chart :simulationid="simId" :suggestedmetric="datametric" @updateUrl="updateUrl"></chart>
    </div>
  </div>
</template>

<script>
import Chart from '@/components/Chart';
import Consumerranking from '@/components/Consumerranking';
import Firmranking from '@/components/Firmranking';
import config from '../config';

export default {
  name: 'simulationview',
  components: {
    Consumerranking, Firmranking, Chart,
  },
  data() {
    return {
      loading: true,
      simDescription: '',
      simInfo: null,
      simId: this.$route.query.sim,
      datametric: this.$route.query.metric ? this.$route.query.metric : 'production',
    };
  },
  methods: {
    updateUrl(selection) {
      this.$router.replace({
        name: 'simulation',
        query: {
          sim: this.simId,
          metric: selection,
        },
      });
      this.datametric = selection;
    },
  },
  created() {
    // get simulation info
    fetch(
      `${config.apiURL}/info?sim=${this.$route.query.sim}`,
      config.xhrConfig,
    )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
      (response) => {
        this.simDescription = response.name;
        this.simInfo = response;
        this.loading = false;
      },
    )
      .catch(error => config.alertError(error));
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'
</style>

