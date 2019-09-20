<template>
  <div>
    <h1>Simulations</h1>

    <div v-if="loading">Loading...</div>

    <ul class="linklist" v-if="!loading">
      <li v-for="sim in simulations">
        <router-link :to="{name: 'simulation', query: {sim: sim.path}}">{{ `${sim.owner} / ${sim.path}` }}</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import config from '../config';

export default {
  name: 'home',
  data() {
    return {
      loading: true,
      simulations: null,
    };
  },
  created() {
    // get simulations
    fetch(
      `${config.apiURL}/list`,
      config.xhrConfig,
    )
    .then(config.handleFetchErrors)
    .then(response => response.json())
    .then(
      (response) => {
        this.simulations = response.sims;
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

// $black:                                                 #000

</style>
