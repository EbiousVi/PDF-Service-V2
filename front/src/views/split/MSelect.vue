<template>
  <modal v-if="warn" :message="warnInfo" @close="closeWarn"></modal>
  <div
    class="m-sel-container"
    :tabindex="0"
    @blur="blur"
    @focus="focus = true"
    :class="{ 'm-sel-m-sel-disabled': true }"
  >
    <div
      class="m-sel-value-container"
      :class="{
        'm-sel-value-container-focus': focus & (options.length !== 0),
        'm-sel-value-container-empty': options.length === 0,
      }"
      @click="openCloseOptions"
    >
      <div class="m-sel-value-div1">
        <span class="m-sel-value">{{ selectedOption }}</span>
      </div>
      <div class="m-sel-value-div2">
        <span class="m-sel-arrow-down" v-if="!showOptions">&#8744;</span>
        <span class="m-sel-arrow-up" v-if="showOptions">&#8743;</span>
      </div>
    </div>
    <div class="m-sel-option-container">
      <ul class="m-sel-options" v-show="showOptions">
        <li class="m-sel-option" v-for="option in getOptions" :key="option">
          <div class="m-sel-option-value" @click="selectOption(option)">
            <span class="m-sel-option-span">{{ option.value }}</span>
          </div>
          <div class="m-sel-option-menu" v-if="isDeleteEnable">
            <div class="m-sel-confirm" v-if="option.canDelete">
              <div>
                <span
                  class="m-sel-btn m-sel-btn-no"
                  @click.stop="deleteOption(option)"
                  >{{ $filters.localize("m-sel-ok") }}</span
                >
              </div>
              <div>
                <span
                  class="m-sel-btn m-sel-btn-yes"
                  @click.stop="undoDelete(option)"
                  >{{ $filters.localize("m-sel-undo") }}</span
                >
              </div>
            </div>
            <div class="m-sel-option-delete">
                   <div>
                <span class="m-sel-btn" @click.stop="confirmDelete(option)"
                  >&#10060;</span
                >
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Modal from "../../components/Modal";
import { bearer } from "../../utils/bearer";

export default {
  name: "m-select",
  components: { Modal },
  emits: ["selected", "deleted"],
  props: {
    options: {
      type: Array,
      required: true,
    },
    isDeleteEnable: {
      type: Boolean,
      default: true,
    },
    isNamespace: {
      type: Boolean,
      default: false,
    },
    selectedNamespace: {
      type: String,
    },
    isFilename: {
      type: Boolean,
      default: false,
    },
    selectedFilename: {
      type: String,
    },
  },
  watch: {
    options: {
      immediate: true,
      handler(cur, old) {
        if (old !== cur) {
          this.selectedOption = this.$filters.localize("m-sel-select");
        }
      },
    },
  },
  computed: {
    getOptions() {
      return this.options;
    },
  },
  data() {
    return {
      focus: false,
      showOptions: false,
      selectedOption: this.$filters.localize("m-sel-select"),
      warn: false,
      warnInfo: "",
    };
  },
  methods: {
    closeWarn() {
      this.warn = false;
    },
    confirmDelete(option) {
      if (option.canDelete) {
        option.canDelete = !option.canDelete;
      } else {
        option.canDelete = !option.canDelete;
      }
    },
    undoDelete(option) {
      option.canDelete = !option.canDelete;
    },
    deleteOption(option) {
      if (this.isNamespace) {
        let URL = "http://localhost:6060/naming/delete-namespace/".concat(
          option.value
        );
        this.delete(URL, option);
      } else {
        let URL = "http://localhost:6060/naming/delete-filename/"
          .concat(this.selectedNamespace)
          .concat("/")
          .concat(option.value);
        this.delete(URL, option);
      }
    },
    delete(URL, option) {
      axios
        .delete(URL, {
          headers: {
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            this.selectedOption = this.$filters.localize("m-sel-select");
            this.$emit("deleted", option);
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.warn = true;
            this.warnInfo = error.message;
          } else {
            this.warn = true;
            this.warnInfo = error.response.data;
          }
          this.showOptions = false;
        });
    },
    selectOption(option) {
      this.showOptions = false;
      this.selectedOption = option.value;
      this.$emit("selected", this.selectedOption);
    },
    openCloseOptions() {
      this.showOptions = !this.showOptions;
      this.options.forEach((el) => (el.canDelete = false));
    },
    blur() {
      this.showOptions = false;
      this.focus = false;
    },
  },
};
</script>

<style scoped>
span,
button {
  font-size: 12pt;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  color: black;
}

.m-sel-value-container-empty {
  pointer-events: none;
  opacity: 0.5;
  z-index: 10;
}

.m-sel-value-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 50em;
  min-height: 1.75em;
  background-color: #dddddd;
  border: 0.1em solid #aaa;
  box-shadow: 0 1px 0 1px rgba(0, 0, 0, 0.04);
  border-radius: 0.1em;
}

.m-sel-value-container-focus {
  border: 0.1em solid black;
  border-radius: 0.1em;
}

.m-sel-value {
  padding: 0;
  margin: 0;
  cursor: pointer;
}

.m-sel-options {
  overflow-y: auto;
  position: absolute;
  max-height: 20em;
  max-width: 50em;
  width: 100%;
  margin: 0;
  padding: 0;
  border: 0.1em solid #aaa;
  border-radius: 0.1em;
  z-index: 999990;
}

.m-sel-option {
  display: flex;
  justify-content: space-between;
  width: 50em;
  background-color: #dddddd;
  border-radius: 0.1em;
  outline: 0.03em solid #aaa;
}

.m-sel-option-value {
  padding-top: 0.5em;
  padding-bottom: 0.5em;
  width: 100%;
  text-align: left;
}

.m-sel-option-delete {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 0.7em;
}

.m-sel-confirm {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #dddddd;
}

.m-sel-btn {
  background-color: #ffffff;
  color: black;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  margin: 0.1em 0.2em;
  padding: 0.1em;
  transition-duration: 0.4s;
  cursor: pointer;
  border: 0.1em solid black;
  border-radius: 0.1em;
  top: 50%;
}

.m-sel-option-menu {
  display: flex;
  justify-content: flex-end;
}

.m-sel-option-span {
  display: inline-block;
  word-break: break-all;
}

.m-sel-option-value:hover {
  cursor: pointer;
  background-color: #bebebe;
}

.m-sel-btn:hover {
  background-color: black;
}

.m-sel-btn-yes:hover {
  background-color: greenyellow;
}

.m-sel-btn-no:hover {
  background-color: red;
}

.m-sel-arrow-down {
  text-align: right;
  transition: 0.5s;
}

.m-sel-value-div1 {
  padding: 0;
  text-align: center;
  flex: 0 0 49em;
}

.m-sel-value-div2 {
  text-align: center;
  flex: 0 0 1em;
}
</style>