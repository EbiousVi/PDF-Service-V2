<template>
  <div>
    <p style="font-weight: bold">{{ $filters.localize("waiting") }}</p>
    <div class="loader"></div>
  </div>
</template>

<script>
export default {
  name: "Waiting",
};
</script>

<style scoped>
.loader {
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #b36ccb;
  width: 75px;
  height: 75px;
  -webkit-animation: spin 2s linear infinite; /* Safari */
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>