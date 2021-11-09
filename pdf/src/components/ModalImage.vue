<template>
  <div class="modal-mask">
    <div class="modal-wrapper" @contextmenu.prevent="$emit('close')">
      <div class="modal-container">
        <div class="close-btn">
          <button type="button" class="cancel-btn" @click="$emit('close')">
            &#10060;
          </button>
        </div>
        <div>
          <img class="full-size-img" :src="image" :alt="image" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ModalImage",
  emits: ["close"],
  props: {
    image: String,
  },
  data() {
    return {};
  },
  methods: {},
};
</script>

<style scoped>
span,
label,
button {
  font-size: 12pt;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  color: black;
}

.modal-mask {
  position: fixed;
  z-index: 9999;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: table;
  transition: opacity 0.3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.close-btn {
  position: absolute;
  right: 0;
  top: 0;
}

.modal-container {
  position: relative;
  max-width: 500px;
  margin: 0 auto;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  font-family: Helvetica, Arial, sans-serif;
  font-size: 16px;
  font-weight: 700;
  transition: opacity 0.3s ease;
}

.cancel-btn {
  cursor: pointer;
}

.full-size-img {
  width: 480px;
  height: 640px;
  border-radius: 2px;
}
</style>
