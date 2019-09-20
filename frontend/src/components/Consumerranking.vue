<template>
  <div>
  <div v-if="loading">
    Loading consumer ranking...
  </div>
  <div v-if="!loading">
    <p>For configurations with immortal consumers, the consumer ranking is based on an exponentially moving average, measured at the last day of the simulation. If there are multiple instances of an agent type, the type score is the average score of its instances. For simulations with mortal consumers, the type score is based on the average life-time utility of all instances that died in the second half of the simulation.</p>
    <table class="agentlist" v-if="!loading">
      <tr>
        <td>Rank</td>
        <td>Consumer</td>
        <td>Utility</td>
        <td>Source</td>
        <td>Version</td>
      </tr>
      <tr v-for="(rank,index) in ranking">
        <td>{{index + 1}}</td>
        <td>{{`${rank.type}`}}</td>
        <td>{{`${rank.score}`}}</td>
        <td>
          <a :href="`${rank.url}`">source</a>
        </td>
        <td>{{`${rank.version}`}}</td>
      </tr>
    </table>
  </div>
  </div>
</template>

<script>
import config from '../config';

export default {
  name: 'consumerranking',
  props: ['simulationid'],
  data() {
    return {
      loading: true,
      ranking: null,
    };
  },
  created() {
    // get simulation ranking
    fetch(
      `${config.apiURL}/ranking?sim=${this.$route.query.sim}`,
      config.xhrConfig,
    )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
      (response) => {
        this.ranking = response.list;
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

.agentlist
  padding: 0

  tr:nth-child(even)
    background-color: #f2f2f2

  th, td
    padding: 10px
    text-align: left

  li
    text-align: left

</style>

