<template>
  <div>
    <div class="services">
      <div class="service" @click="$router.push('/split')">
        <span class="service-name">{{ $filters.localize("split.split-btn") }}</span>
      </div>
      <div class="service" @click="$router.push('/merge')">
        <span class="service-name">{{ $filters.localize("merge.merge-btn") }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Main",
  components: {}
};
</script>

<style scoped>
.services {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  height: 10em;
  border-radius: 0.3em;
}

.service {
  height: 100%;
  width: 48%;
  margin-right: 0.3em;
  padding-top: 5em;
  background-color: lavender;
  cursor: pointer;
  text-align: center;
  border-radius: 0.3em;
}

.service:hover {
  background-color: #d2c3ec;
}

.service-name {
  font-size: 24pt;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  font-weight: bold;
}
</style>