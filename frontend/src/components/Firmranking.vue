<template>
  <div>
  <div v-if="loading">
    Loading firm ranking...
  </div>
  <div v-if="!loading">
    <p>Firm types ranked by total real dividends paid to consumer-shareholders.</p>
    <table class="agentlist">
      <tr>
        <td>Rank</td>
        <td>Firm</td>
        <td>Dividends</td>
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
  name: 'firmranking',
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
      `${config.apiURL}/firmranking?sim=${this.$route.query.sim}`,
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

