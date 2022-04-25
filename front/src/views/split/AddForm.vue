<template>
  <div class="add-form-container">
    <div class="add-form-elem">
      <label>
        <span style="font-weight: bold"
          ><slot></slot>
          <prompt
            v-if="namespace"
            :prompt="$filters.localize('prompt-add-form-namespace')"
            >&#128269;</prompt
          >
          <prompt
            v-if="filename"
            :prompt="$filters.localize('prompt-add-form-filename')"
            >&#128269;</prompt
          >
        </span>
        <div style="display: flex; align-items: center">
          <input
            v-if="namespace"
            type="text"
            v-model="form.namespace"
            @input="(isRespBad = false), (isRespOk = false)"
          />
          <input
            v-if="filename"
            type="text"
            v-model="form.filename"
            @input="(isRespBad = false), (isRespOk = false)"
          />
          <span v-if="isRespOk">&#9989;</span>
          <span v-if="isRespBad"
            >&#10060;
            <prompt :prompt="badRespInfo">&#128269;</prompt>
          </span>
        </div>
      </label>
    </div>
    <div class="add-form-elem" v-if="namespace">
      <button type="button" class="btn menu-btn" @click="addNamespace">
        <span style="word-break: break-all">{{
          $filters.localize("btn-add")
        }}</span>
      </button>
    </div>
    <div
      v-if="filename"
      style="display: flex; flex-direction: column; align-items: center"
    >
      <div class="add-form-elem">
        <m-select
          :options="namespaces"
          :is-delete-enable="false"
          @selected="getSelectedNamespace"
        ></m-select>
      </div>
      <div class="add-form-elem">
        <button type="button" class="btn menu-btn" @click="addFilename">
          <span style="word-break: break-all">{{
            $filters.localize("btn-add")
          }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import MSelect from "./MSelect";
import Prompt from "../../components/Prompt";
import { bearer } from "../../utils/bearer";
import { getUrl } from "../../utils/url";

export default {
  name: "AddForm",
  components: { Prompt, MSelect },
  emits: ["filename", "namespace"],
  props: {
    namespaces: Array,
    namespace: {
      type: Boolean,
      default: false,
    },
    filename: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      form: {
        namespace: "",
        filename: "",
      },
      badRespInfo: "",
      isRespOk: false,
      isRespBad: false,
      showAddForm: false,
      addFilenameURL: "/naming/add-filename",
      addNamespaceURL: "/naming/add-namespace",
    };
  },
  methods: {
    getSelectedNamespace(option) {
      this.form.namespace = option;
    },
    addFilename() {
      if (this.form.filename.length < 3) {
        this.isRespBad = true;
        this.badRespInfo = this.$filters.localize(
          "validate-input-len-add-form"
        );
        return;
      }
      if (this.form.namespace.length === 0) {
        this.isRespBad = true;
        this.badRespInfo = this.$filters.localize(
          "validate-not-selected-namespace"
        );
        return;
      }
      axios
        .post(getUrl(this.addFilenameURL), this.form, {
          headers: {
            "Access-Control-Expose-Headers": "Content-Disposition",
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            this.isRespBad = false;
            this.isRespOk = true;
            this.$emit("filename", this.form);
            this.form.filename = "";
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.isRespBad = true;
            this.badRespInfo = error.message;
          } else {
            this.isRespBad = true;
            this.badRespInfo = error.response.data;
          }
        });
    },
    addNamespace() {
      if (this.form.namespace.length < 3) {
        this.isRespBad = true;
        this.badRespInfo = this.$filters.localize(
          "validate-input-len-add-form"
        );
        return;
      }
      axios
        .get(getUrl(this.addNamespaceURL), {
          params: { namespace: this.form.namespace },
          headers: {
            "Access-Control-Expose-Headers": "Content-Disposition",
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            this.isRespBad = false;
            this.isRespOk = true;
            this.$emit("namespace", this.form.namespace);
            this.form.namespace = "";
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.isRespBad = true;
            this.badRespInfo = error.message;
          } else {
            this.isRespBad = true;
            this.badRespInfo = error.response.data;
          }
        });
    },
  },
};
</script>

<style scoped>
span,
label,
button {
  font-size: 10pt;
  color: black;
}

input {
  display: block;
  width: 25em;
  padding: 0.6em 1.4em 0.5em 0.8em;
  background-color: rgba(211, 211, 211, 0.92);
  border: 0.1em solid #aaa;
  box-shadow: 0 1px 0 1px rgba(0, 0, 0, 0.04);
  border-radius: 0.1em;
}

.add-form-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.add-form-elem {
  margin: 0.1em 0;
}

.btn {
  display: inline-block;
  background-color: #ffffff;
  color: black;
  padding: 0.4em 0.8em;
  margin: 0.2em 0.1em;
  text-align: center;
  text-decoration: none;
  border: 0.1em solid black;
  border-radius: 0.1em;
  cursor: pointer;
  transition-duration: 0.5s;
}

.menu-btn:hover {
  background-color: darkseagreen;
  color: black;
}
</style>